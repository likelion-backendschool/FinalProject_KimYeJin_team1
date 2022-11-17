package com.yejin.exam.wbook.domain.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ProductTag extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Product product;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    private Member member;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    private ProductKeyword productKeyword;
}