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
import com.yejin.exam.wbook.global.exception.ActorCanNotModifyException;
import com.yejin.exam.wbook.global.exception.ActorCanNotRemoveException;
import com.yejin.exam.wbook.global.request.Rq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

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
    public String showCreate(Principal principal,Model model) {
        Long memberId = memberService.findByUsername(principal.getName()).get().getId();
        List<PostKeyword> postKeywords = postKeywordService.findByMemberId(memberId);
//        log.debug("[product] rq.getId : "+rq.getMember().getNickname());
        log.debug("[product] principal : "+principal.getName());
        log.debug("[product] postKeywords : "+postKeywords);
        model.addAttribute("postKeywords", postKeywords);
        return "product/create";
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @PostMapping("/create")
    public String create(@Valid ProductDto productDto) {
        Member author = rq.getMember();
        Product product = productService.create(author, productDto.getSubject(), productDto.getPrice(), productDto.getPostKeywordId(), productDto.getProductTagContents());
        return "redirect:/product/" + product.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findForPrintById(id).get();
        List<Post> posts = productService.findPostsByProduct(product);

        model.addAttribute("product", product);
        model.addAttribute("posts", posts);

        return "product/detail";
    }

    @GetMapping("/list")
    public String list(Model model) {
        List<Product> products = productService.findAllForPrintByOrderByIdDesc(rq.getMember());

        model.addAttribute("products", products);

        return "product/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@PathVariable long id, Model model) {
        Product product = productService.findForPrintById(id).get();

        Member actor = rq.getMember();

        if (productService.actorCanModify(actor, product) == false) {
            throw new ActorCanNotModifyException();
        }

        model.addAttribute("product", product);

        return "product/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@Valid ProductModifyDto productModifyDto, @PathVariable long id) {
        Product product = productService.findById(id).get();
        Member actor = rq.getMember();

        if (productService.actorCanModify(actor, product) == false) {
            throw new ActorCanNotModifyException();
        }

        productService.modify(product, productModifyDto.getSubject(), productModifyDto.getPrice(), productModifyDto.getProductTagContents());
        return Rq.redirectWithMsg("/product/" + product.getId(), "%d번 도서 상품이 수정되었습니다.".formatted(product.getId()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/remove")
    public String remove(@PathVariable long id) {
        Product post = productService.findById(id).get();
        Member actor = rq.getMember();

        if (productService.actorCanRemove(actor, post) == false) {
            throw new ActorCanNotRemoveException();
        }

        productService.remove(post);

        return Rq.redirectWithMsg("/post/list", "%d번 글이 삭제되었습니다.".formatted(post.getId()));
    }

    @GetMapping("/tag/{tagContent}")
    public String tagList(Model model, @PathVariable String tagContent) {
        List<ProductTag> productTags = productService.getProductTags(tagContent, rq.getMember());

        model.addAttribute("productTags", productTags);
        return "product/tagList";
    }
}