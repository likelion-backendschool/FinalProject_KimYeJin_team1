package com.yejin.exam.wbook.domain.member.service;

import com.yejin.exam.wbook.domain.member.dto.MemberDto;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.entity.MemberRole;
import com.yejin.exam.wbook.domain.member.repository.MemberRepository;
import com.yejin.exam.wbook.domain.post.service.PostService;
import com.yejin.exam.wbook.global.exception.EntityAlreadyExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.yejin.exam.wbook.global.error.ErrorCode.USERNAME_ALREADY_EXIST;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PostService postService;

    public Member join(MemberDto memberDto) {
        if (memberRepository.existsByUsername(memberDto.getUsername())) {
            throw new EntityAlreadyExistException(USERNAME_ALREADY_EXIST);
        }
        final String username = memberDto.getUsername();

//        if (!emailCodeService.checkRegisterCode(username, memberDto.getEmail(), memberDto.getCode())) {
//            return false;
//        }

        final Member member = convertMemberDtoToMember(memberDto);
        memberRepository.save(member);

//        final SearchMember searchMember = new SearchMember(member);
//        searchMemberRepository.save(searchMember);

        // 축하 이메일 발송
        String subject = "[wbook] 회원가입을 축하합니다.";
        String text = "%s 님의 회원가입을 축하합니다.".formatted(member.getUsername());
        //emailService.sendMessage(member.getEmail(), subject,text);
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

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public void modify(Member member, String email, String nickname) {

        if(!member.getEmail().equals(email)){
            member.setEmail(email);
        }
        if(!member.getNickname().equals(nickname)){
            member.setNickname(nickname);
            if(member.getAuthLevel()!=MemberRole.ROLE_AUTHOR){
                member.setAuthLevel(MemberRole.ROLE_AUTHOR);
            }
        }
        memberRepository.save(member);
    }
    public void modifyPassword(Member member, String password) {
        member.setEncryptedPassword(passwordEncoder.encode(password));
        memberRepository.save(member);
    }

    public void setTempPassword(Member member) {

        String subject = "[wbook] %s 님의 임시 비밀번호 입니다.".formatted(member.getUsername());
        String tempPassword = UUID.randomUUID().toString().replace("-","");
        member.setEncryptedPassword(passwordEncoder.encode(tempPassword));
        String text = """
                        임시 비밀번호 : %s
                        위의 임시 비밀번호로 로그인 후, 비밀번호를 변경해 주세요.
                        """.formatted(tempPassword);

        emailService.sendMessage(member.getEmail(),subject,text);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }


    public void delete(Member member) {
        postService.findByAuthor(member).forEach(p -> postService.delete(p));
        memberRepository.delete(member);
    }
}
