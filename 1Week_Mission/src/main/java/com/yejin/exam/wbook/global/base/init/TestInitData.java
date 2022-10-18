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
            Member member1 = memberService.join(new MemberDto("user1", "1234", "user1@test.com","author1"));
            Member member2 = memberService.join(new MemberDto("user2", "1234", "user2@test.com","author2"));
        };
    }
}