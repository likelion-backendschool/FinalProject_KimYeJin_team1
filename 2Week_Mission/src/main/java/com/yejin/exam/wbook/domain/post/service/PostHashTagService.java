package com.yejin.exam.wbook.domain.post.service;

import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.domain.post.entity.PostHashTag;
import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
import com.yejin.exam.wbook.domain.post.repository.PostHashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostHashTagService {

    private final PostKeywordService postKeywordService;
    private final PostHashTagRepository postHashTagRepository;

    public List<PostHashTag> applyHashTags(Member member, Post post, String keywordContentsStr) {

        List<PostHashTag> oldHashTags = postHashTagRepository.findByPost(post);
//        List<HashTag> needToDelTags = new ArrayList<>();
        if(oldHashTags.size()!=0){
            resetHashTag(post);
        }
        List<String> keywordContents = Arrays.stream(keywordContentsStr.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

/*        for(HashTag oldHashTag : oldHashTags){
            String oldKeywordContent = oldHashTag.getKeyword().getContent();
            if(!keywordContents.contains(oldKeywordContent)){
                needToDelTags.add(oldHashTag);
            }
        }*/
        List<PostHashTag> newHashTags = new ArrayList<>();
        keywordContents.forEach(keywordContent -> {
            newHashTags.add(saveHashTag(member, post, keywordContent));
        });
        return newHashTags;
    }

    private PostHashTag saveHashTag(Member member, Post post, String keywordContent) {
        PostKeyword keyword = postKeywordService.save(keywordContent);

        Optional<PostHashTag> opHashTag = postHashTagRepository.findByPostIdAndKeywordId(post.getId(), keyword.getId());

        if (opHashTag.isPresent()) {
            return opHashTag.get();
        }

        PostHashTag postHashTag = PostHashTag.builder()
                .post(post)
                .member(member)
                .keyword(keyword)
                .build();

        postHashTagRepository.save(postHashTag);

        return postHashTag;
    }
    private void resetHashTag(Post post) {

        List<PostHashTag> postHashTags = postHashTagRepository.findByPost(post);
        for(PostHashTag postHashTag : postHashTags){
            postHashTagRepository.delete(postHashTag);
        }
    }
    public List<PostHashTag> getHashTags(Post post){
        return postHashTagRepository.findByPost(post);
    }

    public List<PostHashTag> getPostTags(long authorId, long postKeywordId) {
        return postHashTagRepository.findAllByMemberIdAndKeywordIdOrderByPost_idDesc(authorId, postKeywordId);
    }
}