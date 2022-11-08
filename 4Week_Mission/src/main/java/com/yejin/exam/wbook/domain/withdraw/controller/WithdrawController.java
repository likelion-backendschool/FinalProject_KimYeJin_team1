package com.yejin.exam.wbook.domain.withdraw.controller;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.withdraw.dto.WithdrawApplyDto;
import com.yejin.exam.wbook.domain.withdraw.entity.WithdrawApply;
import com.yejin.exam.wbook.domain.withdraw.service.WithdrawService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
@Api(tags = "출금 API")
@RestController
@RequestMapping("/api/v1/withdraw")
@RequiredArgsConstructor
@Slf4j
public class WithdrawController {

    private final WithdrawService withdrawService;

    @ApiOperation(value = "출금 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 출금 신청이 완료되었습니다"),
            @ApiResponse(code = 400, message = "FOO1 - 출금 신청에 실패하였습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.."),
    })
    @PostMapping("/apply")
    public ResponseEntity<ResultResponse> apply(@AuthenticationPrincipal MemberContext memberContext, @Valid @RequestBody WithdrawApplyDto withdrawApplydto){
        log.debug("[withdraw] post apply dto :  price : "+withdrawApplydto.getPrice());

        Member member = memberContext.getMember();
        WithdrawApply withdrawApply = withdrawService.apply(member, withdrawApplydto);

        final boolean isApplied = withdrawApply.isApplied();
        if (isApplied) {
            return Util.spring.responseEntityOf(ResultResponse.successOf("S001","출금 신청이 완료되었습니다", withdrawApply));
        } else {
            return Util.spring.responseEntityOf(ResultResponse.successOf("F001","출금 신청에 실패하였습니다.", null));
        }
    }
    @ApiOperation(value = "출금 신청 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 출금 신청 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 출금 신청 조회 할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.."),
    })
    @GetMapping("/applyList")
    public ResponseEntity<ResultResponse> applyList(@AuthenticationPrincipal MemberContext memberContext){
        Member member = memberContext.getMember();
        List<WithdrawApply> withdrawApplies = withdrawService.findByMember(member);
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","출금 신청 조회에 성공하였습니다.", withdrawApplies));
    }
}
