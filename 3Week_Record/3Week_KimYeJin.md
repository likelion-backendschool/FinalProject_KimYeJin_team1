## [2Week] 김예진

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
- [x] 장바구니 내의 상품 주문
- [x] 기존 회원이 가지고 있는 cash를 이용한 결제
- [x] 토스페이를 이용한 결제
- [x] 결제 이후 환불 가능


### [필수기능]
- [x] 관리자 회원, 관리자 페이지
- [x] 정산데이터 생성
- [x] 건별, 전체(선택) 정산 처리

### [추가기능]
- [x] 정산데이터 배치로 생성
- [ ] 출금 기능 // 구현 중

<br>
<br> 

### 3주차 미션 요약

---

**[접근 방법]**

### 지난주 보강
1. rq 클래스 도입  

url == null 인 상황 발생, RequestURI로 받도록 추가
```java
    public String redirectToBackWithMsg(String msg) {
        String url = req.getHeader("Referer");
        log.debug("[Rq] url : "+url);
        if(url==null){
            url=req.getRequestURI();
            log.debug("[Rq] requestURI : "+url);
        }
        return redirectWithMsg(url, msg);
    }
```

2. ResultCode 규칙  

코드의 끝이 OK 인 경우 Success로 나타내고, FAILED 등 일경우 fail로 판단
```java
    public boolean isSuccess() {
        return resultCode.endsWith("OK");
    }

    public boolean isFail() {
        return isSuccess() == false;
    }
```

결제 취소 시 ResultCode
```java
    public ResultResponse actorCanCancel(Member actor, Order order) {
        if ( order.isPaid() ) {
            return ResultResponse.of("IS_PAID_ORDER_CANCEL_FAIL", "이미 결제처리 되었습니다.");
        }

        if (order.isCanceled()) {
            return ResultResponse.of("IS_CANCELED_ORDER_CANCEL_FAIL", "이미 취소되었습니다.");
        }

        if (actor.getId().equals(order.getBuyer().getId()) == false) {
            return ResultResponse.of("NO_AUTH_ORDER_CANCEL_FAIL", "권한이 없습니다.");
        }

        return ResultResponse.of("ORDER_CANCEL_OK", "취소할 수 있습니다.");
    }

```

### 정산 도메인

    '정산'도 도메인인가? -> 도메인이다.  
