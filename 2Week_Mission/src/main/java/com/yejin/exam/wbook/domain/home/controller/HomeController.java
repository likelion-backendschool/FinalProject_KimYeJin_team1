package com.yejin.exam.wbook.domain.home.controller;

import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.service.PostService;
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


    @GetMapping("/")
    public String main(Model model){
        List<Post> posts = postService.getPostsOrderByCreatedTime();
        model.addAttribute("posts",posts);
        return "home/main";
    }
}
