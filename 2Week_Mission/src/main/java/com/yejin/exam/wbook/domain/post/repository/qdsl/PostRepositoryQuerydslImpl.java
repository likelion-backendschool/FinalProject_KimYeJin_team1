package com.yejin.exam.wbook.domain.post.repository.qdsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yejin.exam.wbook.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.yejin.exam.wbook.domain.post.entity.QPost.*;
import static com.yejin.exam.wbook.domain.post.entity.QPostKeyword.*;
import static com.yejin.exam.wbook.domain.post.entity.QPostHashTag.*;

@RequiredArgsConstructor
public class PostRepositoryQuerydslImpl implements PostRepositoryQuerydsl {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getPostsByUsernameAndKeyword(String username, String keyword) {
        return jpaQueryFactory
                .selectDistinct(post)
                .from(post)
                .leftJoin(postHashTag)
                .on(postHashTag.post.eq(post))
                .where(postHashTag.keyword.content.eq(keyword).and(post.author.username.eq(username)))
                .orderBy(post.createDate.desc())
                .fetch();
    }

    @Override
    public List<Post> getPostsByUsername(String username) {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .where(post.author.username.eq(username))
                .fetch();
    }

    @Override
    public List<Post> getPostsByKeyword(String keyword) {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .leftJoin(postHashTag)
                .on(postHashTag.post.id.eq(post.id))
                .where(postHashTag.keyword.content.eq(keyword))
                .fetch();
    }

    @Override
    public List<Post> getPostsOrderByCreatedTime() {

        return jpaQueryFactory
                .select(post)
                .from(post)
                .orderBy(post.createDate.desc())
                .limit(100)
                .fetch();
    }
}