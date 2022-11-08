package com.yejin.exam.wbook.domain.member.controller;

import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.dto.MemberModifyDto;
import com.yejin.exam.wbook.domain.member.dto.MemberModifyPasswordDto;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.request.LoginDto;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;



    @GetMapping(value = "/join")
    public String showJoin(MemberDto memberDto) {
        return "member/join_form";
    }
    @ApiOperation(value = "회원가입")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 회원가입에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다.\n"
                    + "F001 - 2개의 패스워드가 일치하지 않습니다."),
    })
    @PostMapping(value = "/join")
    public ModelAndView join(@Valid MemberDto memberDto, ModelAndView mav, BindingResult bindingResult) {
        mav.setViewName("member/join_form");

        if (bindingResult.hasErrors()) {
            return mav;
        }
        if (!memberDto.getPassword().equals(memberDto.getPasswordConfirm())) {
            bindingResult.addError(new FieldError("member", "PwdConfirm","2개의 패스워드가 일치하지 않습니다."));
            return mav;
        }
        Member member = memberService.join(memberDto);

        final boolean isRegistered = member != null;
        if (isRegistered) {
            //memberService.login(memberDto.getUsername(), memberDto.getPassword());
            mav.addObject("msg", "회원가입을 축하합니다.");
            mav.addObject("url", "/");
            mav.setViewName("common/alert");
            return mav;
        } else {
            mav.addObject("msg", "회원가입이 불가합니다.");
            mav.addObject("url", "/member/join");
            mav.setViewName("common/alert");
            return mav;
        }
    }

    @GetMapping("/login")
    public String login(MemberDto memberDto) {
        return "member/login_form";
    }

    @GetMapping("/profile")
    public ModelAndView detail(Principal principal, ModelAndView mav){
        Member member = memberService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException());
        mav.addObject("member", member);
        mav.setViewName("member/profile");
        return mav;
    }

    @GetMapping("/modify")
    public ModelAndView showModify(Principal principal, ModelAndView mav, MemberModifyDto memberModifyDto){
        Member member = memberService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException());
        mav.addObject("member",member);
        mav.setViewName("member/profile_form");
        return mav;
    }

    @PostMapping("/modify")
    public ModelAndView modify(Principal principal, ModelAndView mav, @Valid MemberModifyDto memberModifyDto, BindingResult bindingResult) {
        Member member = memberService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException());
        if (bindingResult.hasErrors()) {
            mav.addObject("member",member);
            mav.setViewName("member/profile_form");
            return mav;
        }
        memberService.modify(member, memberModifyDto.getEmail(), memberModifyDto.getNickname());
        mav.setViewName("redirect:/member/profile");
        return mav;
    }

    @GetMapping("/modifyPassword")
    public ModelAndView showModifyPassword(Principal principal, ModelAndView mav, MemberModifyPasswordDto memberModifyPasswordDto){
        Member member = memberService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException());
        mav.addObject("member",member);
        mav.setViewName("member/profilePassword_form");
        return mav;
    }
    @ApiOperation(value = "비밀번호 변경")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 비밀번호 변경에 성공하였습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다.\n"
                    + "F001 - 2개의 패스워드가 일치하지 않습니다.\n"
                    + "F002 - 기존 패스워드와 동일한 패스워드로 바꿀 수 없습니다."),
    })
    @PostMapping("/modifyPassword")
    public ModelAndView modifyPassword(Principal principal, ModelAndView mav, @Valid MemberModifyPasswordDto memberModifyPasswordDto, BindingResult bindingResult){
        mav.setViewName("member/profile_form");
        Member member = memberService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException());
        if(memberModifyPasswordDto.getOldPassword() == memberModifyPasswordDto.getPassword()){
            bindingResult.addError(new FieldError("member", "password","기존 패스워드와 동일한 패스워드로 바꿀 수 없습니다."));
            return mav;
        }
        if (!memberModifyPasswordDto.getPassword().equals(memberModifyPasswordDto.getPasswordConfirm())) {
            bindingResult.addError(new FieldError("member", "passwordConfirm","2개의 패스워드가 일치하지 않습니다."));
            return mav;
        }
//        if(!memberService.modifyPassword(member,memberModifyPasswordDto.getPassword(),memberModifyPasswordDto.getOldPassword())){
//            bindingResult.addError(new FieldError("member", "oldPassword","올바른 기존 패스워드를 입력하세요."));
//            return mav;
//        }
        mav.setViewName("redirect:/member/profile");
        return mav;

    }

    @GetMapping("/findUsername")
    public String showFindUsername(){
        return "/member/findUsername_form";
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
    public ResponseEntity<ResultResponse> findUsername(ModelAndView mav, String email){
        Optional<Member> oMember = memberService.findByEmail(email);
        if(oMember.isPresent()){
            return ResponseEntity.ok(ResultResponse.successOf("S001","아이디 찾기에 성공하였습니다." ,oMember.get().getUsername()));
        }
        return ResponseEntity.ok(ResultResponse.failOf("F001","해당하는 아이디가 없습니다.",false));
    }

    @GetMapping("/findPassword")
    public String showFindPassword(){
        return "member/findPassword_form";
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
    public ResponseEntity<ResultResponse> findUsername(ModelAndView mav, String username, String email){
        Optional<Member> oMember = memberService.findByUsername(username);
        if(!oMember.isPresent()){
            return ResponseEntity.ok(ResultResponse.of("F001","해당하는 아이디가 없습니다.",username));
        }
//        memberService.setTempPassword(oMember.get());
        return ResponseEntity.ok(ResultResponse.of("S001","%s로 임시 비밀번호를 전송하였습니다.".formatted(email),username));

    }

    @ApiOperation(value = "탈퇴")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 회원 탈퇴가 완료되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 해당하는 회원 정보가 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @GetMapping("/delete")
    public String delete(Principal principal){
        Member member = memberService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException());
        memberService.delete(member);
        return "redirect:/";
    }


}
