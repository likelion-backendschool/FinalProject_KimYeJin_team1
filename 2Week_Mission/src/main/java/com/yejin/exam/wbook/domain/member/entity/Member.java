package com.yejin.exam.wbook.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.File;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {

    @Column(nullable = false, length = 20, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "authLevel")
    @Enumerated(EnumType.ORDINAL)
    private MemberRole authLevel;

    public Member(long id) {
        super(id);
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.password=encryptedPassword;
    }
}