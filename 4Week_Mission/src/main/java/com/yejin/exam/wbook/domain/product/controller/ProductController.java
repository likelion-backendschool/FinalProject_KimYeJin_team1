package com.yejin.exam.wbook.domain.product.controller;


import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.mybook.dto.MyBookDto;
import com.yejin.exam.wbook.domain.order.service.OrderService;
import com.yejin.exam.wbook.domain.post.dto.BookChapterDto;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
import com.yejin.exam.wbook.domain.post.service.PostKeywordService;
import com.yejin.exam.wbook.domain.product.dto.*;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.domain.product.entity.ProductTag;
import com.yejin.exam.wbook.domain.product.service.ProductService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.exception.ActorCanNotModifyException;
import com.yejin.exam.wbook.global.exception.ActorCanNotRemoveException;
import com.yejin.exam.wbook.global.request.Rq;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = "도서 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;
    private final PostKeywordService postKeywordService;
    private final MemberService memberService;
    private final Rq rq;

    @ApiOperation(value = "도서 생성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 도서가 생성되었습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @PreAuthorize("hasAuthority('AUTHOR')")
    @PostMapping("/create")
    public ResponseEntity<ResultResponse> create(@AuthenticationPrincipal MemberContext memberContext, @Valid @RequestBody ProductDto productDto) {
        Member author = memberContext.getMember();
        Product product = productService.create(author, productDto.getSubject(), productDto.getPrice(), productDto.getPostKeywordId(), productDto.getProductTagContents());
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d번 도서가 생성되었습니다.", product));
    }
    @ApiOperation(value = "도서 상세 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 도서 상세 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 도서를 찾을 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @ApiImplicitParam(name = "id", value = "도서 PK", example = "1", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> detail(@PathVariable Long id) {

        Optional<Product> oProduct = productService.findForPrintById(id);
        if(!oProduct.isPresent()){
            return Util.spring.responseEntityOf(ResultResponse.successOf("FOO1","도서를 찾을 수 없습니다.",null));
        }
        Product product = oProduct.get();
        List<Post> posts = productService.findPostsByProduct(product);
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d건의 정산품목을 정산처리하였습니다.",new ProductBookChaptersDto(product,posts)));
    }
    @ApiOperation(value = "도서 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 도서 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 도서 조회할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @GetMapping("/list")
    public ResponseEntity<ResultResponse> list(@AuthenticationPrincipal MemberContext memberContext) {
        Member author = memberContext.getMember();
        List<Product> products = productService.findAllForPrintByOrderByIdDesc(author);
        List<ProductTagsDto> productTagsDtos = new ArrayList<>();
        products.stream()
                .map(product -> new ProductTagsDto(product))
                .forEach(productTagsDto -> productTagsDtos.add(productTagsDto));
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","도서 조회에 성공하였습니다.",productTagsDtos));
    }

    @ApiOperation(value = "도서 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 도서 상품이 수정되었습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다.\n"
                    + "F001 - 도서를 찾을 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @ApiImplicitParam(name = "id", value = "도서 PK", example = "1", required = true)
    @PostMapping("/{id}/modify")
    public ResponseEntity<ResultResponse> modify(@AuthenticationPrincipal MemberContext memberContext, @Valid @RequestBody ProductModifyDto productModifyDto, @PathVariable long id) {
        Optional<Product> oProduct = productService.findForPrintById(id);
        if(!oProduct.isPresent()){
            return Util.spring.responseEntityOf(ResultResponse.successOf("FOO1","도서를 찾을 수 없습니다.",null));
        }

        Product product = oProduct.get();
        Member actor = memberContext.getMember();
        if (productService.actorCanModify(actor, product) == false) {
            throw new ActorCanNotModifyException();
        }
        productService.modify(product, productModifyDto.getSubject(), productModifyDto.getPrice(), productModifyDto.getProductTagContents());
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d번 도서 상품이 수정되었습니다.",product));
    }
    @ApiOperation(value = "도서 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 도서가 삭제되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 도서를 찾을 수 없습니다.\n"
                    + "FOO2 - 주문 목록에 포함되어 있는 상품입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @ApiImplicitParam(name = "id", value = "도서 PK", example = "1", required = true)
    @PostMapping("/{id}/remove")
    public ResponseEntity<ResultResponse> remove(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Optional<Product> oProduct = productService.findForPrintById(id);
        if(!oProduct.isPresent()){
            return Util.spring.responseEntityOf(ResultResponse.successOf("FOO1","도서를 찾을 수 없습니다.",null));
        }

        Product product = oProduct.get();
        Member actor = memberContext.getMember();
        if (productService.actorCanRemove(actor, product) == false) {
            throw new ActorCanNotRemoveException();
        }
        if(!productService.remove(product)){
            return Util.spring.responseEntityOf(ResultResponse.successOf("F002","주문 목록에 포함되어 있는 상품입니다.",null));
        }
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d번 도서가 삭제되었습니다.",id));
    }
    @ApiOperation(value = "도서 태그 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 도서 태그 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 도서 태그 조회할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 작가 권한이 필요한 화면입니다.")
    })
    @ApiImplicitParam(name = "tagContent", value = "태그", example = "IT", required = true)
    @GetMapping("/tag/{tagContent}")
    public ResponseEntity<ResultResponse> tagList(@AuthenticationPrincipal MemberContext memberContext, @PathVariable String tagContent) {
        Member author = memberContext.getMember();
        List<ProductTag> productTags = productService.getProductTags(tagContent, author);
        List<ProductTagDto> productTagDtos = new ArrayList<>();
        productTags.stream()
                .map(productTag -> new ProductTagDto(productTag))
                .forEach(productTagDto -> productTagDtos.add(productTagDto));
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","도서 태그 조회에 성공하였습니다.",productTagDtos));
    }
}