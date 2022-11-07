package com.yejin.exam.wbook.domain.withdraw.entity;

import com.yejin.exam.wbook.domain.cash.entity.CashLog;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.global.base.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Table(name = "withdraw")
public class WithdrawApply extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Member member;
    private String bankName;
    private String backAccountNo;
    private int price;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private CashLog withdrawCashLog; // 출금에 관련된 내역
    private LocalDateTime withdrawDate;

    public WithdrawApply(long id) {
        super(id);
    }

    public boolean isPaid() { // 출금처리여부(지급여부)
        if (withdrawDate != null || withdrawCashLog != null) {
            return false;
        }
        return true;
    }
    public void setPay(Long cashLogId) {
        withdrawDate = LocalDateTime.now();
        this.withdrawCashLog = new CashLog(cashLogId);
    }

    private boolean isCanceled; // 취소여부
}