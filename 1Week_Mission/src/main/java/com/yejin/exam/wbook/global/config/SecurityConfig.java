package com.yejin.exam.wbook.global.config;


import com.yejin.exam.wbook.domain.member.service.MemberSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /* 인가 구분을 위한 url path 지정 */
    private static final String[] AUTH_WHITELIST_STATIC = {
            "/css/**",
            "/fonts/**",
            "/image/**",
            "/images/**",
            "/img/**",
            "/js/**",
            "/scss",
            "/assets/**",
            "/error/**",
            "/new/**",
            "/manuals/**",
            "/sitemap.xml/**",
            "/robots.txt/**"
    }; // 정적 파일 인가 없이 모두 허용
    private static final String[] AUTH_ALL_LIST = {
            "/member/join/**",
            "/member/login/**",
            "/member/findUsername/**",
            "/member/findPassword/**",
            "/"
    }; // 모두 허용
    private static final String[] AUTH_ADMIN_LIST = {
            "/admin/**"
    }; // admin 롤 만 허용
    private static final String[] AUTH_AUTHENTICATED_LIST = {
            "/member/**",
            "/post/**",
    }; // 인가 필요

    private final MemberSecurityService memberSecurityService;
    //private final AuthenticationFailureHandler customFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(memberSecurityService);
        return daoAuthenticationProvider;
    }

//    @Bean
//    public AuthenticationSuccessHandler customSuccessHandler() {
//        return new CustomSuccessHandler("/main");
//    }

    /*  스프링에서 보안상의 이슈로 ignoring() 을 권장하지 않음.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception {
        return (web) -> web.ignoring().antMatchers(AUTH_WHITELIST_STATIC);
    }
    */
    @Bean
    @Order(0)
    SecurityFilterChain resources(HttpSecurity http) throws Exception {
        http
                .requestMatchers((matchers) -> matchers.antMatchers(AUTH_WHITELIST_STATIC))
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
                .requestCache().disable()
                .securityContext().disable()
                .sessionManagement().disable()
        ;
        return http.build();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource());
        http
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(AUTH_ALL_LIST).permitAll()
                .antMatchers(AUTH_AUTHENTICATED_LIST).authenticated();
        http
                .csrf()
                .disable();
        http
                .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));
        http
                .formLogin()
                .loginPage("/member/login");
        //.successHandler(customSuccessHandler())
        //.failureHandler(customFailureHandler);
        http
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true);
//        http
//                .exceptionHandling()
//                .accessDeniedPage("/restrict");

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(CorsConfiguration.ALL));
        configuration.setAllowedMethods(List.of(CorsConfiguration.ALL));
        configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}