package com.yejin.exam.wbook.domain.post.repository.qdsl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
import lombok.RequiredArgsConstructor;

import static com.yejin.exam.wbook.domain.post.entity.QPostKeyword.*;
import static com.yejin.exam.wbook.domain.post.entity.QPostHashTag.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PostKeywordRepositoryQuerydslImpl implements PostKeywordRepositoryQuerydsl{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<PostKeyword> getQslAllByAuthorId(Long authorId) {
        List<Tuple> fetch = jpaQueryFactory
                .select(postKeyword, postHashTag.count())
                .from(postKeyword)
                .innerJoin(postHashTag)
                .on(postKeyword.eq(postHashTag.keyword))
                .where(postHashTag.member.id.eq(authorId))
                .orderBy(postHashTag.post.id.desc())
                .groupBy(postKeyword.id)
                .fetch();

        return fetch.stream().
                map(tuple -> {
                    PostKeyword _postKeyword = tuple.get(postKeyword);
                    Long postTagsCount = tuple.get(postHashTag.count());

                    _postKeyword.getExtra().put("postTagsCount", postTagsCount);

                    return _postKeyword;
                })
                .collect(Collectors.toList());
    }
}
