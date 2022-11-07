package com.yejin.exam.wbook.domain.withdraw.service;

import com.yejin.exam.wbook.domain.cash.entity.CashLog;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.rebate.entity.RebateOrderItem;
import com.yejin.exam.wbook.domain.withdraw.dto.WithdrawApplyDto;
import com.yejin.exam.wbook.domain.withdraw.entity.WithdrawApply;
import com.yejin.exam.wbook.domain.withdraw.repository.WithdrawApplyRepository;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithdrawService {

    private final WithdrawApplyRepository withdrawApplyRepository;
    private final MemberService memberService;
    @Transactional
    public WithdrawApply apply(Member member, WithdrawApplyDto withdrawApplydto){

        WithdrawApply withdraw = WithdrawApply.builder()
                .member(member)
                .bankName(withdrawApplydto.getBankName())
                .backAccountNo(withdrawApplydto.getBackAccountNo())
                .price(withdrawApplydto.getPrice())
                .isCanceled(false)
                .build();

        withdrawApplyRepository.save(withdraw);
        return withdraw;
    }

    public List<WithdrawApply> findByMember(Member member) {
       return withdrawApplyRepository.findAllByMember(member);
    }

    public List<WithdrawApply> findAll() {
        return withdrawApplyRepository.findAll();
    }

    public ResultResponse withdraw(long withdrawApplyId) {
        Optional<WithdrawApply> oWithdrawApply = withdrawApplyRepository.findById(withdrawApplyId);
        if(!oWithdrawApply.isPresent()){
            return ResultResponse.of("NO_WITHDRAW_APPLY_FAILED", "출금가능한 신청서가 없습니다.");
        }
        WithdrawApply withdrawApply = oWithdrawApply.get();

        if (!withdrawApply.isPaid()) {
            return ResultResponse.of("APPLY_PAID_FAILED", "이미 처리 되었습니다.");
        }
        int calculateWithdrawPrice =  withdrawApply.getPrice() * -1;
        CashLog cashLog = memberService.addCash(
                withdrawApply.getMember(),
                calculateWithdrawPrice,
                "출금__%d__지급__예치금".formatted(withdrawApplyId)
        ).getData().getCashLog();

        withdrawApply.setPay(cashLog.getId());

        return ResultResponse.of(
                "REBATE_FIN_OK",
                "출금 신청 번호 %d번에 대해서 출금 %d 원 정상 지급되었습니다.".formatted(withdrawApplyId, calculateWithdrawPrice),
                Util.mapOf(
                        "cashLogId", cashLog.getId()
                )
        );
    }
}
