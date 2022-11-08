package com.yejin.exam.wbook.domain.member.controller;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.request.LoginDto;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation(value = "로그인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 로그인에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다."),
            @ApiResponse(code = 401, message = "M001 - 일치하는 회원이 존재하지 않습니다.\n"
                    + "M002 - 비밀번호가 일치하지 않습니다."),
    })
    @PostMapping("/login")
    public ResponseEntity<ResultResponse> login(@Valid @RequestBody LoginDto loginDto) {
        Member member = memberService.findByUsername(loginDto.getUsername()).orElse(null);

        if (member == null) {
            return Util.spring.responseEntityOf(ResultResponse.of("M001", "일치하는 회원이 존재하지 않습니다."));
        }

        if (memberService.isMatched(loginDto.getPassword(), member.getPassword()) == false) {
            return Util.spring.responseEntityOf(ResultResponse.of("M002", "비밀번호가 일치하지 않습니다."));
        }

        log.debug("Util.json.toStr(member.getAccessTokenClaims()) : " + Util.json.toStr(member.getAccessTokenClaims()));

        String accessToken = memberService.genAccessToken(member);

        return Util.spring.responseEntityOf(
                ResultResponse.of(
                        "S001",
                        "로그인에 성공하였습니다.",
                        Util.mapOf(
                                "accessToken", accessToken
                        )
                ),
                Util.spring.httpHeadersOf("Authentication", accessToken)
        );
    }
    @ApiOperation(value = "사용자 계정 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 사용자 계정 조회에 성공하였습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultResponse> me(@AuthenticationPrincipal MemberContext memberContext) {
        if (memberContext == null) {
            return Util.spring.responseEntityOf(ResultResponse.failOf("M003","로그인이 필요한 화면입니다.",null));
        }

        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","사용자 계정 조회에 성공하였습니다.",memberContext));
    }
}