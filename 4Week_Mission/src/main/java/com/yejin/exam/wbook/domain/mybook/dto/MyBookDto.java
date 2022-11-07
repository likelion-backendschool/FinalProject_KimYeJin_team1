package com.yejin.exam.wbook.domain.mybook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.yejin.exam.wbook.domain.mybook.entity.MyBook;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.product.dto.ProductBookChaptersDto;
import com.yejin.exam.wbook.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyBookDto {

    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long ownerId;
    private ProductBookChaptersDto product;

    @QueryProjection
    public MyBookDto(MyBook myBook, Product product, List<Post> bookChapters) {
        this.id=myBook.getId();
        this.createDate=myBook.getCreateDate();
        this.modifyDate=myBook.getModifyDate();
        this.ownerId = myBook.getOwnerId();
        this.product = new ProductBookChaptersDto(product,bookChapters);
    }
}
