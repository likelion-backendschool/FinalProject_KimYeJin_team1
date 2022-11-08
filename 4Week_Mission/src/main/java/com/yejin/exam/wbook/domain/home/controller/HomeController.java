package com.yejin.exam.wbook.domain.home.controller;

import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.service.PostService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    @ApiOperation(value = "게시글 전체 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 게시글 전체 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 게시글을 찾을 수 없습니다."),
    })
    @GetMapping("/")
    public String main(Model model){
        List<Post> posts = postService.getPostsOrderByCreatedTime();
        model.addAttribute("posts",posts);
        return "home/main";
    }
}
