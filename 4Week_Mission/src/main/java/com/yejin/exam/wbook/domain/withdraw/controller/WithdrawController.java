package com.yejin.exam.wbook.domain.withdraw.controller;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.withdraw.dto.WithdrawApplyDto;
import com.yejin.exam.wbook.domain.withdraw.entity.WithdrawApply;
import com.yejin.exam.wbook.domain.withdraw.service.WithdrawService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
@Api(tags = "출금 API")
@Controller
@RequestMapping("/withdraw")
@RequiredArgsConstructor
@Slf4j
public class WithdrawController {

    private final WithdrawService withdrawService;

    @GetMapping("/apply")
    public ModelAndView showApply(@AuthenticationPrincipal MemberContext memberContext, ModelAndView mav, WithdrawApplyDto withdrawApplydto){
        log.debug("[withdraw] get apply form");
        Member member = memberContext.getMember();
        log.debug("[withdraw] name : "+member.getUsername() + " cash : "+member.getRestCash());
        mav.addObject("actorRestCash",member.getRestCash());
        mav.setViewName("withdraw/apply");
        return mav;

    }
    @PostMapping("/apply")
    public ModelAndView apply(@AuthenticationPrincipal MemberContext memberContext, @Valid WithdrawApplyDto withdrawApplydto, ModelAndView mav, BindingResult bindingResult){
        log.debug("[withdraw] post apply form");

        mav.setViewName("withdraw/apply");

        if (bindingResult.hasErrors()) {
            return mav;
        }
        Member member = memberContext.getMember();
        WithdrawApply withdrawApply = withdrawService.apply(member, withdrawApplydto);

        final boolean isApplied = withdrawApply.isApplied();
        if (isApplied) {
            mav.addObject("msg", "출금 신청이 완료되었습니다.");
            mav.addObject("url", "/withdraw/applyList");
            mav.setViewName("common/alert");
            return mav;
        } else {
            mav.addObject("msg", "출금 신청에 실패하였습니다.");
            mav.addObject("url", "/withdraw/apply");
            mav.setViewName("common/alert");
            return mav;
        }
    }

    @GetMapping("/applyList")
    public ModelAndView applyList(@AuthenticationPrincipal MemberContext memberContext, ModelAndView mav){
        Member member = memberContext.getMember();
        List<WithdrawApply> withdrawApplies = withdrawService.findByMember(member);
        mav.addObject("withdrawApplies", withdrawApplies);
        mav.setViewName("withdraw/applyList");
        return mav;

    }
}
