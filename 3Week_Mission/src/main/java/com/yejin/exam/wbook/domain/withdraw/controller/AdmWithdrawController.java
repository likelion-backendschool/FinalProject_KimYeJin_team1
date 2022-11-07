package com.yejin.exam.wbook.domain.withdraw.controller;

import com.yejin.exam.wbook.domain.withdraw.entity.WithdrawApply;
import com.yejin.exam.wbook.domain.withdraw.service.WithdrawService;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
        List<WithdrawApply> withdrawApplies = withdrawService.findAll();
        mav.addObject("withdrawApplies", withdrawApplies);
        mav.setViewName("adm/withdraw/applyList");
        return mav;
    }

    @PostMapping("/{withdrawApplyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String rebateOne(@PathVariable long withdrawApplyId, HttpServletRequest req) {
        log.debug("[withdraw] [adm] post mapping");
        ResultResponse withdrawResultResponse = withdrawService.withdraw(withdrawApplyId);
        log.debug("[withdraw] [adm] response : "+ withdrawResultResponse);

        String redirect = "redirect:/adm/withdraw/applyList";
        redirect = withdrawResultResponse.addMessageToUrl(redirect);
        log.debug("[withdraw] [adm] redirect url : "+ redirect);

        return redirect;
    }
}
