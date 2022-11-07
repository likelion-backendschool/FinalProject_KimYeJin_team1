package com.yejin.exam.wbook.withdraw;

import com.yejin.exam.wbook.domain.cart.service.CartService;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.order.entity.Order;
import com.yejin.exam.wbook.domain.order.service.OrderService;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.domain.product.service.ProductService;
import com.yejin.exam.wbook.domain.rebate.entity.RebateOrderItem;
import com.yejin.exam.wbook.domain.rebate.service.RebateService;
import com.yejin.exam.wbook.domain.withdraw.entity.WithdrawApply;
import com.yejin.exam.wbook.domain.withdraw.service.WithdrawService;
import com.yejin.exam.wbook.global.result.ResultResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class WithdrawServiceTests {

    @Autowired
    WithdrawService withdrawService;

    @Test
    @DisplayName("출금 신청서 리스트 보기")
    void t1() {


        List<WithdrawApply> withdrawApplies = withdrawService.findAll();
        System.out.println("[testWithdraw] 출금 신청서 사이즈 : "+ withdrawApplies.size());
        withdrawApplies.forEach(
                i -> {
                    System.out.println("신청자 : " + i.getMember().getUsername()+"가격 : " + i.getPrice());
                    assertThat(i.isApplied()).isTrue();
    });

    }
}
