package com.yejin.exam.wbook.domain.withdraw.service;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.withdraw.dto.WithdrawDto;
import com.yejin.exam.wbook.domain.withdraw.entity.Withdraw;
import com.yejin.exam.wbook.domain.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithdrawService {

    private final WithdrawRepository withdrawRepository;
    @Transactional
    public Withdraw apply(Member member, WithdrawDto withdrawDto){

        Withdraw withdraw = Withdraw.builder()
                .member(member)
                .bankName(withdrawDto.getBankName())
                .backAccountNo(withdrawDto.getBackAccountNo())
                .price(withdrawDto.getPrice())
                .isApplied(true)
                .isCanceled(false)
                .isPaid(false)
                .build();

        withdrawRepository.save(withdraw);
        return withdraw;
    }

    public List<Withdraw> findByMember(Member member) {
       return withdrawRepository.findAllByMember(member);
    }

    public List<Withdraw> findAll() {
        return withdrawRepository.findAll();
    }
}
