package com.yejin.exam.wbook.domain.post.service;

import com.yejin.exam.wbook.domain.post.entity.PostKeyword;
import com.yejin.exam.wbook.domain.post.repository.PostKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostKeywordService {

    private final PostKeywordRepository keywordRepository;

    public PostKeyword save(String keywordContent) {
        Optional<PostKeyword> optKeyword = keywordRepository.findByContent(keywordContent);

        if ( optKeyword.isPresent() ) {
            return optKeyword.get();
        }

        PostKeyword keyword = PostKeyword
                .builder()
                .content(keywordContent)
                .build();

        keywordRepository.save(keyword);

        return keyword;
    }

    public PostKeyword findByContent(String content) {
        return keywordRepository.findByContent(content).get();
    }
}