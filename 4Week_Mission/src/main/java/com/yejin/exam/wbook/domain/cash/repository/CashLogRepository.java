package com.yejin.exam.wbook.domain.cash.repository;

import com.yejin.exam.wbook.domain.cash.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashLogRepository extends JpaRepository<CashLog, Long> {
}