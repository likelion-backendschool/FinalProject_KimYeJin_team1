package com.yejin.exam.wbook.domain.home.controller;

import com.yejin.exam.wbook.domain.post.dto.PostTagsDto;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.service.PostService;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Api("홈 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    @ApiOperation(value = "게시글 전체 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 게시글 전체 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 게시글을 찾을 수 없습니다."),
    })
    @GetMapping("/")
    public ResponseEntity<ResultResponse> main(){
        List<Post> posts = postService.getPostsOrderByCreatedTime();
        List<PostTagsDto> postTagsDtos = new ArrayList<>();
        posts.stream()
                .map(post -> new PostTagsDto(post)
                )
                .forEach(postTagsDto -> postTagsDtos.add(postTagsDto));
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","게시글 조회에 성공하였습니다.", postTagsDtos));
    }
}
