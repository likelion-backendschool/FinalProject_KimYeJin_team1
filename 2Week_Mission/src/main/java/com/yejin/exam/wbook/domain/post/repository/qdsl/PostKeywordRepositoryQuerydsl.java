package com.yejin.exam.wbook.domain.post.repository.qdsl;

import com.yejin.exam.wbook.domain.post.entity.PostKeyword;

import java.util.List;

public interface PostKeywordRepositoryQuerydsl {
    List<PostKeyword> getQslAllByAuthorId(Long authorId);
}
