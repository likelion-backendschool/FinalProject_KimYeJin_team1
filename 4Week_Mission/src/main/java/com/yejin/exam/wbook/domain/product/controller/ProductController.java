package com.yejin.exam.wbook.domain.product.controller;


import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
import com.yejin.exam.wbook.domain.post.service.PostKeywordService;
import com.yejin.exam.wbook.domain.product.dto.ProductDto;
import com.yejin.exam.wbook.domain.product.dto.ProductModifyDto;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.domain.product.entity.ProductTag;
import com.yejin.exam.wbook.domain.product.service.ProductService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.exception.ActorCanNotModifyException;
import com.yejin.exam.wbook.global.exception.ActorCanNotRemoveException;
import com.yejin.exam.wbook.global.request.Rq;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
@Api(tags = "도서 API")
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final PostKeywordService postKeywordService;
    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @GetMapping("/create")
    public String showCreate(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Long memberId = memberContext.getId();
        List<PostKeyword> postKeywords = postKeywordService.findByMemberId(memberId);
//        log.debug("[product] rq.getId : "+rq.getMember().getNickname());
        log.debug("[product] postKeywords : "+postKeywords);
        model.addAttribute("postKeywords", postKeywords);
        return "product/create";
    }
    @ApiOperation(value = "도서 생성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 도서가 생성되었습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal MemberContext memberContext,@Valid ProductDto productDto) {
//        Member author = rq.getMember();
        Member author = memberContext.getMember();
        Product product = productService.create(author, productDto.getSubject(), productDto.getPrice(), productDto.getPostKeywordId(), productDto.getProductTagContents());
        return "redirect:/product/" + product.getId();
    }
    @ApiOperation(value = "도서 상세 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 도서 상세 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 도서를 찾을 수 없습니다"),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @ApiImplicitParam(name = "id", value = "도서 PK", example = "1", required = true)
    @GetMapping("/{id}")
    public String detail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id, Model model) {
        Member author = memberContext.getMember();

        Product product = productService.findForPrintById(id).get();
        List<Post> posts = productService.findPostsByProduct(product);

        model.addAttribute("product", product);
        model.addAttribute("posts", posts);
        model.addAttribute("author", author);

        return "product/detail";
    }
    @ApiOperation(value = "도서 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 도서 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 도서 조회할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member author = memberContext.getMember();
        List<Product> products = productService.findAllForPrintByOrderByIdDesc(author);

        model.addAttribute("products", products);

        return "product/list";
    }

    @GetMapping("/{id}/modify")
    public String showModify(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model) {
        Product product = productService.findForPrintById(id).get();

//        Member actor = rq.getMember();

        Member actor = memberContext.getMember();
        if (productService.actorCanModify(actor, product) == false) {
            throw new ActorCanNotModifyException();
        }

        model.addAttribute("product", product);

        return "product/modify";
    }
    @ApiOperation(value = "도서 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 도서가 수정되었습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @ApiImplicitParam(name = "id", value = "도서 PK", example = "1", required = true)
    @PostMapping("/{id}/modify")
    public String modify(@AuthenticationPrincipal MemberContext memberContext, @Valid ProductModifyDto productModifyDto, @PathVariable long id) {
        Product product = productService.findById(id).get();
//        Member actor = rq.getMember();
        Member actor = memberContext.getMember();

        if (productService.actorCanModify(actor, product) == false) {
            throw new ActorCanNotModifyException();
        }

        productService.modify(product, productModifyDto.getSubject(), productModifyDto.getPrice(), productModifyDto.getProductTagContents());
        return Rq.redirectWithMsg("/product/" + product.getId(), "%d번 도서 상품이 수정되었습니다.".formatted(product.getId()));
    }
    @ApiOperation(value = "도서 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 도서가 삭제되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 도서를 찾을 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @ApiImplicitParam(name = "id", value = "도서 PK", example = "1", required = true)
    @PostMapping("/{id}/remove")
    public String remove(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Product product = productService.findById(id).get();
//        Member actor = rq.getMember();
        Member actor = memberContext.getMember();

        if (productService.actorCanRemove(actor, product) == false) {
            throw new ActorCanNotRemoveException();
        }

        productService.remove(product);

        return Rq.redirectWithMsg("/product/list", "%d번 도서가 삭제되었습니다.".formatted(product.getId()));
    }
    @ApiOperation(value = "도서 태그 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 도서 태그 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 도서 태그 조회할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @ApiImplicitParam(name = "tagContent", value = "태그", example = "#태그1", required = true)
    @GetMapping("/tag/{tagContent}")
    public String tagList(@AuthenticationPrincipal MemberContext memberContext, Model model, @PathVariable String tagContent) {
        Member author = memberContext.getMember();

        List<ProductTag> productTags = productService.getProductTags(tagContent, author);

        model.addAttribute("productTags", productTags);
        return "product/tagList";
    }
}