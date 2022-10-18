package com.yejin.exam.wbook.global.base.init;

import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("test")
public class TestInitData {
    @Bean
    CommandLineRunner initData(MemberService memberService, PasswordEncoder passwordEncoder) {
        return args -> {
            Member member1 = Member.builder()
                    .username("user1")
                    .password("1234")
                    .email("kyj011202@naver.com")
                    .nickname(null)
                    .build();
            Member member2 = Member.builder()
                    .username("user2")
                    .password("1234")
                    .email("kyj2212@gmail.com")
                    .nickname("author2")
                    .build();
        };
    }
}