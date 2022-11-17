package com.yejin.exam.wbook.domain.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.order.entity.Order;
import com.yejin.exam.wbook.domain.order.service.OrderService;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.exception.ActorCanNotPayOrderException;
import com.yejin.exam.wbook.global.exception.ActorCanNotSeeOrderException;
import com.yejin.exam.wbook.global.exception.OrderIdNotMatchedException;
import com.yejin.exam.wbook.global.exception.OrderNotEnoughRestCashException;
import com.yejin.exam.wbook.global.request.Rq;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Api(tags = "주문 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final Rq rq;
    @ApiOperation(value = "예치금 결제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d원 예치금 결제가 완료되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 예치금이 부족합니다.\n"),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "id", value = "주문 PK", example = "1", required = true)
    @PostMapping("/{id}/payByRestCashOnly")
    public ResponseEntity<ResultResponse> payByRestCashOnly(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findForPrintById(id).get();
        Member actor = memberContext.getMember();
        long restCash = memberService.getRestCash(actor);
        if (orderService.actorCanPayment(actor, order) == false) {
            throw new ActorCanNotPayOrderException();
        }
        ResultResponse resultResponse = orderService.payByRestCashOnly(order);
        return Util.spring.responseEntityOf(resultResponse);
    }

    @ApiOperation(value = "주문 상세")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 주문 상세 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 주문을 찾을 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "id", value = "주문 PK", example = "1", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> detail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findForPrintById(id).orElse(null);

        if (order == null) {
            return Util.spring.responseEntityOf(ResultResponse.failOf("F001","주문을 찾을 수 없습니다.", id));
        }

        Member actor = memberContext.getMember();
        long restCash = memberService.getRestCash(actor);

        if (orderService.actorCanSee(actor, order) == false) {
            throw new ActorCanNotSeeOrderException();
        }

        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d번 도서가 생성되었습니다.", Util.mapOf("order",order,"actorRestCash",restCash)));
    }

    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }

    @Value("${custom.tossPayments.secretKey}")
    private String SECRET_KEY;

    @ApiOperation(value = "토스페이먼츠 결제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 주문이 결제처리되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 예치금이 부족합니다.\n"),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "주문 PK", example = "1", required = true),
            @ApiImplicitParam(name = "paymentKey", value = "페이지", example = "1", required = true),
            @ApiImplicitParam(name = "orderId", value = "주문번호", example = "order__1__78893412342", required = true),
            @ApiImplicitParam(name = "amount", value = "페이 사용금액", example = "1000", required = true)
    })
    @RequestMapping("/{id}/success")
    public ResponseEntity<ResultResponse> confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @AuthenticationPrincipal MemberContext memberContext
    ) throws Exception {

        Order order = orderService.findForPrintById(id).get();

        long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

        if (id != orderIdInputed) {
            throw new OrderIdNotMatchedException();
        }

        HttpHeaders headers = new HttpHeaders();
        // headers.setBasicAuth(SECRET_KEY, ""); // spring framework 5.2 이상 버전에서 지원
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Member actor = memberContext.getMember();
        long restCash = memberService.getRestCash(actor);
        long payPriceRestCash = order.calculatePayPrice() - amount;

        if (payPriceRestCash > restCash) {
            throw new OrderNotEnoughRestCashException();
        }

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            ResultResponse resultResponse = orderService.payByTossPayments(order, payPriceRestCash);
            return Util.spring.responseEntityOf(resultResponse);
        } else {
            JsonNode failNode = responseEntity.getBody();
            return Util.spring.responseEntityOf(ResultResponse.failOf(failNode.get("code").asText(), failNode.get("message").asText(), null));
        }
    }

    @RequestMapping("/{id}/fail")
    public ResponseEntity<ResultResponse> failPayment(@RequestParam String message, @RequestParam String code) {
        return Util.spring.responseEntityOf(ResultResponse.failOf(code, message, null));
    }

    @ApiOperation(value = "주문 생성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 주문이 생성되었습니다."),
            @ApiResponse(code = 400, message = "GOO1 - 유효하지 않은 입력입니다.\n"
                    + "G002 - 유효하지 않은 입력 타입 입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @PostMapping("/create")
    public ResponseEntity<ResultResponse> create(@AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberContext.getMember();
        Order order = orderService.createFromCart(member);
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d번 주문이 생성되었습니다.", order));
    }

    @ApiOperation(value = "주문 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 주문 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 주문 조회할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @GetMapping("/list")
    public ResponseEntity<ResultResponse> list() {
        List<Order> orders = orderService.findAllByBuyerId(rq.getId());
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","주문 조회에 성공하였습니다.", orders));
    }

    @ApiOperation(value = "주문 취소")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 주문이 취소되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 이미 결제처리 되었습니다.\n"
                    + "FOO2 - 이미 취소되었습니다.\n"
                    + "FOO3 - 주문자만 취소할 수 있습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "orderId", value = "주문 PK", example = "1", required = true)
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ResultResponse> cancel(@PathVariable Long orderId) {
        ResultResponse resultResponse = orderService.cancel(orderId, rq.getMember());
        return Util.spring.responseEntityOf(resultResponse);
    }

    @ApiOperation(value = "환불")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d원 환불되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 결제 상품을 찾을 수 없습니다.\n"
                    + "FOO2 - 이미 취소되었습니다.\n"
                    + "FOO3 - 이미 환불되었습니다.\n"
                    + "FOO4 - 결제가 되어야 환불이 가능합니다.\n"
                    + "FOO5 - 주문자만 환불할 수 있습니다.\n"
                    + "FOO6 - 결제 된지 10분이 지났으므로, 환불 할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "orderId", value = "주문 PK", example = "1", required = true)
    @PostMapping("/{orderId}/refund")
    public ResponseEntity<ResultResponse> refund(@PathVariable Long orderId) {
        ResultResponse resultResponse = orderService.refund(orderId, rq.getMember());
        return Util.spring.responseEntityOf(resultResponse);
    }
}