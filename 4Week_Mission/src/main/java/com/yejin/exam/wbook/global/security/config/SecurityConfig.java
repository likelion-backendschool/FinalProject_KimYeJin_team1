package com.yejin.exam.wbook.global.security.config;


import com.yejin.exam.wbook.global.security.handler.AccessDeniedHandlerImpl;
import com.yejin.exam.wbook.global.security.jwt.JwtAuthenticationEntryPoint;
import com.yejin.exam.wbook.global.security.jwt.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
            "/robots.txt/**",
            "/actuator/**"
    }; // 정적 파일 인가 없이 모두 허용
    private static final String[] AUTH_WHITELIST_SWAGGER = {
            "/swagger/**",
            "/swagger-ui/**"
    };
    private static final String[] AUTH_ALL_LIST = {
            "/api/v1/member/join/**",
            "/api/v1/member/login/**",
            "/api/v1/member/findUsername/**",
            "/api/v1/member/findPassword/**",
            "/denied",
            "/"
    }; // 모두 허용
    private static final String[] AUTH_ADMIN_LIST = {
            "/api/v1/adm/**"
    }; // admin 롤 만 허용
    private static final String[] AUTH_AUTHENTICATED_LIST = {
            "/api/v1/**",
    }; // 인가 필요

//    private final MemberSecurityService memberSecurityService;
//    private final AuthenticationFailureHandler customFailureHandler;

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AccessDeniedHandlerImpl jwtAccessDeniedHandler;
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }


//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        daoAuthenticationProvider.setUserDetailsService(memberSecurityService);
//        return daoAuthenticationProvider;
//    }

//    @Bean
//    public AuthenticationSuccessHandler customSuccessHandler() {
//        return new CustomSuccessHandler("/");
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
                .requestMatchers((matchers) -> matchers.antMatchers(AUTH_WHITELIST_STATIC).antMatchers(AUTH_WHITELIST_SWAGGER))
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
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        ;
        http
                .cors().configurationSource(corsConfigurationSource());
        http
                .csrf()
                .disable();
        http
                .authorizeRequests()
                .mvcMatchers("/member/login/**").permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(AUTH_ALL_LIST).permitAll()
                .antMatchers(AUTH_AUTHENTICATED_LIST).authenticated()
                ;
        http
                .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));
        http
                .httpBasic().disable() // httpBaic 로그인 방식 끄기
                .formLogin().disable() // 폼 로그인 방식 끄기
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(STATELESS)
                )
                .addFilterBefore(
                        jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
        ;
//        http
//                .formLogin()
//                .loginPage("/member/login")
//                .loginProcessingUrl("/member/login")
//                .failureHandler(customFailureHandler);
//        http
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true);


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

//    @Bean
//    public AccessDeniedHandler accessDeniedHandler(){
//        AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
//        accessDeniedHandler.setErrorPage("/denied");
//        return accessDeniedHandler;
//    }
}