package com.yejin.exam.wbook.domain.withdraw.entity;

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
    private boolean isApplied; // 신청여부
    private boolean isCanceled; // 취소여부
    private boolean isPaid; // 출금처리여부(지급여부)
}