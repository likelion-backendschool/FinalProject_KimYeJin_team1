package com.yejin.exam.wbook;

import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.entity.MemberRole;
import com.yejin.exam.wbook.domain.member.repository.MemberRepository;
import com.yejin.exam.wbook.domain.member.service.EmailService;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MemberTests {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailService emailService;
    @Test
    @DisplayName("회원 생성시, member role에 따라 authlevel이 db에 저장되는지 확인")
    public void test_authLevel_convert(){

        memberService.join(new MemberDto("roletest1","1234","1234","roletest1@mail.com",null));
        memberService.join(new MemberDto("roletest2","1234","1234", "roletest2@mail.com","authortest2"));
        MemberRole authLevel1 = memberRepository.findAuthLevelByUsername("roletest1");
        MemberRole authLevel2 = memberRepository.findAuthLevelByUsername("roletest2");

        assertThat(authLevel1).isEqualTo(MemberRole.ROLE_MEMBER);
        assertThat(authLevel2).isEqualTo(MemberRole.ROLE_AUTHOR);

    }

    @Test
    @DisplayName("이메일 전송 테스트")
    public void test_email_send(){
        emailService.sendMessage("kyj2212@gmail.com","test","test");
    }

    @Test
    @DisplayName("임시 비밀번호 테스트")
    public void test_tempPassword(){
        memberService.setTempPassword(memberService.findByUsername("user1").get());
    }


}
