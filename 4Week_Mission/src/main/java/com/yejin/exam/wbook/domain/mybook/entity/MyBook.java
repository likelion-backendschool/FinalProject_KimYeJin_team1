package com.yejin.exam.wbook.domain.mybook.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.order.entity.OrderItem;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.global.base.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class MyBook extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Member owner;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JsonIgnore
    private OrderItem orderItem;
}