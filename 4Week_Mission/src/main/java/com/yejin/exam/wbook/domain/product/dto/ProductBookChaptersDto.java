package com.yejin.exam.wbook.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryProjection;
import com.yejin.exam.wbook.domain.post.dto.BookChapterDto;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
@Getter
@NoArgsConstructor
public class ProductBookChaptersDto{

    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long authorId;
    private String authorName;
    private String subject;
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<BookChapterDto> bookChapters;
    private int price;

    @QueryProjection
    public ProductBookChaptersDto(Product product,List<Post> posts){
        this.id=product.getId();
        this.createDate=product.getCreateDate();
        this.modifyDate=product.getModifyDate();
        this.authorId=product.getAuthor().getId();
        this.authorName=product.getAuthor().getName();
        this.bookChapters=getBookChapters(posts);
        this.price=product.getPrice();
    }

    private List<BookChapterDto> getBookChapters(List<Post> posts){
        List<BookChapterDto> bookChaptersDtos = new ArrayList<>();
        posts.stream()
                .map(post -> new BookChapterDto(post)
                        )
                .forEach(bookChaptersDto -> bookChaptersDtos.add(bookChaptersDto));
        return bookChaptersDtos;
    }
}
