package com.yejin.exam.wbook.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryProjection;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.domain.product.entity.ProductKeyword;
import com.yejin.exam.wbook.domain.product.entity.ProductTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.ManyToOne;

import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
public class ProductTagDto {
    private Long authorId;
    private String authorName;
    private String subject;
    private int price;

    private String productKeyword;

    @QueryProjection
    public ProductTagDto(ProductTag productTag){
        this.authorId=productTag.getMember().getId();
        this.authorName=productTag.getMember().getName();
        this.subject=productTag.getProduct().getSubject();
        this.price=productTag.getProduct().getPrice();
        this.productKeyword=productTag.getProductKeyword().getContent();
    }

}
