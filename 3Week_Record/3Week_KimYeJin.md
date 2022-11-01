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
base/  config/  error/  exception/  request/  result/  security/

domain
cart/  cash/  home/  member/  order/  post/  product/


```
<br>

### [도메인별 체크리스트]

#### **<Rebate 도메인>**

**1. mvc 구성**
**2. 체크리스트**

#### **<order 도메인>**

**1. mvc 구성**
**2. 체크리스트**

#### **<cart 도메인>**

**1. mvc 구성**
**2. 체크리스트**


### [지난 주 필수 기능]
- [x] 장바구니 내의 상품 주문
- [x] 기존 회원이 가지고 있는 cash를 이용한 결제
- [x] 토스페이를 이용한 결제
- [x] 결제 이후 환불 가능


### [필수기능]
- [ ] 정산데이터 생성

### 2주차 미션 요약

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

'정산'도 도메인인가?
-> 도메인이다.  
[참고링크 : https://runa-nam.tistory.com/m/120](https://runa-nam.tistory.com/m/120)

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

**[특이사항]**


### 아쉬운점/ 궁금한점

- 1주차 Product 의 구현을 완료하는데 시간을 소요하여, 2주차 기능 필수기능을 구현하지 못하였습니다.
-> 다음 주차 부터는 이전 주차의 내용을 따라하는 형식으로 구현한 후 진행하도록 하겠습니다.


### Refcatoring 시 추가적으로 구현하고 싶은 부분
- 