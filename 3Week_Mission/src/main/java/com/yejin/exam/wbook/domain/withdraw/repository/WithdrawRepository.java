package com.yejin.exam.wbook.domain.withdraw.repository;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.withdraw.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {

    List<Withdraw> findAllByMember(Member member);
}
