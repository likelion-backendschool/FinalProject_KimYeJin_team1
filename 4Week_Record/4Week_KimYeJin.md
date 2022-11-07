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
- [ ] jwt 회원 로그인 구현


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




### Refcatoring 시 추가적으로 구현하고 싶은 부분  

1. yearMonth를 선택하지 않은 url에서는 정산을 할 시, Refer에서 url을 가져오는 과정에서 yearMonth가 없기 때문에 500 에러 발생
--> default month정보를 입력하는 방식으로 수정   
<br>


2. ItemReader에 QueryDSL 도입
ItemReader 에 queryDsl을 도입한 예시를 찾아서 이부분으로 구현해보고 refactoring 해보고 싶다.
참고자료 : [https://techblog.woowahan.com/2662/](https://techblog.woowahan.com/2662/)  
<br>

3. 추가 기능 출금 구현
