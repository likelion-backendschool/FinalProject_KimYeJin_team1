package com.yejin.exam.wbook.domain.post.entity;

import com.yejin.exam.wbook.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class PostHashTag extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private Post post;

    @ManyToOne
    @ToString.Exclude
    private PostKeyword keyword;
}