package com.yejin.exam.wbook.domain.post.controller;


import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.post.dto.PostDto;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.service.PostHashTagService;
import com.yejin.exam.wbook.domain.post.service.PostKeywordService;
import com.yejin.exam.wbook.domain.post.service.PostService;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
@Api(tags = "게시글 API")
@Slf4j
@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostHashTagService postHashTagService;
    private final PostKeywordService postKeywordService;
    private final MemberService memberService;
    @ApiOperation(value = "게시글 생성폼")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 게시글 생성 폼 성공하였습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite(PostDto postDto) {
        return "post/post_form";
    }
    @ApiOperation(value = "게시글 생성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 게시물이 작성되었습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(Principal principal, @Valid PostDto postDto) {
        Member member = memberService.findByUsername(principal.getName()).orElseThrow();
        Post post = postService.write(member, postDto.getSubject(), postDto.getContent(),postDto.getContentHTML(),postDto.getHashTagsStr());

        String msg = "%d번 게시물이 작성되었습니다.".formatted(post.getId());
        msg = Util.url.encode(msg);
        return "redirect:/post/%d?msg=%s".formatted(post.getId(), msg);
    }
    @ApiOperation(value = "게시글 상세")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 게시글 상세조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showDetail(Model model, @PathVariable Long id) {
        Post post= postService.getForPrintPostById(id);

        model.addAttribute("post", post);

        return "post/detail";
    }
    @ApiOperation(value = "게시글 수정폼")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 게시글 생성 폼 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(Model model, @PathVariable Long id) {
        Post post= postService.getForPrintPostById(id);

        model.addAttribute("post", post);

        return "post/modify";
    }
    @ApiOperation(value = "게시글 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 게시물이 수정되었습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다.\n"
                    + "FOO1 - 존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    @ResponseBody // 임시
    public String modify(
            Model model,
            Principal principal,
            @PathVariable Long id,
            @Valid PostDto postDto,
            @RequestParam Map<String, String> params) {
        Post post = postService.getForPrintPostById(id);

        postService.modify(post, postDto.getSubject(), postDto.getContent(), postDto.getContentHTML(),postDto.getHashTagsStr());

        String msg = Util.url.encode("%d번 게시물이 수정되었습니다.".formatted(id));
        return "redirect:/post/%d?msg=%s".formatted(id, msg);
    }
    @ApiOperation(value = "게시글 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 게시글 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 게시글을 찾을 수 없습니다.\n"
                    + "M001 - 유효하지 않은 사용자 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(Model model, Principal principal, @RequestParam(defaultValue = "all") String kwType, @RequestParam(defaultValue = "") String kw){
        List<Post> posts = kwType.equals("all")
                ? posts=postService.getForPrintPostsByUsername(principal.getName())
                : postService.getForPrintPostsByKeyword(principal.getName(),kw);

        model.addAttribute("posts",posts);
        return "/post/list";
    }



}