[참고링크 : https://runa-nam.tistory.com/m/120](https://runa-nam.tistory.com/m/120)

<br>


1. 정산 서비스 테스트 -> OrderItem 이 없는 케이스가 들어간 경우

RebateService 의 rebate 메소드 에서 Optional 예외처리 추가
```java
        Optional<RebateOrderItem> oRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(orderItemId);
        if(!oRebateOrderItem.isPresent()){
            return ResultResponse.of("REBATE_NO_ITEM_FAILED", "정산가능한 주문 품목이 없습니다.");
        }
        RebateOrderItem rebateOrderItem = oRebateOrderItem.get();

```

테스트 코드로 Oder Item이 없는 경우 체크
```java
    @Test
    @DisplayName("주문 item 모두 정산하기 ")
    void t4() {
        String ids = "1,2,3,4,7,8";
        String[] idsArr = ids.split(",");
        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    ResultResponse rebateResultResponse = rebateService.rebate(id);
                    System.out.println(rebateResultResponse.getResultCode() + " "+ rebateResultResponse.getMessage()+" "+rebateResultResponse.getData());
                    assertThat(rebateResultResponse.isSuccess()).isTrue();
                });

        ids = "5,6";
        idsArr = ids.split(",");
        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    ResultResponse rebateResultResponse = rebateService.rebate(id);
                    System.out.println(rebateResultResponse.getResultCode() + " "+ rebateResultResponse.getMessage()+" "+rebateResultResponse.getData());
                    assertThat(rebateResultResponse.isFail()).isTrue();
                });
    }
```


<br>

2. 정산 컨트롤러 테스트

컨트롤러에서 Referer를 통해 가져오는 url 쿼리 파라미터 값을 직접 지정하여 테스트코드를 작성하였다.

```java
    @Test
    @DisplayName("선택한 주문 아이템건에 대한 정산")
    @WithUserDetails("admin")
    void t5() throws Exception {
        // GIVEN
        String ids = "1,2,3,4,7,8";
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/adm/rebate/rebate")
                        .param("ids",ids)
                        .header("Referer","?yearMonth=2022-11"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(RebateController.class))
                .andExpect(handler().methodName("rebate"))
                .andExpect(redirectedUrlPattern("/adm/rebate/rebateOrderItemList?yearMonth=**&msg=**"));
    }
```
<br>

3. AccessDenied 예외처리  

관리자가 아닌 경우, 403 AccessDenied 가 발생 -> 예외처리를 AccessDeniedException 을 AccessDeniedHandler 를 통해 앞단에서 먼저 처리하도록 하였다.

먼저 아래와 같이 AccessDeniedHandler 인터페이스를 상속하는 AccessDeniedHandlerImpl 클래스를 작성한다.
`response.sendRedirect()` 를 이용하여 원하는 errorPage로 리디렉션 하였다.  
에러메세지를 함께 넘기기 위하여 Rq 클래스의 url에 ErrorMsg를 쿼리 파라미터에 넣는 메소드를 이용하였다.  
초반에는 query 파라미터를 직접 입력하여 "exception"과 "message" 를 모두 넘겼으나, exception은 불필요하다 생각되어 다시 msg만 넘기는 것으로 수정하였다.  
  
```java
@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    private String errorPage;

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("[accessDeniedHandler] AccessDeniedException", accessDeniedException);
        String msg = "권한이 없습니다.";
        //response.sendError(HttpServletResponse.SC_FORBIDDEN, msg);
        log.debug("[accessDeniedHandler] error : "+accessDeniedException.getMessage());
        response.sendRedirect(Rq.urlWithErrorMsg(errorPage,accessDeniedException.getMessage()));
    }
}
```

errorPage 요청이 들어오는 컨트롤러 작성  

error 처리는 글로벌 범위에 속한다고 판단하여 global.error 에 컨트롤러 패키징을 추가하였다.  
일반적으로 에러페이지를 위한 컨트롤러는 어디에 배치하는지 궁금해졌다.

`global.error.controller.ErrorController` 는 아래와 같다.

```java
    @GetMapping("/denied")
    @ResponseBody
    public ResultResponse accessDenied(String errorMsg){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("[denied] auth username : "+ authentication.getName() + " authority : "+authentication.getAuthorities());
        log.debug("[denied] exception : "+ errorMsg);

        return ResultResponse.of("ACCESS_DENIED",errorMsg);
    }
```

Spring Security 내부에도 exception 처리에 대한 부분을 수정한다.  

SecurityConfig 는 아래와 같다.
```java
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // ... 생략 ..
        http
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler())
    }

    // hander 빈으로 생성 -> 생성자 주입으로 하여도 될것 같다.
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
        accessDeniedHandler.setErrorPage("/denied");
        return accessDeniedHandler;
    }
    
```


참고 자료  
[https://anjoliena.tistory.com/108](https://anjoliena.tistory.com/108)  
[https://velog.io/@rudwnd33/Spring-Security-AccessDeniedException](https://velog.io/@rudwnd33/Spring-Security-AccessDeniedException)  
[https://escapefromcoding.tistory.com/489](https://escapefromcoding.tistory.com/489)  


<br>

### batch

1. 정산 Job 

`@Scheduled (cron=)` 을 이용하여 매월 15일 4시에 실행되도록 설정  
`incrementer(new RunIdIncrementer())` 를 이용하여 run_id 라는 파라미터로 job 이 서로 다른 스케쥴로 인식되어 실행될 수 있도록 한다.

```java
    @Bean(JOB_NAME+"Job")
    @Scheduled(cron= "0 0 4 15 * ?"  )
    public Job makeRebateOrderItemJob(Step makeRebateOrderItemStep1, CommandLineRunner initData) throws Exception {
            initData.run();
            log.debug("[rebateJob] start");
            return jobBuilderFactory.get(JOB_NAME+"Job")
            .start(makeRebateOrderItemStep1)
            .incrementer(new RunIdIncrementer())
            .build();
    }
```
![img1](https://i.imgur.com/ufK5UPM.png)  


2. Job 파라미터  

job 파라미터를 확인 하면 아래와 같다.
java 실행 시 입력한 parameter 인 month = 2022-11 값을 기준으로 데이터를 정렬한다.
단, month 값이 들어오지 않을 시 default로 2022-11로 실행되도록 하였다.
```java
    @StepScope
    @Bean("orderItemReader")
    public JpaPagingItemReader<OrderItem> orderItemReader(@Value("#{jobParameters['month']}") String yearMonth) throws Exception{

        log.debug("[rebateJob] reader start");
        if(yearMonth==null){
            yearMonth="2022-11";
        }
        // .. 생략 ...
    }
```
![img3](https://i.imgur.com/Uu8PifK.png)

<br>
run configuration 에 아래와 같이 parameter를 지정하였다.  
(처음에 environment variable에 값을 넣고 parameter가 계속 null 이 나와 엄청난 삽질을 하였다...)  

![img4](https://i.imgur.com/fQ94KRV.png)

3. ItemReader : RepositoryItemReader -> JpaPagingItemReader  

처음에는 RepositoryItemReader로 OrderItemRespsitory의 `findAllByPayDateBetween` 메소드를 이용하였으나,
Reader 값이 반복적으로 null 이 나와 Processor(), writer()가 정상적으로 실행되지 못하였다.  

따라서 일반적으로 ItemReader 구현 클래스로 많이 사용하는 JpaPagingItemReader 클래스를 사용해 보았다.  

참고자료 : [https://renuevo.github.io/spring/batch/spring-batch-chapter-3/](https://renuevo.github.io/spring/batch/spring-batch-chapter-3/)  
-> `JdbcPagingItemReader`, `JpaPagingItemReader`, `RepositoryItemReader` 세가지 클래스를 모두 잘 설명하고 있어 많은 참고가 되었다.

```java
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fromDate", fromDate);
        parameters.put("toDate", toDate);

        return new JpaPagingItemReaderBuilder<OrderItem>()
                .name("orderItemReader")
                .entityManagerFactory(emf)
                .pageSize(5)
                .queryString("SELECT o FROM OrderItem as o where o.payDate between :fromDate and :toDate ORDER BY o.id ASC")
                .parameterValues(parameters)
                .build();
```

JpaPaging 은 Paging ItemReader의 한가지로 속도가 느리다는 단점이 있지만 메모리 이슈가 없고 성능상 유리하기 때문에 많이 사용한다고 한다.    
여기서 pageSize 는 chunksize과 동일하게 맞추는 것이 좋다고 하여 chunksize로 설정한 '5' 를 동일하게 설정하였다.  

<br>
queryString에 파라미터를 넣기 위하여 `parameterValues` 를 사용하였다.  

파라미터를 모은 map 를 parameterValues()로 세팅 후 아래와 같이 파라미터의 key값을 기준으로 `:key값` 의 형태로 query문에 값을 사용할 수 있다.
```java
SELECT o FROM OrderItem as o where o.payDate between :fromDate and :toDate ORDER BY o.id ASC"

```

참고자료 : [https://sadcode.tistory.com/47](https://sadcode.tistory.com/47)
-> 단순히 parameter를 사용하는 부분만 참고하였으나, 포스팅 내용은 영속성과 관련된 내용  

<br>


4. 정산 데이터 배치로 생성 부분 확인  

정산데이터 리스트 접근시 데이터가 정상적으로 생성된 것을 확인  
![img2](https://i.imgur.com/wBIdmJi.png)  



<br>

### 출금 도메인

<br>

1. mvc 구성

```java
withdraw
    entity/WithdrawApply // 출금신청
    dto/WithdrawApplyDto // 출금신청 작성 폼
    controller/AdmWithddrawController // 관리자 출금 처리
              /WithdrawController // 사용자 출금 신청 
    service/WithdrawService // 출금 비지니스 로직 
    repository/WithdrawApplyRepository // 출금신청 repository
```

WithdraApply entity 구성은 아래와 같다.

출금 신청서 작성자 Member와, 은행 계좌정보, 출금 가격 정보를 필드로 가지며,

신청서가 작성이 되었는지, 취소하였는지, 출금처리가 완료되었는지 여부를 확인하는 boolean 필드 세가지를 추가로 구성하였다.

```java
public class WithdrawApply extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Member member;
    private String bankName;
    private String backAccountNo;
    private int price;
    private boolean isApplied; // 신청여부
    private boolean isCanceled; // 취소여부
    private boolean isPaid; // 출금처리여부(지급여부)
}
```

2. 출금 테스트 작성

## **[특이사항]**


### 아쉬운점/ 궁금한점

<br>
1. Admin Role 처리 ->   MemberContext.genAuthorities()

처음 1주차에서 나는 MEMBER 권한, ADMIN 권한, AUTHOR 권한을 개별적으로 구현하였으나,  
요구사항을 다시 보니 AUTHOR 권한은 중복적으로 가질 수 있는 권한으로 확인되어 아래와 같이 authorities 를 구성하도록 수정하였다.

AUTHOR 권한은 추가적으로 가지는 authorities.  기본적으로 MEMBER 또는 ADMIN
```java
    public List<GrantedAuthority> genAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authLevel.name()));

        if (StringUtils.hasText(nickname)) {
            authorities.add(new SimpleGrantedAuthority("AUTHOR"));
        }
        System.out.println("[member] authority : "+authorities);
        return authorities;
    }
```
[https://escapefromcoding.tistory.com/m/526](https://escapefromcoding.tistory.com/m/526)  

<br>

2. 정산 비율 5:5  

rebatePrice를 계산하는 회계적인 이론을 잘 모르겠어서 도매가를 기준으로  
`도매가 -pgFee = 100` 이라면, 작가가 받을 수 있는 정산 비용은 `(도매가-pgFee)/2` 라고 판단하여 구현하였다.  

```java
    public int calculateRebatePrice() {
        if (refundPrice > 0) {
            return 0;
        }

        return (wholesalePrice - pgFee)/2;
    }

```
<br>

3. RepositoryItemReader 클래스로 null이 발생한 원인 분석

-> 아직 어느 부분에서 null이 발생하는지 원인을 찾지 못했다. paging을 하는 과정에서 아래 메소드가 잘못된 것인지 리팩토링 수 확인해 봐야겠다.
`Page<OrderItem> findAllByPayDateBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable)`

<br>

4. reader() 의 리턴은 인터페이스가 아닌 클래스로 리턴해야 한다.  
batch 구현 중 아래와 같은 에러 발생
```java
Method threw 'java.lang.IllegalArgumentException' exception. Cannot evaluate com.sun.proxy....
```
디버깅 하며 확인해보니, ItemReader의 값이 null이 발생하여 proxy를 구현할 수 없는 상황으로 확인 하여 ItemReader 수정  

reader() 의 리턴값은 ItemReadear의 구현체인 JpaPaginItemReader나 JdbcPagingItemReader와 같이 클래스로 리턴해야 함을 알게됨  

참고자료 : [https://jojoldu.tistory.com/132](https://jojoldu.tistory.com/132)

5. batch 구현하며 만난 에러

- [ ] 여러개의 batch를 동일하게 세팅하였을 때 발생 -> 한개의 jpa로 수정
```text
JPA does not support custom isolation levels, so locks may not be taken when launching Jobs. To silence this warning, set 'spring.batch.jdbc.isolation-level-for-create' to 'default'.

```
- [ ] initialize always 설정 -> batch 테이블이 이미 있다는 경고 메세지
```java
error: 1050-42s01: table 'batch_job_instance' already exists
```
-> 해당 메세지로 인하여 batch가 실패하지는 않으나 지속적으로 발생

- [ ] processor와 wirter에 intellij 경고 메세지 
```text
unchecked call to 'processor(itemprocessor<? super i, ? extends o>)' as a member of raw type 'org.springframework.batch.core.step.builder.simplestepbuilder' 

```
-> 에러메세지는 아니나, intelliJ에서 수정할 것을 권고한다.. 정확한 원인은 나중에 리팩토링시 확인해봐야겠다.


- [ ] batch의 job, step, reader, processor, write의 메소드 명과 bean 으로 생성된 클래스명이 일치하지 않아 필요한 bean을 식별하지 못하여 발생 -> 이름을 모두 동일 하게 수정

```text
spring batch required a single bean but 2 were found
```
`JOB_NAME` 이라는 static final 변수를 지정하여 해당 변수에 jon, step을 붙이는 규칙성있는 네이밍을 지정
```java
    public static final String JOB_NAME = "makeRebateOrderItem";

    @Bean(JOB_NAME+"Job")
    @Scheduled(cron= "0 0 4 15 * ?"  )
    public Job makeRebateOrderItemJob(Step makeRebateOrderItemStep1, CommandLineRunner initData) throws Exception{
        initData.run();
        log.debug("[rebateJob] start");
        return jobBuilderFactory.get(JOB_NAME+"Job")
        // ... 생략 ...
    }
```
  
<br>

### Refcatoring 시 추가적으로 구현하고 싶은 부분  

1. yearMonth를 선택하지 않은 url에서는 정산을 할 시, Refer에서 url을 가져오는 과정에서 yearMonth가 없기 때문에 500 에러 발생
--> default month정보를 입력하는 방식으로 수정   
<br>


2. ItemReader에 QueryDSL 도입
ItemReader 에 queryDsl을 도입한 예시를 찾아서 이부분으로 구현해보고 refactoring 해보고 싶다.
참고자료 : [https://techblog.woowahan.com/2662/](https://techblog.woowahan.com/2662/)  
<br>

3. 추가 기능 출금 구현
