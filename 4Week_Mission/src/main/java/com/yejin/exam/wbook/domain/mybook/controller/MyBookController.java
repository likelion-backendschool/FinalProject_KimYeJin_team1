package com.yejin.exam.wbook.domain.mybook.controller;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.mybook.dto.MyBookDto;
import com.yejin.exam.wbook.domain.mybook.entity.MyBook;
import com.yejin.exam.wbook.domain.mybook.service.MyBookService;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.domain.product.service.ProductService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
@RequestMapping("/api/v1/myBooks")
@RequiredArgsConstructor
@Slf4j
public class MyBookController {
    private final MyBookService myBookService;
    private final ProductService productService;
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultResponse> list(@AuthenticationPrincipal MemberContext memberContext) {
        Optional<List<MyBook>> oMyBooks = myBookService.findByOwner(memberContext.getId());
        if(!oMyBooks.isPresent()){
            return Util.spring.responseEntityOf(ResultResponse.failOf("GET_MYBOOKS_FAILED","mybooks이 없습니다.",null));
        }
        List<MyBook> myBooks = oMyBooks.get();
        return Util.spring.responseEntityOf(ResultResponse.successOf("GET_MYBOOKS_OK","성공",myBooks));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultResponse> detail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id) {
        Optional<MyBook> oMyBook = myBookService.findById(id);
        if(!oMyBook.isPresent()){
            return Util.spring.responseEntityOf(ResultResponse.failOf("GET_MYBOOK_FAILED", "해당하는 도서가 존재하지 않습니다.",null));
        }
        MyBook myBook = oMyBook.get();
        List<Post> bookChapters = productService.findPostsByProduct(myBook.getProduct());
        MyBookDto myBookDto = new MyBookDto(myBook,myBook.getProduct(),bookChapters);

        return Util.spring.responseEntityOf(ResultResponse.successOf("GET_MYBOOK_OK","성공", myBookDto));

    }
}
