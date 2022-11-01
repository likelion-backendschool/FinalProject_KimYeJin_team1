package com.yejin.exam.wbook.domain.rebate.service;

import com.yejin.exam.wbook.domain.cash.entity.CashLog;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.order.entity.OrderItem;
import com.yejin.exam.wbook.domain.order.service.OrderService;
import com.yejin.exam.wbook.domain.rebate.entity.RebateOrderItem;
import com.yejin.exam.wbook.domain.rebate.repository.RebateOrderItemRepository;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RebateService {
    private final OrderService orderService;
    private final MemberService memberService;
    private final RebateOrderItemRepository rebateOrderItemRepository;

    @Transactional
    public ResultResponse makeDate(String yearMonth) {
        int monthEndDay = Util.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Util.date.parse(fromDateStr);
        LocalDateTime toDate = Util.date.parse(toDateStr);

        // 데이터 가져오기
        List<OrderItem> orderItems = orderService.findAllByPayDateBetweenOrderByIdAsc(fromDate, toDate);

        // 변환하기
        List<RebateOrderItem> rebateOrderItems = orderItems
                .stream()
                .map(this::toRebateOrderItem)
                .collect(Collectors.toList());

        // 저장하기
        rebateOrderItems.forEach(this::makeRebateOrderItem);

        return ResultResponse.of("MAKE_REBATE_DATA_OK", "정산데이터가 성공적으로 생성되었습니다.");
    }

    @Transactional
    private void makeRebateOrderItem(RebateOrderItem item) {
        RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

        if (oldRebateOrderItem != null) {
            rebateOrderItemRepository.delete(oldRebateOrderItem);
        }

        rebateOrderItemRepository.save(item);
    }

    private RebateOrderItem toRebateOrderItem(OrderItem orderItem) {
        return new RebateOrderItem(orderItem);
    }

    public List<RebateOrderItem> findRebateOrderItemsByPayDateIn(String yearMonth) {
        int monthEndDay = Util.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Util.date.parse(fromDateStr);
        LocalDateTime toDate = Util.date.parse(toDateStr);
        log.debug("[rebate] fromDate : "+fromDate + " toDate : "+ toDate);
        return rebateOrderItemRepository.findAllByPayDateBetweenOrderByIdAsc(fromDate, toDate);
    }

    @Transactional
    public ResultResponse rebate(long orderItemId) {
        Optional<RebateOrderItem> oRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(orderItemId);
        if(!oRebateOrderItem.isPresent()){
            return ResultResponse.of("REBATE_NO_ITEM_FAILED", "정산가능한 주문 품목이 없습니다.");
        }
        RebateOrderItem rebateOrderItem = oRebateOrderItem.get();

        if (rebateOrderItem.isRebateAvailable() == false) {
            return ResultResponse.of("REBATE_AVAILABLE_FAILED", "정산을 할 수 없는 상태입니다.");
        }

        int calculateRebatePrice = rebateOrderItem.calculateRebatePrice();

        CashLog cashLog = memberService.addCash(
                rebateOrderItem.getProduct().getAuthor(),
                calculateRebatePrice,
                "정산__%d__지급__예치금".formatted(rebateOrderItem.getOrderItem().getId())
        ).getData().getCashLog();

        rebateOrderItem.setRebateDone(cashLog.getId());

        return ResultResponse.of(
                "REBATE_FIN_OK",
                "주문품목번호 %d번에 대해서 판매자에게 %s원 정산을 완료하였습니다.".formatted(rebateOrderItem.getOrderItem().getId(), calculateRebatePrice),
                Util.mapOf(
                        "cashLogId", cashLog.getId()
                )
        );
    }
}