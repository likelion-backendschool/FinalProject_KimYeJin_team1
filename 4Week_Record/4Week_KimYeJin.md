## [3Week] 김예진

### 미션 요구사항 분석 & 체크리스트

---

### [미션 요구사항 분석]
미션 요구사항에 대하여 아래 2가지 기준에 따라 분석하였습니다. 
1. 전체 패키징(도메인 등)을 어떻게 구성할 것인지
2. 도메인별 URl 구성, mvc 구성을 어떻게 하고, 어떤 체크리스트에 따라 구현할지

<br>

### [전체 패키징 구성]

```
util/

global
base/      config/    error/     exception/ request/   result/    security/ 

domain
cart/    cash/    home/    member/  mybook/  order/   post/    product/ rebate/  


```
<br>

### [도메인별 체크리스트]

이번주 정산 도메인은 아래 참고 자료를 확인하여 작성하였습니다.  
[https://techblog.woowahan.com/2711/](https://techblog.woowahan.com/2711/)  

#### **<Rebate 도메인>**
#### **<Withdraw 도메인>**


### [지난 주 필수 기능]
- [x] 출금 기능 


### [필수기능]
- [x] jwt 회원 로그인 구현
- [x] jwt 회원 정보 디테일 구현


### [추가기능]
- [ ] 정산데이터 배치로 생성


<br>
<br> 

### 3주차 미션 요약

---

**[접근 방법]**

### jwt

1. PasswordEncoder 빈 생성 위치에 따른 cycle 에러 발생
- 기존 위치 : SecurityConfig 내의 빈
```java
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

```

SecurityConfig에 passwordEncoder 빈을 생성 시 빈 생성에 cycle이 발생한다.

JwtFilter 내의 MemberService가 실행되고, MemberService 내의 passwordEncdoer가 실행되고, 다시 SecurityConfig의 passwordEncoder가 실행되면서 JwtFilter클래스의 빈이 다시 호출되는 형태로 보인다.
따라서 전혀 생성자 주입으로 연관되지 않은 Application 클래스에 공통적으로 상용되는 빈을 생성시, 해당 이슈를 피해갈 수 있었다.

- 변경된 위치 : Application 내의 빈
```java
public class WbookApplication {
    
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
```

2. jwt 토큰 이용한 로그인, 회원정보 확인

Spring Security의 securityContext 를 사용하여 PreAuthorize() 등의 인증/인가 모듈을 사용하기 위하여
JwtProvider에서 생성한 token으로 부터 Authentication을 가져와서 SecurityContextHolder에 해당 Authentication을 저장하는 방법을 사용하였다.  

JwtAuthorizationFilter - 신규 로그인 정보 생성 로직
```java
        MemberContext memberContext = new MemberContext(member,member.genAuthorities());
        log.debug("[jwtFilter] context : " + memberContext.getName());
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        memberContext,
                        null,
                        memberContext.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
```

JwtAuthorizationFilter - 헤더의 토큰으로부터 로그인 정보 판단 로직
```java

        // 1. Request Header 에서 토큰을 꺼냄
        String token = resolveToken(req);
        log.debug("[jwtFilter] token : " + token);
        // 2. validateToken 으로 토큰 유효성 검사
        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 MemberContext 에 저장
        if (token!=null && jwtProvider.verify(token)) {
            log.debug("[jwtFilter] provider verify ok : " + jwtProvider.verify(token));

            Authentication authentication = jwtProvider.getAuthentication(token);
            log.debug("[jwtFilter] authentication: " + authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("[jwtFilter] securifycontext: " + SecurityContextHolder.getContext().getAuthentication().getName());
            Map<String, Object> claims = jwtProvider.getClaims(token);
            String username = (String) claims.get("username");
            Member member = memberService.findByUsername(username).orElseThrow(
                    () -> new UsernameNotFoundException("'%s' Username not found.".formatted(username))
            );

            forceAuthentication(member);
        }

```
Jwt provider의 token으로부터 authentication 가져오는 로직
UserDetail 객체를 직접 생성 (기존 UserDetailService를 상속받았던 것과 유사)
```java
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
```

![img1](https://i.imgur.com/sjN7v59.png)


```java
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultResponse> me(@AuthenticationPrincipal MemberContext memberContext) {
        if (memberContext == null) {
            return Util.spring.responseEntityOf(ResultResponse.failOf("GET_PROFILE_FAILED","로그인이 필요합니다.",null));
        }

        return Util.spring.responseEntityOf(ResultResponse.successOf("GET_PROFILE_OK","사용자 프로필",memberContext));
    }
```
회원정보 테스트 확인 시 accessToken=null 발생.   
하지만 postMan으로 테스트 시 정상. 테스크 코드를 추후 수정필요.
![img2](https://i.imgur.com/WpRXBdb.png)

3. mybooks 의 ManyToOne 필드들의 무한 참조 이슈
오류 메세지
```text
(through reference chain: com.yejin.exam.wbook.global.result.ResultResponse["data"]->java.util.ArrayList[0]->com.yejin.exam.wbook.domain.mybook.entity.MyBook["product"]->com.yejin.exam.wbook.domain.member.entity.Member$HibernateProxy$jBUFbZrX["hibernateLazyInitializer"])
```
Product, OrderItem 필드가 내부에 Member를 다시 참조하고 있기 때문에 무한 참조 에러가 발생.  
따라서 @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 어노테이션을 추가.

추가로 기존의 Member 타입의 필드를 api 요구사항에 맞추어 Long타입의 ownerId로 변경
```java

    private Long ownerId;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JsonIgnore
    private OrderItem orderItem;
```
api 요구사항의 날짜 json 형태와 조금 다른 형태로 표출됨. --> 추후 수정 필요
![img3](https://i.imgur.com/es3YK73.png)

4. 유효하지 않은 자격 증명의 경우 예외처리
기존의 AccessDeniedHandler는 403 authority 가 없는 경우만 예외처리 됨.  
유효한 Authentication 없는 경우(token==null) 401 인 경우 예외처리 추가.

AuthenticationEntryPoint 클래스를 상속받은 JwtAuthenticationEntryPoint 클래스를 적용.

```java
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때
        log.debug("[accessDeniedHandler] error : "+authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,authException.getMessage());
    }
```
SecurityConfig에 JwtauthenticationEntryPoint 추가
```java
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        http
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler())
                ;
```

### Refcatoring 시 추가적으로 구현하고 싶은 부분  


### 궁금한 점
1. handler와 entrypoint로 예외처리 시 responseentiry의 형태로 예외처리 가능한지  

    -> REST API 에서의 예외처리 방식은 어떻게 되는가?

<br>

3. 추가 기능 출금 구현
