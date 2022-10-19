package com.yejin.exam.wbook.domain.post.repository;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.repository.qdsl.PostRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl {
    List<Post> findByAuthorUsername(String username);

    List<Post> findByAuthor(Member member);

}