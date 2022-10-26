package com.yejin.exam.wbook.domain.post.repository;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.entity.PostHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHashTagRepository  extends JpaRepository<PostHashTag,Long> {

    Optional<PostHashTag> findByPostIdAndKeywordId(Long articleId, Long keywordId);

    List<PostHashTag> findByPost(Post post);

}