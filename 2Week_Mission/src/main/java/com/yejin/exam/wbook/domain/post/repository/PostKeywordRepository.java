package com.yejin.exam.wbook.domain.post.repository;

import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
import com.yejin.exam.wbook.domain.post.repository.qdsl.PostKeywordRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostKeywordRepository extends JpaRepository<PostKeyword,Long>, PostKeywordRepositoryQuerydsl {
    Optional<PostKeyword> findByContent(String keywordContent);
}