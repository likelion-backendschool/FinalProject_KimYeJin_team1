package com.yejin.exam.wbook.global.base.init;

import com.yejin.exam.wbook.domain.cart.service.CartService;
import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.service.PostService;
import com.yejin.exam.wbook.domain.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevInitData {
    @Bean
    CommandLineRunner initData(MemberService memberService, PostService postService, PasswordEncoder passwordEncoder, ProductService productService, CartService cartService) {
        return args -> {

            Member member1=memberService.join(new MemberDto("user1","1234","1234","kyj011202@naver.com","author1"));
            for(int i =1;i<=10;i++){
                postService.write(member1,"제목%d".formatted(i),"내용%d".formatted(i),"내용%d".formatted(i),"#태그%d #태그%d".formatted(i,i+1));
            }
            for(int i=1;i<=5;i++){

                productService.create(member1,"도서제목%d".formatted(i),1234,(long)i,"#도서태그%d #도서태그%d".formatted(i,i+1));
            }

            cartService.addItem(member1, productService.findById(0L).get(), 1);
            cartService.addItem(member1, productService.findById(1L).get(), 4);
            cartService.addItem(member1, productService.findById(2L).get(), 2);

        };
    }
}