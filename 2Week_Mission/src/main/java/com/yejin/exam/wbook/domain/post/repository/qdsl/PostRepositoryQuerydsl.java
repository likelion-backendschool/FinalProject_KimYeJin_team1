package com.yejin.exam.wbook.domain.post.repository.qdsl;

import com.yejin.exam.wbook.domain.post.entity.Post;
import java.util.List;
public interface PostRepositoryQuerydsl {
    List<Post> getPostsByUsernameAndKeyword(String username, String keyword);
    List<Post> getPostsByUsername(String username);

    List<Post> getPostsByKeyword(String keyword);

    List<Post> getPostsOrderByCreatedTime();
}
