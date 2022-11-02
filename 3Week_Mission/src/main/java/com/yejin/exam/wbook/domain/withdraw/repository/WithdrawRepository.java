package com.yejin.exam.wbook.domain.withdraw.repository;

import com.yejin.exam.wbook.domain.withdraw.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {

}
