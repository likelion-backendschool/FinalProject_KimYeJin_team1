package com.yejin.exam.wbook.domain.member.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberModifyDto {

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일의 형식이 맞지 않습니다")
    private String email;

    @Length(min = 2, max = 12, message = "필명은 2문자 이상 12문자 이하여야 합니다")
    private String nickname;
}
