package com.yejin.exam.wbook.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
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

    private long restCash;

    public Member(long id) {
        super(id);
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.password=encryptedPassword;
    }

    public List<GrantedAuthority> genAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authLevel.name()));

        // 닉네임을 가지고 있다면 작가의 권한을 가진다.
        if (StringUtils.hasText(nickname)) {
            authorities.add(new SimpleGrantedAuthority("AUTHOR"));
        }
        System.out.println("[member] authority : "+authorities);
        return authorities;
    }

    public String getName() {
        if (nickname != null) {
            return nickname;
        }

        return username;
    }

}