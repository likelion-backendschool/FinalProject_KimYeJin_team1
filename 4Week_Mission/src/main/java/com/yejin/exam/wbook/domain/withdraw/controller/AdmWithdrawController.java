package com.yejin.exam.wbook.domain.withdraw.controller;

import com.yejin.exam.wbook.domain.withdraw.entity.WithdrawApply;
import com.yejin.exam.wbook.domain.withdraw.service.WithdrawService;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Api(tags = "관리자 정산 API")
@RestController
@RequestMapping("/api/v1/adm/withdraw")
@RequiredArgsConstructor
@Slf4j
public class AdmWithdrawController {
    private final WithdrawService withdrawService;
    @ApiOperation(value = "관리자 출금 신청 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 관리자 출금 신청 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 관리자 출금 신청 조회 할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M003 - 관리자 권한이 필요한 화면입니다."),
    })
    @GetMapping("/applyList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultResponse> list(){
        List<WithdrawApply> withdrawApplies = withdrawService.findAll();
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","출금 신청 조회에 성공하였습니다.", withdrawApplies));
    }
    @ApiOperation(value = "출금")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 출금 신청 번호 %d번에 대해서 출금 %d 원 정상 지급되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 출금가능한 신청서가 없습니다.\n"
                    + "FOO2 - 이미 처리 되었습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.."),
            @ApiResponse(code = 403, message = "M003 - 관리자 권한이 필요한 화면입니다."),
    })
    @PostMapping("/{withdrawApplyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultResponse> withdraw(@PathVariable long withdrawApplyId, HttpServletRequest req) {
        log.debug("[withdraw] [adm] post mapping");
        ResultResponse withdrawResultResponse = withdrawService.withdraw(withdrawApplyId);
        return Util.spring.responseEntityOf(withdrawResultResponse);
    }
}
