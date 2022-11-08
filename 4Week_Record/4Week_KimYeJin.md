## [4Week] 김예진

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
cart/    cash/    home/    member/  mybook/  order/   post/    product/ rebate/  withdraw/


```
<br>

### [도메인별 체크리스트]



### [지난 주 필수 기능]
- [x] 출금 기능 


### [필수기능]
- [x] jwt 회원 로그인 구현
- [x] jwt 회원 정보 디테일 구현
- [x] mybook 리스트 구현
- [x] mybook 디테일 구현
- [x] spring doc 으로 API 문서화


### [추가기능]
- [x] REST API 로 구현
- [ ] 엑세트 토큰 화이트리스트 구현



<br> 

### 4주차 미션 요약

---

**[접근 방법]**

### spring security + jwt

<br>

### 1. PasswordEncoder 빈 생성 위치에 따른 cycle 에러 발생
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

<br>

### 2. jwt 토큰 이용한 로그인, 회원정보 확인

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

<br>

### MyBook 도메인 REST API

<br>

### 3. mybooks 의 ManyToOne 필드들의 무한 참조 이슈
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

<br>

### 4. 유효하지 않은 자격 증명의 경우 예외처리
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

<br>

### 5. Dto를 이용하여 원하는 데이터 추출

요구사항 json 데이터와 동일하게 출력하기 위하여  
`MyBook, Product, Post(bookChapter)` 데이터의 필요부분만 추출하여 dto를 생성하였다.
`builder`를 두지않고 간단히 생성자를 이용하였으나, refactoring 해본다면 builder 등을 이용하여 좀더 통일성을 줄 수 있을 것 같다. 

기존의 MyBook 엔티티와 다르게 `Product`가 아닌 `ProductBookChaptersDto`를 가진 Dto.  

```java
@Getter
@NoArgsConstructor
public class MyBookDto {

    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long ownerId;
    private ProductBookChaptersDto product;

    @QueryProjection
    public MyBookDto(MyBook myBook, Product product, List<Post> bookChapters) {
        this.id=myBook.getId();
        this.createDate=myBook.getCreateDate();
        this.modifyDate=myBook.getModifyDate();
        this.ownerId = myBook.getOwnerId();
        this.product = new ProductBookChaptersDto(product,bookChapters);
    }
}
```

기존의 Product 엔티니에서 `List<Post>` 에 해당하는 `List<BookChapterDto>` 를 포함하는 dto.  
`getBookChapters(List<Post>)` 메소드를 통해 스트림을 이용하여 `BookChapterDto`를 얻도록 하였다.  
```java
@Getter
@NoArgsConstructor
public class ProductBookChaptersDto{

    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long authorId;
    private String authorName;
    private String subject;
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<BookChapterDto> bookChapters;
    private int price;

    @QueryProjection
    public ProductBookChaptersDto(Product product,List<Post> posts){
        this.id=product.getId();
        this.createDate=product.getCreateDate();
        this.modifyDate=product.getModifyDate();
        this.authorId=product.getAuthor().getId();
        this.authorName=product.getAuthor().getName();
        this.subject=product.getSubject();
        this.bookChapters=getBookChapters(posts);
        this.price=product.getPrice();
    }

    private List<BookChapterDto> getBookChapters(List<Post> posts){
        List<BookChapterDto> bookChaptersDtos = new ArrayList<>();
        posts.stream()
                .map(post -> new BookChapterDto(post)
                        )
                .forEach(bookChaptersDto -> bookChaptersDtos.add(bookChaptersDto));
        return bookChaptersDtos;
    }
}
```

마지막으로 createDate, modifyDate, Author를 뺀 BookChapterDto.
```java
@Getter
@NoArgsConstructor
public class BookChapterDto {
    private Long id;
    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String content;

    private String contentHTML;

