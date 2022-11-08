package com.yejin.exam.wbook.domain.rebate.controller;

import com.yejin.exam.wbook.domain.rebate.entity.RebateOrderItem;
import com.yejin.exam.wbook.domain.rebate.service.RebateService;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
@Api(tags = "정산 API")
@Controller
@RequestMapping("/adm/rebate")
@RequiredArgsConstructor
@Slf4j
public class RebateController {
    private final RebateService rebateService;

    @ApiOperation(value = "정산 데이터 생성 폼")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 정산 데이터 생성 페이지입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 관리자 권한이 필요한 화면입니다."),
    })
    @GetMapping("/makeData")
    @PreAuthorize("hasRole('ADMIN')")
    public String showMakeData() {
        return "adm/rebate/makeData";
    }

    @ApiOperation(value = "정산 데이터 생성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 정산 데이터 생성에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 정산 데이터를 생성할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 관리자 권한이 필요한 화면입니다."),
    })
    @PostMapping("/makeData")
    @PreAuthorize("hasRole('ADMIN')")
    public String makeData(String yearMonth) {
        ResultResponse makeDateResultResponse = rebateService.makeDate(yearMonth);
        String redirect = makeDateResultResponse.addMessageToUrl("redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth);

        return redirect;
    }
    @ApiOperation(value = "정산 데이터 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 정산 데이터 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 조회 가능한 정산데이터가 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 관리자 권한이 필요한 화면입니다."),
    })
    @GetMapping("/rebateOrderItemList")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRebateOrderItemList(String yearMonth, Model model) {
        if (yearMonth==null || yearMonth.isEmpty()) {
            yearMonth = "2022-11";
        }

        List<RebateOrderItem> items = rebateService.findRebateOrderItemsByPayDateIn(yearMonth);

        model.addAttribute("items", items);

        return "adm/rebate/rebateOrderItemList";
    }
    @ApiOperation(value = "주문품목 단건 정산")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 주문품목번호 %d번에 대해서 판매자에게 %s원 정산을 완료하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 정산가능한 주문 품목이 없습니다.\n"
                    + "F002 - 정산을 할 수 없는 상태입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 관리자 권한이 필요한 화면입니다."),
    })
    @PostMapping("/rebateOne/{orderItemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String rebateOne(@PathVariable long orderItemId, HttpServletRequest req) {
        ResultResponse rebateResultResponse = rebateService.rebate(orderItemId);

        String referer = req.getHeader("Referer");
        String yearMonth = Util.url.getQueryParamValue(referer, "yearMonth", "2022-11");

        String redirect = "redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth;

        redirect = rebateResultResponse.addMessageToUrl(redirect);

        return redirect;
    }
    @ApiOperation(value = "주문품목 리스트 정산")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 주문품목번호 %d번에 대해서 판매자에게 %s원 정산을 완료하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 정산가능한 주문 품목이 없습니다.\n"
                    + "F002 - 정산을 할 수 없는 상태입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
            @ApiResponse(code = 403, message = "M004 - 관리자 권한이 필요한 화면입니다."),
    })
    @PostMapping("/rebate")
    @PreAuthorize("hasRole('ADMIN')")
    public String rebate(String ids, HttpServletRequest req) {

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    rebateService.rebate(id);
                });

        String referer = req.getHeader("Referer");
        String yearMonth = Util.url.getQueryParamValue(referer, "yearMonth", "");

        String redirect = "redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth;
        redirect += "&msg=" + Util.url.encode("%d건의 정산품목을 정산처리하였습니다.".formatted(idsArr.length));

        return redirect;
    }
}
