package com.yejin.exam.wbook.domain.member.controller;

import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.dto.MemberModifyDto;
import com.yejin.exam.wbook.domain.member.dto.MemberModifyPasswordDto;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.request.LoginDto;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;
@Api(tags = "회원 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;



    @ApiOperation(value = "회원가입")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 회원가입에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다.\n"
                    + "F001 - 2개의 패스워드가 일치하지 않습니다."),
    })
    @PostMapping(value = "/join")
    public ResponseEntity<ResultResponse> join(@Valid @RequestBody MemberDto memberDto) {
        if (!memberDto.getPassword().equals(memberDto.getPasswordConfirm())) {
            //bindingResult.addError(new FieldError("member", "PwdConfirm","2개의 패스워드가 일치하지 않습니다."));
            return ResponseEntity.ok(ResultResponse.failOf("F001","2개의 패스워드가 일치하지 않습니다.",Util.mapOf("objectName","member","field","PwdConfirm")));
        }
        Member member = memberService.join(memberDto);
        final boolean isRegistered = member != null;
        if (isRegistered) {
            return ResponseEntity.ok(ResultResponse.successOf("S001","회원가입을 축하합니다.",member.getId()));
        } else {
            return ResponseEntity.ok(ResultResponse.failOf("F001","회원가입이 불가합니다.",null));
        }
    }

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
                ResultResponse.successOf(
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
    public ResponseEntity<ResultResponse> me(@AuthenticationPrincipal MemberContext memberContext) {
        if (memberContext == null) {
            return Util.spring.responseEntityOf(ResultResponse.failOf("M003","로그인이 필요한 화면입니다.",null));
        }

        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","사용자 계정 조회에 성공하였습니다.",memberContext));
    }

    @ApiOperation(value = "회원정보 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 회원정보 수정에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다."),
    })
    @PostMapping("/modify")
    public ResponseEntity<ResultResponse> modify(@AuthenticationPrincipal MemberContext memberContext, @Valid @RequestBody MemberModifyDto memberModifyDto) {
        if (memberContext == null) {
            return Util.spring.responseEntityOf(ResultResponse.failOf("M003","로그인이 필요한 화면입니다.",null));
        }
        Member member = memberContext.getMember();
        memberService.modify(member, memberModifyDto.getEmail(), memberModifyDto.getNickname());
        return ResponseEntity.ok(ResultResponse.successOf("S001","회원정보 수정에 성공하였습니다.",member.getId()));
    }

    @ApiOperation(value = "비밀번호 변경")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 비밀번호 변경에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다.\n"
                    + "F001 - 2개의 패스워드가 일치하지 않습니다.\n"
                    + "F002 - 기존 패스워드와 동일한 패스워드로 바꿀 수 없습니다.\n"
                    + "F003 - 올바른 기존 패스워드를 입력하세요."),
    })
    @PostMapping("/modifyPassword")
    public ResponseEntity<ResultResponse> modifyPassword(@AuthenticationPrincipal MemberContext memberContext, @Valid @RequestBody MemberModifyPasswordDto memberModifyPasswordDto){

        Member member = memberContext.getMember();
        if(memberModifyPasswordDto.getOldPassword() == memberModifyPasswordDto.getPassword()){
            //bindingResult.addError(new FieldError("member", "password","기존 패스워드와 동일한 패스워드로 바꿀 수 없습니다."));
            return ResponseEntity.ok(ResultResponse.failOf("F002","기존 패스워드와 동일한 패스워드로 바꿀 수 없습니다.",Util.mapOf("objectName","member","field","password")));
        }
        if (!memberModifyPasswordDto.getPassword().equals(memberModifyPasswordDto.getPasswordConfirm())) {
            //bindingResult.addError(new FieldError("member", "passwordConfirm","2개의 패스워드가 일치하지 않습니다."));
            return ResponseEntity.ok(ResultResponse.failOf("F001","2개의 패스워드가 일치하지 않습니다.",Util.mapOf("objectName","member","field","PwdConfirm")));
        }
        if(!memberService.modifyPassword(member,memberModifyPasswordDto.getPassword(),memberModifyPasswordDto.getOldPassword())){
            //bindingResult.addError(new FieldError("member", "oldPassword","올바른 기존 패스워드를 입력하세요."));
            return ResponseEntity.ok(ResultResponse.failOf("F003","올바른 기존 패스워드를 입력하세요.",Util.mapOf("objectName","member","field","oldPassword")));
        }

        return ResponseEntity.ok(ResultResponse.successOf("S001","비밀번호 변경에 성공하였습니다.",member.getId()));
    }


    @ApiOperation(value = "아이디 찾기")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 아이디 찾기에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다.\n"
                    + "F001 - 해당하는 아이디가 없습니다."),
    })
    @ApiImplicitParam(name = "email", value = "이메일", example = "kyj2212@gmail.com", required = true)
    @PostMapping("/findUsername")
    public ResponseEntity<ResultResponse> findUsername(String email){
        Optional<Member> oMember = memberService.findByEmail(email);
        if(oMember.isPresent()){
            return ResponseEntity.ok(ResultResponse.successOf("S001","아이디 찾기에 성공하였습니다." ,oMember.get().getUsername()));
        }
        return ResponseEntity.ok(ResultResponse.failOf("F001","해당하는 아이디가 없습니다.",false));
    }

    @ApiOperation(value = "비밀번호 찾기")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %s로 임시 비밀번호를 전송하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다.\n"
                    + "F001 - 해당하는 아이디가 없습니다."),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "아이디", example = "user1", required = true),
            @ApiImplicitParam(name = "email", value = "이메일", example = "kyj2212@gmail.com", required = true)
    })
    @PostMapping("/findPassword")
    public ResponseEntity<ResultResponse> findUsername(String username, String email){
        Optional<Member> oMember = memberService.findByUsername(username);
        if(!oMember.isPresent()){
            return ResponseEntity.ok(ResultResponse.failOf("F001","해당하는 아이디가 없습니다.",username));
        }
        memberService.setTempPassword(oMember.get());
        return ResponseEntity.ok(ResultResponse.successOf("S001","%s로 임시 비밀번호를 전송하였습니다.".formatted(email),username));

    }

    @ApiOperation(value = "탈퇴")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 회원 탈퇴가 완료되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 해당하는 회원 정보가 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @GetMapping("/delete")
    public ResponseEntity<ResultResponse> delete(@AuthenticationPrincipal MemberContext memberContext){
        Member member = memberContext.getMember();
        memberService.delete(member);
        return ResponseEntity.ok(ResultResponse.of("S001","회원 탈퇴가 완료되었습니다.",member.getId()));
    }


}
