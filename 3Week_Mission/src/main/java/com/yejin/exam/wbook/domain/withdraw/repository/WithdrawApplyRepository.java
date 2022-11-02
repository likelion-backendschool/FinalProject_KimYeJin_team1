package com.yejin.exam.wbook.domain.withdraw.repository;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.withdraw.entity.WithdrawApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WithdrawApplyRepository extends JpaRepository<WithdrawApply, Long> {

    List<WithdrawApply> findAllByMember(Member member);


}
