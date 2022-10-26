//package com.yejin.exam.wbook.domain.post.repository.qdsl;
//
//import com.querydsl.core.Tuple;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
//import lombok.RequiredArgsConstructor;
//
////import static com.yejin.exam.wbook.domain.post.entity.
//
//import java.util.List;
//
//@RequiredArgsConstructor
//public class PostKeywordRepositoryQuerydslImpl implements PostKeywordRepositoryQuerydsl{
//    private final JPAQueryFactory jpaQueryFactory;
//    @Override
//    public List<PostKeyword> getQslAllByAuthorId(Long authorId) {
//        List<Tuple> fetch = jpaQueryFactory
//                .select(postKeyword, postTag.count())
//                .from(postKeyword)
//                .innerJoin(postTag)
//                .on(postKeyword.eq(postTag.postKeyword))
//                .where(postTag.member.id.eq(authorId))
//                .orderBy(postTag.post.id.desc())
//                .groupBy(postKeyword.id)
//                .fetch();
//
//        return fetch.stream().
//                map(tuple -> {
//                    PostKeyword _postKeyword = tuple.get(postKeyword);
//                    Long postTagsCount = tuple.get(postTag.count());
//
//                    _postKeyword.getExtra().put("postTagsCount", postTagsCount);
//
//                    return _postKeyword;
//                })
//                .collect(Collectors.toList());
//    }
//    }
//}
