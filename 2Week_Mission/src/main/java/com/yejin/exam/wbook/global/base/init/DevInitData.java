package com.yejin.exam.wbook.global.base.init;

import com.yejin.exam.wbook.domain.cart.service.CartService;
import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.order.entity.Order;
import com.yejin.exam.wbook.domain.order.service.OrderService;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.service.PostService;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.domain.product.entity.ProductOption;
import com.yejin.exam.wbook.domain.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("dev")
public class DevInitData {
    @Bean
    CommandLineRunner initData(MemberService memberService, PostService postService, PasswordEncoder passwordEncoder,
                               ProductService productService, CartService cartService, OrderService orderService) {
        return args -> {

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

            Member member1=memberService.join(new MemberDto("user1","1234","1234","kyj011202@naver.com","author1"));
            for(int i =1;i<=10;i++){
                postService.write(member1,"제목%d".formatted(i),"내용%d".formatted(i),"내용%d".formatted(i),"#태그%d #태그%d".formatted(i,i+1));
            }

            Product product1 = productService.create(member1,"도서제목1",1234,1L,"#도서태그1 #도서태그2");
            Product product2 = productService.create(member1,"도서제목2",1234,2L,"#도서태그2 #도서태그3");
            Product product3 = productService.create(member1,"도서제목3",1234,3L,"#도서태그3 #도서태그4");
            Product product4 = productService.create(member1,"도서제목4",1234,4L,"#도서태그4 #도서태그5");

            memberService.addCash(member1, 10_000, "충전__무통장입금");
            memberService.addCash(member1, 20_000, "충전__무통장입금");
            memberService.addCash(member1, -5_000, "출금__일반");
            memberService.addCash(member1, 1_000_000, "충전__무통장입금");


            // 1번 주문 : 결제완료
            Order order1 = helper.order(member1, Arrays.asList(
                            product1,
                            product2
                    )
            );

            int order1PayPrice = order1.calculatePayPrice();
            orderService.payByRestCashOnly(order1);

            // 2번 주문 : 결제 후 환불
            Order order2 = helper.order(member1, Arrays.asList(
                            product3,
                            product4
                    )
            );

            orderService.payByRestCashOnly(order2);

            orderService.refund(order2);

            // 3번 주문 : 결제 전
            Order order3 = helper.order(member1, Arrays.asList(
                            product1,
                            product2
                    )
            );
        };
    }
}