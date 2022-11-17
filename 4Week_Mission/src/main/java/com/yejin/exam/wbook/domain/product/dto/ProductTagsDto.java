package com.yejin.exam.wbook.domain.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.yejin.exam.wbook.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductTagsDto {
    private Long authorId;
    private String authorName;
    private String subject;
    private int price;

    private List<String> productKeywords=new ArrayList<>();

    @QueryProjection
    public ProductTagsDto(Product product){
        this.authorId=product.getAuthor().getId();
        this.authorName=product.getAuthor().getName();
        this.subject=product.getSubject();
        this.price=product.getPrice();
        this.productKeywords.add(product.getPostKeyword().getContent());
    }
}
