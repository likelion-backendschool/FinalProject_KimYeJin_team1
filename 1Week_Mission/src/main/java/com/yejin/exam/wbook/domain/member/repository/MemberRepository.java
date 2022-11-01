package com.yejin.exam.wbook.domain.member.repository;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.entity.MemberRole;
import com.yejin.exam.wbook.domain.member.repository.qdsl.MemberRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository
        extends JpaRepository<Member, Long>, MemberRepositoryQuerydsl {

    public boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);

    Optional<Member> findByEmail(String email);

}
