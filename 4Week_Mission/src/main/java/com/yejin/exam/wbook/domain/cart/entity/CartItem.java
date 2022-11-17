package com.yejin.exam.wbook.domain.cart.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yejin.exam.wbook.domain.member.entity.Member;
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
public class CartItem extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Member buyer;
    @ManyToOne(fetch = LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    public CartItem(long id) {
        super(id);
    }
}