package com.yejin.exam.wbook.domain.member.service;

import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.entity.MemberRole;
import com.yejin.exam.wbook.domain.member.repository.MemberRepository;
import com.yejin.exam.wbook.global.exception.EntityAlreadyExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yejin.exam.wbook.global.error.ErrorCode.USERNAME_ALREADY_EXIST;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member join(MemberDto memberDto) {
        if (memberRepository.existsByUsername(memberDto.getUsername())) {
            throw new EntityAlreadyExistException(USERNAME_ALREADY_EXIST);
        }
        final String username = memberDto.getUsername();

//        if (!emailCodeService.checkRegisterCode(username, memberDto.getEmail(), memberDto.getCode())) {
//            return false;
//        }

        final Member member = convertMemberDtoToMember(memberDto);
//        final String encryptedPassword = bCryptPasswordEncoder.encode(member.getPassword());
//        member.setEncryptedPassword(encryptedPassword);
        memberRepository.save(member);

//        final SearchMember searchMember = new SearchMember(member);
//        searchMemberRepository.save(searchMember);

        // 축하 이메일 발송

        // 로그인
       return member;
    }
    private Member convertMemberDtoToMember(MemberDto memberDto) {
        Member member = Member.builder()
                .username(memberDto.getUsername())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .email(memberDto.getEmail())
                .nickname(memberDto.getNickname())
                .build();
        return setMemberRoleByNickname(member);
    }
    private Member setMemberRoleByNickname(Member member){
        if(member.getNickname()==null || member.getNickname().length()==0){
            member.setAuthLevel(MemberRole.ROLE_MEMBER);
            return member;
        }
        member.setAuthLevel(MemberRole.ROLE_AUTHOR);
        return member;
    }

    public void login(String username, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
