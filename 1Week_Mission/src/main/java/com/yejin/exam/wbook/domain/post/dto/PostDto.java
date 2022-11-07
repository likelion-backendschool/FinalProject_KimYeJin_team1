package com.yejin.exam.wbook.domain.post.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class PostDto {
    @NotEmpty
    private String subject;
    @NotEmpty
    private String content;

    private String hashTagsStr;
}