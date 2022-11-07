package com.yejin.exam.wbook.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yejin.exam.wbook.domain.post.entity.Post;
import com.yejin.exam.wbook.global.base.entity.BaseEntity;
import com.yejin.exam.wbook.util.Util;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    @Column(columnDefinition = "TEXT")
    private String accessToken;
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
    public static Member fromMap(Map<String, Object> map) {
        return fromJwtClaims(map);
    }

    public static Member fromJwtClaims(Map<String, Object> jwtClaims) {
        long id = 0;

        if (jwtClaims.get("id") instanceof Long) {
            id = (long) jwtClaims.get("id");
        } else if (jwtClaims.get("id") instanceof Integer) {
            id = (long) (int) jwtClaims.get("id");
        }

        LocalDateTime createDate = null;
        LocalDateTime modifyDate = null;

        if (jwtClaims.get("createDate") instanceof List) {
            createDate = Util.date.bitsToLocalDateTime((List<Integer>) jwtClaims.get("createDate"));
        }

        if (jwtClaims.get("modifyDate") instanceof List) {
            modifyDate = Util.date.bitsToLocalDateTime((List<Integer>) jwtClaims.get("modifyDate"));
        }

        String username = (String) jwtClaims.get("username");
        String email = (String) jwtClaims.get("email");
        String accessToken = (String) jwtClaims.get("accessToken");

        return Member
                .builder()
                .id(id)
                .createDate(createDate)
                .modifyDate(modifyDate)
                .username(username)
                .email(email)
                .accessToken(accessToken)
                .build();
    }

    // 현재 회원이 가지고 있는 권한들을 List<GrantedAuthority> 형태로 리턴
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MEMBER"));

        return authorities;
    }

    public Map<String, Object> getAccessTokenClaims() {
        return Util.mapOf(
                "id", getId(),
                "createDate", getCreateDate(),
                "modifyDate", getModifyDate(),
                "username", getUsername(),
                "email", getEmail(),
                "authorities", getAuthorities()
        );
    }

    public Map<String, Object> toMap() {
        return Util.mapOf(
                "id", getId(),
                "createDate", getCreateDate(),
                "modifyDate", getModifyDate(),
                "username", getUsername(),
                "email", getEmail(),
                "accessToken", getAccessToken(),
                "authorities", getAuthorities()
        );
    }
}