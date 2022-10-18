package com.yejin.exam.wbook.domain.member.controller;

import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.global.result.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping(value = "/join")
    public String showJoin() {
        return "member/join_form";
    }
    @PostMapping(value = "/join")
    public ModelAndView join(@Valid @RequestBody MemberDto memberDto, ModelAndView mav) {
        final boolean isRegistered = memberService.join(memberDto) == null;

        if (isRegistered) {
            memberService.login(memberDto.getUsername(), memberDto.getPassword());
            mav.addObject("msg","회원가입을 축하합니다.");
            mav.addObject("url","/");
            mav.setViewName("alert");
            mav.setViewName("redirect:/");
            return mav;
        } else {
            mav.addObject("msg","회원가입이 불가합니다.");
            mav.addObject("url","/member/join");
            mav.setViewName("alert");
            return mav;
        }
    }
}
