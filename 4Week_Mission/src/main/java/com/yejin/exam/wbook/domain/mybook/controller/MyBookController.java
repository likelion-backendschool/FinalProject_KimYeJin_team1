package com.yejin.exam.wbook.domain.mybook.controller;

import com.yejin.exam.wbook.domain.mybook.dto.MyBookDto;
import com.yejin.exam.wbook.domain.mybook.entity.MyBook;
import com.yejin.exam.wbook.domain.mybook.service.MyBookService;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.product.service.ProductService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.Optional;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Api(tags = "게시물 API")
@RestController
@RequestMapping("/api/v1/myBooks")
@RequiredArgsConstructor
@Slf4j
public class MyBookController {
    private final MyBookService myBookService;
    private final ProductService productService;
    @ApiOperation(value = "My Book 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 구매 도서 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 구매 도서를 찾을 수 없습니다.\n"
                    + "M001 - 유효하지 않은 사용자 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultResponse> list(@AuthenticationPrincipal MemberContext memberContext) {
        Optional<List<MyBook>> oMyBooks = myBookService.findByOwner(memberContext.getId());
        if(!oMyBooks.isPresent()){
            return Util.spring.responseEntityOf(ResultResponse.failOf("F001","구매 도서를 찾을 수 없습니다.",null));
        }
        List<MyBook> myBooks = oMyBooks.get();
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","구매 도서 조회에 성공하였습니다.",myBooks));
    }
    @ApiOperation(value = "My Book 상세")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 구매 도서 상세 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 존재하지 않는 도서입니다.\n"
                    + "M001 - 유효하지 않은 사용자 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultResponse> detail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id) {
        Optional<MyBook> oMyBook = myBookService.findById(id);
        if(!oMyBook.isPresent()){
            return Util.spring.responseEntityOf(ResultResponse.failOf("FOO1", "존재하지 않는 도서입니다.",null));
        }
        MyBook myBook = oMyBook.get();
        List<Post> bookChapters = productService.findPostsByProduct(myBook.getProduct());
        MyBookDto myBookDto = new MyBookDto(myBook,myBook.getProduct(),bookChapters);

        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","구매 도서 상세 조회에 성공하였습니다.", myBookDto));

    }
}
