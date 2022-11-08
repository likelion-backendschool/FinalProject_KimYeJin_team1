package com.yejin.exam.wbook.domain.post.controller;


import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.post.dto.BookChapterDto;
import com.yejin.exam.wbook.domain.post.dto.PostDto;
import com.yejin.exam.wbook.domain.post.dto.PostTagsDto;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.service.PostHashTagService;
import com.yejin.exam.wbook.domain.post.service.PostKeywordService;
import com.yejin.exam.wbook.domain.post.service.PostService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Api(tags = "게시글 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostHashTagService postHashTagService;
    private final PostKeywordService postKeywordService;
    private final MemberService memberService;


    @ApiOperation(value = "게시글 생성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 게시물이 작성되었습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @PostMapping("/write")
    public ResponseEntity<ResultResponse> write(@AuthenticationPrincipal MemberContext memberContext, @Valid @RequestBody PostDto postDto) {
        Member member = memberContext.getMember();
        Post post = postService.write(member, postDto.getSubject(), postDto.getContent(),postDto.getContentHTML(),postDto.getHashTagsStr());
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d번 게시물이 작성되었습니다.".formatted(post.getId()), post.getId()));
    }

    @ApiOperation(value = "게시글 상세")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 게시글 상세조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "id", value = "게시글 PK", example = "1", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> detail(@PathVariable Long id) {
        Post post= postService.getForPrintPostById(id);
        if(post==null){
            return Util.spring.responseEntityOf(ResultResponse.failOf("FOO1","존재하지 않는 게시글 입니다.", id));
        }
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","게시글 상세조회에 성공하였습니다.", new PostTagsDto(post)));
    }

    @ApiOperation(value = "게시글 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 게시물이 수정되었습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다.\n"
                    + "FOO1 - 존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "id", value = "게시글 PK", example = "1", required = true)
    @PostMapping("/{id}/modify")
    @ResponseBody
    public ResponseEntity<ResultResponse> modify(
            @AuthenticationPrincipal MemberContext memberContext,
            @PathVariable Long id,
            @Valid @RequestBody PostDto postDto,
            @RequestParam Map<String, String> params) {
        Post post= postService.getForPrintPostById(id);
        if(post==null){
            return Util.spring.responseEntityOf(ResultResponse.failOf("FOO1","존재하지 않는 게시글 입니다.", id));
        }
        postService.modify(post, postDto.getSubject(), postDto.getContent(), postDto.getContentHTML(),postDto.getHashTagsStr());
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d번 게시물이 수정되었습니다.", post.getId()));
    }
    @ApiOperation(value = "게시글 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 게시글 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 게시글을 찾을 수 없습니다.\n"
                    + "M001 - 유효하지 않은 사용자 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "kw", value = "태그 키워드", example = "태그1", required = false)
    @GetMapping("/list")
    public ResponseEntity<ResultResponse> list(@AuthenticationPrincipal MemberContext memberContext, @RequestParam(defaultValue = "all") String kwType, @RequestParam(defaultValue = "") String kw){
        String username = memberContext.getUsername();
        List<Post> posts = kwType.equals("all")
                ? posts=postService.getForPrintPostsByUsername(username)
                : postService.getForPrintPostsByKeyword(username,kw);
        List<PostTagsDto> postTagsDtos = new ArrayList<>();
        posts.stream()
                .map(post -> new PostTagsDto(post)
                )
                .forEach(postTagsDto -> postTagsDtos.add(postTagsDto));
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","게시글 조회에 성공하였습니다.", postTagsDtos));
    }



}
