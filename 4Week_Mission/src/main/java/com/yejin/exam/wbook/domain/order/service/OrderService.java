package com.yejin.exam.wbook.domain.order.service;

import com.yejin.exam.wbook.domain.cart.entity.CartItem;
import com.yejin.exam.wbook.domain.cart.service.CartService;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.mybook.service.MyBookService;
import com.yejin.exam.wbook.domain.order.entity.Order;
import com.yejin.exam.wbook.domain.order.entity.OrderItem;
import com.yejin.exam.wbook.domain.order.repository.OrderItemRepository;
import com.yejin.exam.wbook.domain.order.repository.OrderRepository;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.global.result.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final MemberService memberService;
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MyBookService myBookService;

    @Transactional
    public Order createFromCart(Member buyer) {
        // 입력된 회원의 장바구니 아이템들을 전부 가져온다.

        // 만약에 특정 장바구니의 상품옵션이 판매불능이면 삭제
        // 만약에 특정 장바구니의 상품옵션이 판매가능이면 주문품목으로 옮긴 후 삭제

        List<CartItem> cartItems = cartService.getItemsByBuyer(buyer);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.isOrderable()) {
                orderItems.add(new OrderItem(product));
            }

            cartService.removeItem(cartItem);
        }

        return create(buyer, orderItems);
    }

    @Transactional
    public Order create(Member buyer, List<OrderItem> orderItems) {
        Order order = Order
                .builder()
                .buyer(buyer)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        // 주문 품목으로 부터 이름을 만든다.
        order.makeName();

        orderRepository.save(order);

        return order;
    }

    @Transactional
    public ResultResponse payByTossPayments(Order order, long useRestCash) {
        Member buyer = order.getBuyer();
        long restCash = buyer.getRestCash();
        int payPrice = order.calculatePayPrice();

        long pgPayPrice = payPrice - useRestCash;
        memberService.addCash(buyer, pgPayPrice, "주문__%d__충전__토스페이먼츠".formatted(order.getId()));
        memberService.addCash(buyer, pgPayPrice * -1, "주문__%d__사용__토스페이먼츠".formatted(order.getId()));

        if (useRestCash > 0) {
            if ( useRestCash > restCash ) {
                throw new RuntimeException("예치금이 부족합니다.");
            }

            memberService.addCash(buyer, useRestCash * -1, "주문__%d__사용__예치금".formatted(order.getId()));
        }

        payDone(order);

        return ResultResponse.of("S001", "결제가 완료되었습니다.");
    }

    @Transactional
    public ResultResponse payByRestCashOnly(Order order) {
        Member buyer = order.getBuyer();
        long restCash = buyer.getRestCash();
        int payPrice = order.calculatePayPrice();

        if (payPrice > restCash) {
            throw new RuntimeException("예치금이 부족합니다.");
        }

        memberService.addCash(buyer, payPrice * -1, "주문__%d__사용__예치금".formatted(order.getId()));

        payDone(order);

        return ResultResponse.of("S001", "%d원 예치금 결제가 완료되었습니다.".formatted(payPrice));
    }

    private void payDone(Order order) {
        order.setPaymentDone();
        myBookService.add(order);
        orderRepository.save(order);
    }

    @Transactional
    public ResultResponse refund(Order order, Member actor) {
        ResultResponse actorCanRefundRsData = actorCanRefund(actor, order);
        log.debug("[order] actorCanRefund ? : "+ actorCanRefundRsData.getResultCode() + " "+actorCanRefundRsData.getMessage() + " "+actorCanRefundRsData.getData());

        if (actorCanRefundRsData.isFail()) {
            return actorCanRefundRsData;
        }

        order.setCancelDone();

        int payPrice = order.getPayPrice();
        memberService.addCash(order.getBuyer(), payPrice, "주문__%d__환불__예치금".formatted(order.getId()));

        order.setRefundDone();
        orderRepository.save(order);

        myBookService.remove(order);

        return ResultResponse.of("S001", "%d원 환불되었습니다.".formatted(payPrice));
    }

    @Transactional
    public ResultResponse refund(Long orderId, Member actor) {
        Order order = findById(orderId).orElse(null);

        if (order == null) {
            return ResultResponse.of("F001", "결제 상품을 찾을 수 없습니다.");
        }
        return refund(order, actor);
    }

    public ResultResponse actorCanRefund(Member actor, Order order) {

        if (order.isCanceled()) {
            return ResultResponse.of("F002", "이미 취소되었습니다.");
        }

        if ( order.isRefunded() ) {
            return ResultResponse.of("F003", "이미 환불되었습니다.");
        }

        if ( order.isPaid() == false ) {
            return ResultResponse.of("F004", "결제가 되어야 환불이 가능합니다.");
        }

        if (actor.getId().equals(order.getBuyer().getId()) == false) {
            return ResultResponse.of("F005", "주문자만 환불할 수 있습니다.");
        }

        long between = ChronoUnit.MINUTES.between(order.getPayDate(), LocalDateTime.now());

        if (between > 10) {
            return ResultResponse.of("F006", "결제 된지 10분이 지났으므로, 환불 할 수 없습니다.");
        }

        return ResultResponse.of("S001", "환불할 수 있습니다.");
    }

    public Optional<Order> findForPrintById(long id) {
        return findById(id);
    }

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public boolean actorCanSee(Member actor, Order order) {
        return actor.getId().equals(order.getBuyer().getId());
    }

    public boolean actorCanPayment(Member actor, Order order) {
        return actorCanSee(actor, order);
    }


    public List<Order> findAllByBuyerId(long buyerId) {
        return orderRepository.findAllByBuyerIdOrderByIdDesc(buyerId);
    }

    @Transactional
    public ResultResponse cancel(Order order, Member actor) {
        ResultResponse actorCanCancelRsData = actorCanCancel(actor, order);
        log.debug("[order] actorCanCancel ? : "+ actorCanCancelRsData.getResultCode() + " "+actorCanCancelRsData.getMessage() + " "+actorCanCancelRsData.getData());
        if (actorCanCancelRsData.isFail()) {
            return actorCanCancelRsData;
        }

        log.debug("[order] order is canceled : "+ order.isCanceled());
        order.setCanceled(true);
        log.debug("[order] order set cancel : "+ order.isCanceled());
        return ResultResponse.of("S001", "주문이 취소되었습니다.");
    }

    @Transactional
    public ResultResponse cancel(Long orderId, Member actor) {
        Order order = findById(orderId).get();
        return cancel(order, actor);
    }

    public ResultResponse actorCanCancel(Member actor, Order order) {
        if ( order.isPaid() ) {
            return ResultResponse.of("F001", "이미 결제처리 되었습니다.");
        }

        if (order.isCanceled()) {
            return ResultResponse.of("F002", "이미 취소되었습니다.");
        }

        if (actor.getId().equals(order.getBuyer().getId()) == false) {
            return ResultResponse.of("F003", "주문자만 취소할 수 있습니다.");
        }

        return ResultResponse.of("S001", "취소할 수 있습니다.");
    }

    public List<OrderItem> findAllByPayDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate) {
        return orderItemRepository.findAllByPayDateBetween(fromDate, toDate);
    }
}