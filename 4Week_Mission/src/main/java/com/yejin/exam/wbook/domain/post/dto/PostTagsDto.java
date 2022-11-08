package com.yejin.exam.wbook.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.entity.PostHashTag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostTagsDto {

    private long id;
    private long authorId;
    private String authorName;
    private String subject;
    private String content;
    private String contentHTML;
    private List<String> postTags;

    @QueryProjection
    public PostTagsDto(Post post){
        this.id=post.getId();
        this.authorId=post.getId();
        this.subject=post.getSubject();
        this.content=post.getContent();
        this.contentHTML=post.getContentHTML();
        this.postTags=getPostTags(post);
    }
    private List<String> getPostTags(Post post){
        List<String> postTags = new ArrayList<>();
        List<PostHashTag> postHashTags = (List<PostHashTag>) post.getExtra().get("postHashTags");
        postHashTags.stream()
                .map(postHashTag -> postHashTag.getKeyword().getContent()
                )
                .forEach(content ->postTags.add(content));
        return postTags;
    }
}
