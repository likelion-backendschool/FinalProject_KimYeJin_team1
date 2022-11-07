package com.yejin.exam.wbook.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.yejin.exam.wbook.domain.post.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Getter
@NoArgsConstructor
public class BookChapterDto {
    private Long id;
    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String content;

    private String contentHTML;

    @QueryProjection
    public BookChapterDto(Post post){
        this.id=post.getId();
        this.subject=post.getSubject();
        this.content=post.getContent();
        this.contentHTML=post.getContentHTML();
    }
}
