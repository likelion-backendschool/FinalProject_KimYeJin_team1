package com.yejin.exam.wbook.domain.cash.service;

import com.yejin.exam.wbook.domain.cash.entity.CashLog;
import com.yejin.exam.wbook.domain.cash.repository.CashLogRepository;
import com.yejin.exam.wbook.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashService {
    private final CashLogRepository cashLogRepository;

    public CashLog addCash(Member member, long price, String eventType) {
        CashLog cashLog = CashLog.builder()
                .member(member)
                .price(price)
                .eventType(eventType)
                .build();

        cashLogRepository.save(cashLog);

        return cashLog;
    }
}