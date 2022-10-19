package com.yejin.exam.wbook.domain.post.entity;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Post extends BaseEntity {

    @ManyToOne
    private Member author;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String content;

    private String contentHTML;

    public Post(long id) {
        super(id);
    }

}