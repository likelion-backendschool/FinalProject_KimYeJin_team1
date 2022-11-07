package com.yejin.exam.wbook.domain.mybook.controller;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.mybook.entity.MyBook;
import com.yejin.exam.wbook.domain.mybook.service.MyBookService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/myBooks")
@RequiredArgsConstructor
@Slf4j
public class MyBookController {
    private final MyBookService myBookService;
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultResponse> list(@AuthenticationPrincipal MemberContext memberContext) {
        Optional<List<MyBook>> oMyBooks = myBookService.findByOwner(memberContext.getId());
        if(!oMyBooks.isPresent()){
            return Util.spring.responseEntityOf(ResultResponse.failOf("GET_MYBOOKS_FAILED","mybooks이 없습니다.",null));
        }
        List<MyBook> myBooks = oMyBooks.get();
        return Util.spring.responseEntityOf(ResultResponse.successOf("GET_MYBOOKS_OK","",myBooks));
    }
}
