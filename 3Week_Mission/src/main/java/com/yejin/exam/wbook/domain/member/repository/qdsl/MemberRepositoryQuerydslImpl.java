package com.yejin.exam.wbook.domain.member.repository.qdsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yejin.exam.wbook.domain.member.entity.MemberRole;
import lombok.RequiredArgsConstructor;
import static com.yejin.exam.wbook.domain.member.entity.QMember.*;

@RequiredArgsConstructor
public class MemberRepositoryQuerydslImpl implements MemberRepositoryQuerydsl {
    private final JPAQueryFactory queryFactory;


    @Override
    public MemberRole findAuthLevelByUsername(String username) {
        return queryFactory
                .select(member.authLevel)
                .from(member)
                .where(member.username.eq(username))
                .fetchOne();
    }
}