package com.yejin.exam.wbook.domain.member.repository.qdsl;

import com.yejin.exam.wbook.domain.member.entity.MemberRole;

public interface MemberRepositoryQuerydsl {
    public MemberRole findAuthLevelByUsername(String username);
}
