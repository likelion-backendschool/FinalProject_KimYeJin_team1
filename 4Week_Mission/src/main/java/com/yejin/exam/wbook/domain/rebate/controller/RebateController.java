package com.yejin.exam.wbook.domain.rebate.controller;

import com.yejin.exam.wbook.domain.rebate.entity.RebateOrderItem;
import com.yejin.exam.wbook.domain.rebate.service.RebateService;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
@Api(tags = "정산 API")
@RestController
@RequestMapping("/api/v1/adm/rebate")
@RequiredArgsConstructor
@Slf4j
public class RebateController {
    private final RebateService rebateService;


    @ApiOperation(value = "정산 데이터 생성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 정산데이터가 성공적으로 생성되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 정산 데이터를 생성할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 관리자 권한이 필요한 화면입니다."),
    })
    @PostMapping("/makeData")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultResponse> makeData(@RequestParam String yearMonth) { // 추후 정산 데이터 응답용 dto 생성 필요
        ResultResponse makeDateResultResponse = rebateService.makeDate(yearMonth);
        return Util.spring.responseEntityOf(makeDateResultResponse);
    }
    @ApiOperation(value = "정산 데이터 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 정산 데이터 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 정산 데이터 조회 할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 관리자 권한이 필요한 화면입니다."),
    })
    @GetMapping("/rebateOrderItemList")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParam(name = "yearMonth", value = "정산 년월 (YYYY-MM 형태)", example = "2022-11", required = false)
    public ResponseEntity<ResultResponse> rebateOrderItemList(@RequestParam(required = false) String yearMonth, Model model) {
        if (yearMonth==null || yearMonth.isEmpty()) { // RequestParam 의 defaultValue로 처리 불가
            yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM"));
        }
        List<RebateOrderItem> items = rebateService.findRebateOrderItemsByPayDateIn(yearMonth);
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","정산 데이터 조회에 성공하였습니다.",Util.mapOf("yearMonth",yearMonth,"rebateOrderItemList",items)));
    }
    @ApiOperation(value = "주문품목 단건 정산")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 주문품목번호 %d번에 대해서 판매자에게 %s원 정산을 완료하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 정산가능한 주문 품목이 없습니다.\n"
                    + "F002 - 정산을 할 수 없는 상태입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 관리자 권한이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "orderItemId", value = "주문품목번호", example = "1", required = true)
    @PostMapping("/rebateOne/{orderItemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultResponse> rebateOne(@PathVariable long orderItemId, HttpServletRequest req) {
        ResultResponse rebateResultResponse = rebateService.rebate(orderItemId);
        return Util.spring.responseEntityOf(rebateResultResponse);
    }
    @ApiOperation(value = "주문품목 리스트 정산")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d건의 정산품목을 정산처리하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 정산가능한 주문 품목이 없습니다.\n"
                    + "F002 - 정산을 할 수 없는 상태입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 관리자 권한이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "ids", value = "주문품목번호 모음", example = "1,2", required = true)
    @PostMapping("/rebate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultResponse> rebate(@RequestParam String ids, HttpServletRequest req) {

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    rebateService.rebate(id);
                });
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d건의 정산품목을 정산처리하였습니다.",idsArr.length));
    }
}
