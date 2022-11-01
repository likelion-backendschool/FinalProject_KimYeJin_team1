package com.yejin.exam.wbook.rebate;

import com.yejin.exam.wbook.domain.cart.service.CartService;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.order.entity.Order;
import com.yejin.exam.wbook.domain.order.service.OrderService;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.domain.product.service.ProductService;
import com.yejin.exam.wbook.domain.rebate.entity.RebateOrderItem;
import com.yejin.exam.wbook.domain.rebate.service.RebateService;
import com.yejin.exam.wbook.global.result.ResultResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class RebateServiceTests {

    @Autowired
    RebateService rebateService;

    @Test
    @DisplayName("주문 아이템 정산 데이터 만들기")
    void t1() {

        ResultResponse makeDateResultResponse = rebateService.makeDate("2022-11");

        System.out.println(makeDateResultResponse.getResultCode() + " "+ makeDateResultResponse.getMessage());
        assertThat(makeDateResultResponse.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("구매 날짜 순으로 주문아이템 정산 데이터 리스트 보기")
    void t2() {


        List<RebateOrderItem> items = rebateService.findRebateOrderItemsByPayDateIn("2022-11");
        System.out.println("[testRebate] 정산 아이템 : "+ items.size());
        items.forEach(i -> System.out.println("구매자 : " + i.getBuyerName()+"상품명 : " + i.getProductSubject()+"구매날짜 : " + i.getPayDate()));
        assertThat(items.size()).isNotNull();
    }
}
