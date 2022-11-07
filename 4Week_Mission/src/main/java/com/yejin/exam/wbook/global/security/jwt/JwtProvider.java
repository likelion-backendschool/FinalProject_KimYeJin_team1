package com.yejin.exam.wbook.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yejin.exam.wbook.global.error.ErrorResponse;
import com.yejin.exam.wbook.global.exception.BaseException;
import com.yejin.exam.wbook.global.security.handler.AccessDeniedHandlerImpl;
import com.yejin.exam.wbook.util.Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${custom.jwt.secretKey}")
    private String secretKeyPlain;

    private SecretKey jwtSecretKey;
    private final UserDetailsService userDetailsService;
    @PostConstruct
    private void init(){
        System.out.println("[PostConstruct] init Jwt secret key");
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        jwtSecretKey= Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }
    private SecretKey getSecretKey(){
        return jwtSecretKey;
    }

    public String generateAccessToken(Map<String, Object> claims, long seconds) {
        long now = new Date().getTime();
        Date accessTokenExpiresIn = new Date(now + 1000L * seconds);

        return Jwts.builder()
                .claim("body", Util.json.toStr(claims))
                .setExpiration(accessTokenExpiresIn)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication getAuthentication(String token) {

        Map<String,Object> claims = getClaims(token);
        log.debug("claims : "+ claims);
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // UserDetails 객체를 만들어서 Authentication 리턴

        User principal = new User(claims.get("username").toString(),"",authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);

    }

    private Claims parseClaimsJws(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean verify(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            log.debug("[jwt provider] error : "+e.getMessage());
            return false;
        }

        return true;
    }
    public Claims getClaimsC(String token) {
        String body = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("body", String.class);

        return (Claims) Util.json.toMap(body);
    }
    public Map<String, Object> getClaims(String token) {
        String body = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("body", String.class);

        return Util.json.toMap(body);
    }

}