package com.yejin.exam.wbook.domain.withdraw.controller;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.withdraw.dto.WithdrawDto;
import com.yejin.exam.wbook.domain.withdraw.entity.Withdraw;
import com.yejin.exam.wbook.domain.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/adm/withdraw")
@RequiredArgsConstructor
@Slf4j
public class AdmWithdrawController {
    private final WithdrawService withdrawService;

    @GetMapping("/applyList")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView showApply(ModelAndView mav){
        List<Withdraw> withdraws = withdrawService.findAll();
        mav.addObject("withdraws", withdraws);
        mav.setViewName("adm/withdraw/applyList");
        return mav;
    }
}
