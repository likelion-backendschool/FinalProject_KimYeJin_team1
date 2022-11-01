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
- [ ] 정산데이터 배치로 생성

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
  
### Refcatoring 시 추가적으로 구현하고 싶은 부분  