    @QueryProjection
    public BookChapterDto(Post post){
        this.id=post.getId();
        this.subject=post.getSubject();
        this.content=post.getContent();
        this.contentHTML=post.getContentHTML();
    }
}
```

![img5](https://i.imgur.com/pg6gW69.png)


### Swagger 적용

<br>

### 6. swagger2 적용 시 patchmatch 로 인한 patternsCondition = null 이슈
```text
springfox.documentation.spi.service.contexts.Orderings.patternsCondition is null 발생
```

application.yml 에 matching 전략을 ant path로 설정
```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant-path-matcher
```

swaager 설정 파일에서 patchs가 잘 적용되는지 확인  
PathSelector.any() 를 이용하여 모든 ant-path에 대하여 적용
```java
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.POST, responseMessages)
                .globalResponseMessage(RequestMethod.GET, responseMessages)
                .globalResponseMessage(RequestMethod.DELETE, responseMessages)
                .globalResponseMessage(RequestMethod.PUT, responseMessages)
                .apiInfo(apiInfo())
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }
```

<br>

### 7. swagger2를 이용하여 api response code 정보 추가  

`@ApiResponsees()` 어노테이션 이용하여 resultCode S는 성공 F 는 실패 M은 auth 관련 실패 로 나누었다.  
처음에는 resultCode를 "GET_MYBOOK_OK" 짧은 단어형태의 코드로 구현하였는데, 이렇게 문서화하여 조작하기 위해서는 더 간결한 코드가 맞다고 판단되어 수정하였다.

`@ApiImplicitParams` 어노테이션을 통해 특정 파라미터의 예시값, 
```java
    @ApiOperation(value = "토스페이먼츠 결제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d번 주문이 결제처리되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 예치금이 부족합니다.\n"),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "주문 PK", example = "1", required = true),
            @ApiImplicitParam(name = "paymentKey", value = "페이지", example = "1", required = true),
            @ApiImplicitParam(name = "orderId", value = "주문번호", example = "order__1__78893412342", required = true),
            @ApiImplicitParam(name = "amount", value = "페이 사용금액", example = "1000", required = true)
})
```
doc 문서에 적용한 코드 잘 나오는 것 확인
![img6](https://i.imgur.com/mH8C0mc.png)

<br>

### 8. referer에서 가져오던 yearMonth -> orderItem의 payData에서 파싱으로 변경 

기존의 헤더에서 referer의 param에서 가져오던 yearMonth를 정산 아이템의 paydate 값에서 가져오는 로직으로 변경하였다.    
```java
        String yearMonth = rebateOrderItem.getPayDate().format(DateTimeFormatter.ofPattern("YYYY-MM"));

        return ResultResponse.of(
        "S001",
        "주문품목번호 %d번에 대해서 판매자에게 %s원 정산을 완료하였습니다.".formatted(rebateOrderItem.getOrderItem().getId(), calculateRebatePrice),
        Util.mapOf(
        "cashLogId", cashLog.getId(),"yearMonth",yearMonth
        )
        );
```
파싱한 yearMonth 값을 응답 data에 추가하였다.  
![img7](https://i.imgur.com/tPXLv8N.png)

단점은 여러건의 rebate를 할때 yearMonth를 가져오는 로직이 중첩된다.  
이를 위해 controller에서 request param으로 받는 방법도 고려해 봐야햘 것 같다.


<br>


### 9. 도서 상세조회, tag 조회를 위한 dto 추가 생성

mybook과 동일하게 tag에 대한 중복 이슈로 인하여 출력 dto 따로 작성
```java
// 도서 상세 조회를 위한 postTagsDto
@Getter
@NoArgsConstructor
public class ProductTagsDto {
    private Long authorId;
    private String authorName;
    private String subject;
    private int price;

    private List<String> productKeywords=new ArrayList<>();

    @QueryProjection
    public ProductTagsDto(Product product){
        this.authorId=product.getAuthor().getId();
        this.authorName=product.getAuthor().getName();
        this.subject=product.getSubject();
        this.price=product.getPrice();
        this.productKeywords.add(product.getPostKeyword().getContent());
    }
}

// 출력 data에 해당 productTagasDto로 변환하여 전달
    List<Product> products = productService.findAllForPrintByOrderByIdDesc(author);
    List<ProductTagsDto> productTagsDtos = new ArrayList<>();
    products.stream()
            .map(product -> new ProductTagsDto(product))
            .forEach(productTagsDto -> productTagsDtos.add(productTagsDto));
    return Util.spring.responseEntityOf(ResultResponse.successOf("S001","도서 조회에 성공하였습니다.",productTagsDtos));
```

<br>

### 10. 주문 목록에 포함된 도서 상품은 삭제하지 못하도록 수정  

order item에 존재하는 도서는 삭제하지 못하는 예외처리 추가  

```java
// 서비스
        if(orderService.existsByProduct(product.getId())){
            return false;
        }
        productRepository.delete(product);
        return true;

// 컨트롤러
        if(!productService.remove(product)){
            return Util.spring.responseEntityOf(ResultResponse.successOf("F002","주문 목록에 포함되어 있는 상품입니다.",null));
        }
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d번 도서가 삭제되었습니다.",id));
```

주문목록에 존재하는 도서를 삭제할 경우 fail 메세지로 응답
![img10](https://i.imgur.com/pPAXcU5.png)

<br>


### Refcatoring 시 추가적으로 구현하고 싶은 부분  

1. 정산 data에 대한 json 응답 dto 로 변경  
현재는 `@JsonIgnore`을 이용하여 중복되는 엔티티의 항목은 제외하는 형태로 출력하였으나, 리팩토링시 Mybook에서와 같이 json 응답 dto로 구현해 볼 예정.

<br>

### 궁금한 점

<br>

#### 1. handler와 entrypoint로 예외처리 시 responseentiry의 형태로 예외처리 가능한지  

    -> REST API 에서의 예외처리 방식은 어떻게 되는가?

<br>

