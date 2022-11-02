package com.yejin.exam.wbook.global.base.init;

import com.yejin.exam.wbook.domain.cart.service.CartService;
import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.entity.MemberRole;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.order.entity.Order;
import com.yejin.exam.wbook.domain.order.repository.OrderRepository;
import com.yejin.exam.wbook.domain.order.service.OrderService;
import com.yejin.exam.wbook.domain.post.service.PostService;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.domain.product.service.ProductService;
import com.yejin.exam.wbook.domain.rebate.service.RebateService;
import com.yejin.exam.wbook.domain.withdraw.dto.WithdrawApplyDto;
import com.yejin.exam.wbook.domain.withdraw.service.WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@Profile("test")
public class TestInitData {
    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            PostService postService,
            ProductService productService,
            CartService cartService,
            OrderService orderService,
            OrderRepository orderRepository,
            RebateService rebateService,
            WithdrawService withdrawService
    ) {
        return args -> {
            Member member1=memberService.join(new MemberDto("user1","1234","1234","kyj011202@naver.com","author1"));
            Member member2=memberService.join(new MemberDto("user2","1234","1234","kyj2212@gmail.com",null));
            Member memberAdmin = memberService.join(new MemberDto("admin","1234","1234","yejin123kim@gmail.com","admin"));
            memberService.setAuthLevel(memberAdmin,MemberRole.ROLE_ADMIN);

            postService.write(member1, "제목 1", "내용 1", "내용 1", "#IT# 프론트엔드 #HTML #CSS");
            postService.write(member1, "제목 2", "내용 2", "내용 2", "#IT #스프링부트 #자바");
            postService.write(member2, "제목 3", "내용 3", "내용 3", "#IT# 프론트엔드 #HTML #CSS");
            postService.write(member2, "제목 4", "내용 4", "내용 4", "#IT #스프링부트 #자바");
            postService.write(member1, "제목 5", "내용 5", "내용 5", "#IT #자바 #카프카");
            postService.write(member1, "제목 6", "내용 6", "내용 6", "#IT #프론트엔드 #REACT");
            postService.write(member2, "제목 7", "내용 7", "내용 7", "#IT# 프론트엔드 #HTML #CSS");
            postService.write(member2, "제목 8", "내용 8", "내용 8", "#IT #스프링부트 #자바");

            Product product1 = productService.create(member1, "상품명1", 30_000, "카프카", "#IT #카프카");
            Product product2 = productService.create(member2, "상품명2", 40_000, "스프링부트", "#IT #REACT");
            Product product3 = productService.create(member1, "상품명3", 50_000, "REACT", "#IT #REACT");
            Product product4 = productService.create(member2, "상품명4", 60_000, "HTML", "#IT #HTML");

            Product product5 = productService.create(member2, "상품명5", 90_000, "HTML", "#태그5 #태그6");
            Product product6 = productService.create(member2, "상품명6", 90_000, "HTML", "#태그5 #태그6");

            memberService.addCash(member1, 10_000, "충전__무통장입금");
            memberService.addCash(member1, 20_000, "충전__무통장입금");
            memberService.addCash(member1, -5_000, "출금__일반");
            memberService.addCash(member1, 1_000_000, "충전__무통장입금");

            memberService.addCash(member2, 2_000_000, "충전__무통장입금");

            class Helper {
                public Order order(Member member, List<Product> products) {
                    for (int i = 0; i < products.size(); i++) {
                        Product product = products.get(i);

                        cartService.addItem(member, product);
                    }

                    return orderService.createFromCart(member);
                }
            }

            Helper helper = new Helper();

            Order order1 = helper.order(member1, Arrays.asList(
                            product1,
                            product2
                    )
            );

            int order1PayPrice = order1.calculatePayPrice();
            orderService.payByRestCashOnly(order1);

            // 강제로 order1의 결제날짜를 1시간 전으로 돌린다.
            // 환불 테스트를 위해서
            order1.setPayDate(LocalDateTime.now().minusHours(1));
            orderRepository.save(order1);

            Order order2 = helper.order(member2, Arrays.asList(
                            product3,
                            product4
                    )
            );

            orderService.payByRestCashOnly(order2);

            Order order3 = helper.order(member2, Arrays.asList(
                            product1,
                            product2
                    )
            );

            log.debug("[testData] order3 : "+order3.getId());
            log.debug("[testData] order3 items : ");
            order3.getOrderItems().forEach(i -> System.out.println("[testData] "+i.getProduct().getSubject()));
            cartService.addItem(member1, product3);
            cartService.addItem(member1, product4);

            Order order4 = helper.order(member2, Arrays.asList(
                            product3,
                            product4
                    )
            );

            orderService.payByRestCashOnly(order4);

            Order order5 = helper.order(member2, Arrays.asList(
                            product3,
                            product4
                    )
            );

            Order order6 = helper.order(member2, Arrays.asList(
                            product5,
                            product6
                    )
            );
            //rebateService.makeDate("2022-11");

            withdrawService.apply(member1,new WithdrawApplyDto("우리은행","123412341234",10));

        };
    }
}