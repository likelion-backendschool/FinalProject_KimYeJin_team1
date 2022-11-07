package com.yejin.exam.wbook.domain.member.service;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.entity.MemberRole;
import com.yejin.exam.wbook.domain.member.repository.MemberRepository;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).get();

        return new MemberContext(member, member.genAuthorities());
    }
//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        log.debug("[login] username : " + username);
//        Member member = memberRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//
//        log.debug("[login] member password : " + member.getPassword());
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        if (member.getAuthLevel().equals(MemberRole.ROLE_MEMBER)) {
//            authorities.add(new SimpleGrantedAuthority(MemberRole.ROLE_MEMBER.name()));
//        }
//        if(member.getAuthLevel().equals(MemberRole.ROLE_AUTHOR)){
//            authorities.add(new SimpleGrantedAuthority(MemberRole.ROLE_AUTHOR.name()));
//        }
//        return new User(member.getUsername(), member.getPassword(), authorities);
//    }

}