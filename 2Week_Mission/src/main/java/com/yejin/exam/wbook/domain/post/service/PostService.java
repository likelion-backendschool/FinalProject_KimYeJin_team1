package com.yejin.exam.wbook.domain.post.service;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.entity.PostHashTag;
import com.yejin.exam.wbook.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostHashTagService postHashTagService;

    public Post write(Long authorId, String subject, String content,String contentHTML) {
        return write(new Member(authorId), subject, content,contentHTML);
    }
    public Post write(Long authorId, String subject, String content,String contentHTML,String hashTagsStr) {
        return write(new Member(authorId), subject, content,contentHTML,hashTagsStr);
    }
    public Post write(Member author, String subject, String contentHTML, String content) {
        return write(author, subject, content, contentHTML,"");
    }

    public Post write(Member author, String subject, String content, String contentHTML, String hashTagsStr) {
        Post post = Post
                .builder()
                .author(author)
                .subject(subject)
                .content(content)
                .contentHTML(contentHTML)
                .build();

        postRepository.save(post);

        postHashTagService.applyHashTags(author, post, hashTagsStr);
        log.debug("[post][write] hashtagsStr : "+hashTagsStr);
        List<PostHashTag> postHashTags = postHashTagService.getHashTags(post);
        log.debug("[post][write] hashtags : "+postHashTags);
        return post;
    }
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post getForPrintPostById(Long id) {
        Post post = getPostById(id);
        List<PostHashTag> postHashTags = postHashTagService.getHashTags(post);
        log.debug("[post] hashtags : "+postHashTags);
        post.getExtra().put("age__name__33", 22);
        post.getExtra().put("postHashTags",postHashTags);
        return post;
    }

    public void modify(Post post, String subject, String content) {
        post.setSubject(subject);
        post.setContent(content);
        postRepository.save(post);
    }
    public List<Post> getPosts(String username){
        return postRepository.findByAuthorUsername(username);
    }
    public List<Post> getForPrintPosts(List<Post> posts){
        for(Post post: posts){
            List<PostHashTag> postHashTags = postHashTagService.getHashTags(post);
            post.getExtra().put("postHashTags",postHashTags);
        }
        return posts;
    }
    public List<Post> getForPrintPostsByUsername(String username) {
        List<Post> posts = postRepository.getPostsByUsername(username);
        return getForPrintPosts(posts);
    }
    public List<Post> getForPrintPostsByKeyword(String username, String keyword) {
        List<Post> posts = postRepository.getPostsByUsernameAndKeyword(username, keyword);
        return getForPrintPosts(posts);
    }


    public List<Post> getPostsOrderByCreatedTime() {
        return postRepository.getPostsOrderByCreatedTime();
    }

    public List<Post> findByAuthor(Member member) {
        return postRepository.findByAuthor(member);
    }

    public void delete(Post post) {

        postRepository.delete(post);
    }
    public boolean actorCanSee(Member actor, Post post) {
        if ( actor == null ) return false;
        if ( post == null ) return false;

        return post.getAuthor().getId().equals(actor.getId());
    }
}
