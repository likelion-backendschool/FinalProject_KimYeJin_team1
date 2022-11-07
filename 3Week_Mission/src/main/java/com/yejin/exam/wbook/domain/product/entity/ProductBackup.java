package com.yejin.exam.wbook.domain.product.entity;

import com.yejin.exam.wbook.domain.cart.entity.CartItem;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
import com.yejin.exam.wbook.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ProductBackup extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Member author;
    @ManyToOne(fetch = LAZY)
    private PostKeyword postKeyword;
    private String subject;
    private int price;

    @OneToOne(fetch = LAZY)
    private Product product;

    public ProductBackup(Product product) {
        this.product = product;
        author = product.getAuthor();
        postKeyword = product.getPostKeyword();
        subject=product.getSubject();
        price = product.getPrice();
    }
}