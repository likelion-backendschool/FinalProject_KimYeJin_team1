package com.yejin.exam.wbook.domain.member.controller;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.request.LoginDto;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.transform.Result;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Slf4j
public class JwtMemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<ResultResponse> login(@Valid @RequestBody LoginDto loginDto) {
        Member member = memberService.findByUsername(loginDto.getUsername()).orElse(null);

        if (member == null) {
            return Util.spring.responseEntityOf(ResultResponse.of("NOT_FOUND_MEMBER_FAILED", "일치하는 회원이 존재하지 않습니다."));
        }

        if (memberService.isMatched(loginDto.getPassword(), member.getPassword()) == false) {
            return Util.spring.responseEntityOf(ResultResponse.of("NO_MATCH_PWD_FAILED", "비밀번호가 일치하지 않습니다."));
        }

        log.debug("Util.json.toStr(member.getAccessTokenClaims()) : " + Util.json.toStr(member.getAccessTokenClaims()));

        String accessToken = memberService.genAccessToken(member);

        return Util.spring.responseEntityOf(
                ResultResponse.of(
                        "LOGIN_OK",
                        "로그인 성공, Access Token을 발급합니다.",
                        Util.mapOf(
                                "accessToken", accessToken
                        )
                ),
                Util.spring.httpHeadersOf("Authentication", accessToken)
        );
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultResponse> me(@AuthenticationPrincipal MemberContext memberContext) {
        if (memberContext == null) {
            return Util.spring.responseEntityOf(ResultResponse.failOf("GET_PROFILE_FAILED","로그인이 필요합니다.",null));
        }

        return Util.spring.responseEntityOf(ResultResponse.successOf("GET_PROFILE_OK","사용자 프로필",memberContext));
    }
}