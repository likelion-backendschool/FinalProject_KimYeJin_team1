package com.yejin.exam.wbook.domain.post.repository.qdsl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.yejin.exam.wbook.domain.post.entity.QPostKeyword.*;
import static com.yejin.exam.wbook.domain.post.entity.QPostHashTag.*;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
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
                    log.debug("[postkeyword] tuple : "+tuple);

                    PostKeyword _postKeyword = tuple.get(postKeyword);
                    log.debug("[postkeyword] postkeyword : "+_postKeyword);
                    Long postTagsCount = tuple.get(postHashTag.count());
                    log.debug("[postkeyword] postkeyword : "+postTagsCount);

                    _postKeyword.getExtra().put("postTagsCount", postTagsCount);

                    return _postKeyword;
                })
                .collect(Collectors.toList());
    }
}
