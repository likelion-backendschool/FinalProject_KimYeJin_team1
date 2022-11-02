package com.yejin.exam.wbook.domain.withdraw.controller;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.withdraw.dto.WithdrawDto;
import com.yejin.exam.wbook.domain.withdraw.entity.Withdraw;
import com.yejin.exam.wbook.domain.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("/withdraw")
@RequiredArgsConstructor
@Slf4j
public class WithdrawController {

    private final WithdrawService withdrawService;

    @GetMapping("/apply")
    public String showApply(WithdrawDto withdrawdto){

        return "withdraw/apply_form";

    }
    @PostMapping("/apply")
    public ModelAndView apply(@Valid WithdrawDto withdrawdto, ModelAndView mav, BindingResult bindingResult){
        mav.setViewName("withdraw/apply_form");

        if (bindingResult.hasErrors()) {
            return mav;
        }

        Withdraw withdraw = withdrawService.apply(withdrawdto);

        final boolean isApplied = withdraw != null;
        if (isApplied) {
            mav.addObject("msg", "출금 신청이 완료되었습니다.");
            mav.addObject("url", "/withdraw/applyList");
            mav.setViewName("alert");
            return mav;
        } else {
            mav.addObject("msg", "출금 신청에 실패하였습니다.");
            mav.addObject("url", "/withdraw/apply");
            mav.setViewName("alert");
            return mav;
        }
    }
}
