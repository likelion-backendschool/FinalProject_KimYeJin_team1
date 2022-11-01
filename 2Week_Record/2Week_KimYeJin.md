## [1Week] 김예진

### 미션 요구사항 분석 & 체크리스트

---

### [미션 요구사항 분석]
미션 요구사항에 대하여 아래 2가지 기준에 따라 분석하였습니다. 
1. 전체 패키징(도메인 등)을 어떻게 구성할 것인지
2. 도메인별 URl 구성, mvc 구성을 어떻게 하고, 어떤 체크리스트에 따라 구현할지

<br>

### [전체 패키징 구성]

```
util

global

domain

    /member
    /post
    /product

```
<br>

### [도메인별 url 구성, mvc 구성, 체크리스트]
#### **<member 도메인>**

**1. url 구성**

/member

               /join
               /login
               /profile
               /modify
               /modifyPassoword
               /findUsername
               /findPassword

**2. mvc 구성**

entity

              /Member

              /MemberRole (AuthLevel)

dto

              /MemberDto
              /ModifyProfileDto
              /ModifyPasswordDto
              /FindPasswordDto
              /LoginDto

controller

              /MemberController

service

              /MemberService
              /EmailCodeService
              /CustomUserDetailService

repository

              /MemberRepository
              /qdsl/

                      /MemberRepositoryQuerydslImpl
                      /MemberRepositoryQuerydsl

**3. 체크리스트**



- [x] 회원가입 폼을 이용하여 회원가입 폼 입력 페이지를 구현한다.
  - GET /member/join
  - Form : username, password, passwordConfirm, email, nickname
  - valid 조건 : username, password, passwordConfirm, email 필수
    username, email 유니크
    nickname 미입력시 authlevel 3, 입력시 authlevel 4
- [x] 회원가입 시 authLevel이 지정되며, 회원가입 축하메일을 발송 후, 로그인한다.
  - POST /member/join
- [x] 회원정보수정 폼을 이용하여 회원정보 수정 페이지를 구현한다.
  - GET /member/modify
  - Form : email, nickname
  - valid 조건 : email 필수, 유니크
    nickname 미입력시 authlevel 3, 입력시 authlevel 4
- [x] 회원정보 수정시 변경된 정보가 반영되며, 회원정보 페이지로 이동한다.
  - POST /member/modify
- [x] 비밀번호수정 폼을 이용하여 비밀번호 변경 페이지를 구현한다.
  - GET /member/modifyPassword
  - Form : oldPassword, password, passwordConfirm
  - valid 조건 : oldPassword, password, passwordConfirm 필수
- [x] 비밀번호 수정시 변경된 정보가 반영되며, 회원정보 페이지로 이동한다.
  - POST /member/modifyPassword
- [x] 로그인 폼을 이용하여 로그인 페이지를 구현한다.
  - GET /member/login
  - Form : username, password
- [x] 로그아웃 페이지를 구현한다.
  - GET /member/logout
- [x] 아이디찾기 폼을 이용하여 아이디찾기 페이지를 구현한다.
  - GET /member/findUsername
  - Form : email
- [x] email로 부터 가져온 아이디를 화면에 출력한다.
  - POST /member/findUsername
- [x] 비밀번호 찾기폼을 이용하여 비밀번호찾기 페이지를 구현한다.
  - GET /member/findPassword
  - Form : username, email
- [x] 입력한 email로 임시비밀번호를 발송한다.
  - POST /member/findPassword


#### **<post 도메인>**

**1. url 구성**

/post

          /write
          /{id}
          /{id}/modify
          /{id}/delete
          /list

**2. mvc 구성**

entity

              /Post
              /PostTag

dto

              /PostDto
              /PostTagDto
              /ModifyPostDto

controller

              /PostController

service

              /PostService
              /PostTagService



repository

              /PostRepository
              /PostTagRepository
              /qdsl/
                      /PostRepositoryQuerydslImpl
                      /PostRepositoryQuerydsl

**3. 체크리스트**


- [x] 글 등록 폼을 이용하여 글 등록 페이지를 구현한다.
    - GET /post/write
    - Form : subject, content, keyword
    - valid 조건 : subject, content 필수
- [x] 글 등록시 글 상세 페이지로 이동한다.
    - POST /post/write

- [x] 글 수정 폼을 이용하여 글 수정 페이지를 구현한다.
    - GET /post/{id}/modify
    - Form : subject, content, postKeywordContent
    - valid 조건 : subject, content 필수
- [x] 글 수정시 글 상세 페이지로 이동한다.
    - POST /post/{id}/modify

- [ ] 글 삭제 시 삭제 여부를 확인 후 "취소" 시 이전 페이지, "확인"시 글 리스트 페이지로 이동한다.
    - GET /post/delete

- [ ] 본인이 작성한 글 전체 리스트를 화면에 출력하는 글 리스트 페이지로 이동한다.
    - GET /post/list
- [ ] 해시태그를 클릭하면 해당 해시태그와 관련된 글만을 볼 수 있다.
    - GET /post/list?tag={tagName}

- [ ] 글 상세 내용을 화면에 출력하는 글 상세 페이지로 이동한다.
    - GET /post/{id}

- [x] 모든 글은 본인이 작성한 글만 수정/삭제/상세/리스트 할 수 있다.

- [ ] 글의 내용은 토스트 에디터를 이용하여 마크다운 원문과 HTML을 같이 저장 후, 화면에 HTML원문으로 출력한다.



### [필수기능]

- [x]  회원가입, 회원정보수정, 로그인, 로그아웃
- [x]  아이디찾기, 비밀번호 찾기
- [ ]  글 작성, 글 수정, 글 리스트, 글 삭제 --> 구현 중 (미완)


### [추가기능]
- [ ]  상품 등록, 상품 수정, 상품 리스트, 상품 상세페이지


### 1주차 미션 요약

필수기능 3가지 중 회원가입 도메인에 대한 2가지는 구현 완료 <br>  
post 도메인에 대한 필수기능은 주요 기능 CRUD 까지 구현 하였으나, 해시태그, 토스트에디터는 미완입니다.

---

**[접근 방법]**


1. Member 도메인 체크리스트 별 접근 방법

- [x] 회원가입 폼을 이용하여 회원가입 폼 입력 페이지를 구현한다.

    1) MemberDto 구성
    
    상세 valid 옵션을 적용하여, 회원가입 폼으로 사용되는 MemberDto 작성
    -> 왜 valid 옵션을 사용하였는가?
    처음에는 ajax를 이용하여 조건에 맞지 않는 경우, 프론트에 바로 경고 메세지를 뿌려주는 형태로 구현하였으나, dto 자체에 조건을 명시하는것이 코드적으로 보기 좋다고 판단.
    
    ```java
        @Getter
        @Setter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @AllArgsConstructor
        public class MemberDto {

            @NotBlank(message = "아이디를 입력해주세요")
            @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
            @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.")
            private String username;

            @NotBlank(message = "비밀번호를 입력해주세요")
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상, 최소 하나의 문자와 숫자가 필요합니다")
            @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
            private String password;

            @NotBlank(message = "비밀번호를 입력해주세요")
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상, 최소 하나의 문자와 숫자가 필요합니다")
            @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
            private String passwordConfirm;

            @NotBlank(message = "이메일을 입력해주세요")
            @Email(message = "이메일의 형식이 맞지 않습니다")
            private String email;

            @Length(min = 2, max = 12, message = "필명은 2문자 이상 12문자 이하여야 합니다")
            private String nickname;

        }
    ```
    
    


- [x] 회원가입 시 authLevel이 지정되며, 회원가입 축하메일을 발송 후, 로그인한다.

    1) authLevel의 Entity 구성
    
    MemberRole이라는 enum 타입의 authLevel 필드로 구성
    -> 왜 enum  타입을 사용하였는가?
    enum 타입을 사용하여 @Enumerated() 어노테이션을 사용하면, 쉽게 DB에 데이터를 숫자의 형태로 저장할 수 있어 효과적으로 DB에 데이터를 저장할 수 있다.
    실제 authLevel를 코드에서 사용할 때는 해당 숫자를 대표하는 enum에 정의한 name으로 사용하면 된다. ex) ``MemberRole.ROLE_AUTHOR``
    
    ```java
    // Member 엔티티 의 authLevel 필드
    
    @Column(name = "authLevel")
    @Enumerated(EnumType.ORDINAL)
    private MemberRole authLevel;
    ```
    
    ```java
    // 아래의 필드값은 EnumType.ORDINAL 조건에 의해 순서대로 1~7 의 숫자를 부여받는다.
    public enum MemberRole {
        ROLE_00, ROLE_01, ROLE_02, ROLE_MEMBER, ROLE_AUTHOR, ROLE_05, ROLE_06, ROLE_ADMIN;
    }
    ```
    
    실제 db 에 저장된 auth_level 컬럼의 값은 숫자 4인것을 확인할 수 있다.
    ![img1](https://i.imgur.com/fyVDfrH.png)
    
    3) authLevel 이 지정되는 로직 
    
    먼저 MemberDto로 들어온 입력값으로 Member 객체를 build 하는 로직 구성
    ```java
        private Member convertMemberDtoToMember(MemberDto memberDto) {
        Member member = Member.builder()
                .username(memberDto.getUsername())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .email(memberDto.getEmail())
                .nickname(memberDto.getNickname())
                .build();
        return setMemberRoleByNickname(member);
    }
    ```
    생성된 member 객체의 nickname의 유무를 통해 authlevel을 조정
    ```java
    private Member setMemberRoleByNickname(Member member){
        if(member.getNickname()==null || member.getNickname().length()==0){
            member.setAuthLevel(MemberRole.ROLE_MEMBER);
            return member;
        }
        member.setAuthLevel(MemberRole.ROLE_AUTHOR);
        return member;
    }
    ```
       
    
    3) 축하메일이 발송되는 로직
    
    gmail의 smtp 를 이용하여 축하메세지를 단순히 발송
    email 서비스는 임시 비밀번호 발송에도 공통적으로 사용되는 모듈이므로 서비스를 분리
    
    ```java
    // EmailService
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class EmailService {

        private final JavaMailSender emailSender;

        public void sendMessage(String email, String subject, String text){
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("kyj2212@gmail.com");
            message.setTo(email);
            message.setText(text);
            emailSender.send(message);
        }
    }

    ```
    
    축하 메일 발송 부분
    ```java
       String subject = "[wbook] 회원가입을 축하합니다.";
       String text = "%s 님의 회원가입을 축하합니다.".formatted(member.getUsername());
       emailService.sendMessage(member.getEmail(), subject,text);
    ```
    
    회원가입시 아래와 같이 축하메일이 발송되는 것을 확인할 수 있다.
    ![img1](https://i.imgur.com/73spdnI.png)
    

- [x] 회원정보수정 폼을 이용하여 회원정보 수정 페이지를 구현한다.
      
      회원정보 수정폼은 회원가입 폼과, 비밀번호 수정폼과 필드내용이 다르기 때문에 분리
      
      ```java
        @Getter
        @Setter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @AllArgsConstructor
        public class MemberModifyDto {

            @NotBlank(message = "이메일을 입력해주세요")
            @Email(message = "이메일의 형식이 맞지 않습니다")
            private String email;

            @Length(min = 2, max = 12, message = "필명은 2문자 이상 12문자 이하여야 합니다")
            private String nickname;
        }

      ```
      

- [x] 회원정보 수정시 변경된 정보가 반영되며, 회원정보 페이지로 이동한다.
        1) nickname 의 변경 여부에 따라 auth level 의 변동
        작가명을 수정할 경우, authLevel 정보가 변경되기 때문에 확인 로직 추가
        
        email과 nickname 모두 변동사항이 없는 경우, 불필요한 db 호출을 줄이기 위하여 변동사항 체크 로직 추가
        ```java
        public void modify(Member member, String email, String nickname) {

        if(!member.getEmail().equals(email)){
            member.setEmail(email);
        }
        if(!member.getNickname().equals(nickname)){
            member.setNickname(nickname);
            if(member.getAuthLevel()!=MemberRole.ROLE_AUTHOR){
                member.setAuthLevel(MemberRole.ROLE_AUTHOR);
            }
        }
        memberRepository.save(member);
        }
        ```

- [x] 비밀번호수정 폼을 이용하여 비밀번호 변경 페이지를 구현한다.

      회원정보 수정 폼과 비밀번호 수정폼의 필드값이 달라 비밀번호 수정폼을 따로 구현
      -> 처음에는 동일한 회원정보 폼에서 ajax으로 비밀번호 수정폼을 분리하였으나, 마찬가지로 dto를 통한 코드의 가독성을 위해 따로 분리
      
      기존 비밀번호는 valid 가 필요하지 않은 이유 -> password 일치여부를 판단하기 때문에 사용하지 않음
      
      ```java
        @Getter
        @Setter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @AllArgsConstructor
        public class MemberModifyPasswordDto {

            private String oldPassword;

            @NotBlank(message = "비밀번호를 입력해주세요")
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상, 최소 하나의 문자와 숫자가 필요합니다")
            @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
            private String password;

            @NotBlank(message = "비밀번호를 입력해주세요")
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상, 최소 하나의 문자와 숫자가 필요합니다")
            @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
            private String passwordConfirm;

        }

      ```
      

- [x] 비밀번호 수정시 변경된 정보가 반영되며, 회원정보 페이지로 이동한다.
    
    1) oldPassword에는 member 패스워드와 match가 되는지 판단
    ```java
        // controller
        if(!memberService.modifyPassword(member,memberModifyPasswordDto.getPassword(),memberModifyPasswordDto.getOldPassword())){
            bindingResult.addError(new FieldError("member", "oldPassword","올바른 기존 패스워드를 입력하세요."));
            return mav;
        }
        
        // service
        if(!passwordEncoder.matches(oldPassword,member.getPassword())){
            return false;
        }
        
    ```
    2) 기존과 동일한 패스워드 변경 불가, confirm 패스워드 확인
    ```java
        @PostMapping("/modifyPassword")
        public ModelAndView modifyPassword(Principal principal, ModelAndView mav, @Valid MemberModifyPasswordDto memberModifyPasswordDto, BindingResult bindingResult){
        mav.setViewName("member/profile_form");
        Member member = memberService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException());
        if(memberModifyPasswordDto.getOldPassword() == memberModifyPasswordDto.getPassword()){
            bindingResult.addError(new FieldError("member", "password","기존 패스워드와 동일한 패스워드로 바꿀 수 없습니다."));
            return mav;
        }
        if (!memberModifyPasswordDto.getPassword().equals(memberModifyPasswordDto.getPasswordConfirm())) {
            bindingResult.addError(new FieldError("member", "passwordConfirm","2개의 패스워드가 일치하지 않습니다."));
            return mav;
        }
        /// ... 생략 ...
        }
    ```


- [x] 로그인 폼을 이용하여 로그인 페이지를 구현한다.
    1) Spring Security를 이용하여, form 로그인을 지정
    ```java
    // ... 생략 ... 
        http
            .formLogin()
            .loginPage("/member/login");
    ```
    
    인가 필요한 path와 허용해야 하는 path를 분리하여 filter 적용
    ``` java
         private static final String[] AUTH_ALL_LIST = {
                "/member/join/**",
                "/member/login/**",
                "/member/findUsername/**",
                "/member/findPassword/**",
                "/"
        }; // 모두 허용

        private static final String[] AUTH_AUTHENTICATED_LIST = {
                "/member/**",
                "/post/**",
        }; // 인가 필요
        // ... 생략 ...
        http
            .authorizeRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .antMatchers(AUTH_ALL_LIST).permitAll()
            .antMatchers(AUTH_AUTHENTICATED_LIST).authenticated();
    ```
    
    2) 회원가입시 바로 로그인 가능하도록 서비스에 login 메소드 지정
    
    임의로 회원가입시 Spring security를 통해 로그인을 불러올 수 있도록 UsernamePasswordAuthenticationToken을 바로 생성하도록 설정
    ```java
        public void login(String username, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(token);
    }
    ```

- [x] 로그아웃 페이지를 구현한다. 
    Spring security를 이용

- [x] 아이디찾기 폼을 이용하여 아이디찾기 페이지를 구현한다.
- [x] email로 부터 가져온 아이디를 화면에 출력한다.
    
    1) 아이디의 존재 여부를 ResponseEntity의 형태로 응답하여 ajax로 화면에 ID를 표출하도록 한다.
    
    ```java
        @PostMapping("/findUsername")
        public ResponseEntity<ResultResponse> findUsername(ModelAndView mav, String email){
        Optional<Member> oMember = memberService.findByEmail(email);
        if(oMember.isPresent()){
            return ResponseEntity.ok(ResultResponse.of("FIND_USERNAME_OK","해당하는 ID가 존재합니다." ,oMember.get().getUsername()));
        }
        return ResponseEntity.ok(ResultResponse.of("FIND_USERNAME_FAIL","해당하는 ID가 없습니다.",false));
    }
    ```

- [x] 비밀번호 찾기폼을 이용하여 비밀번호찾기 페이지를 구현한다.
    1) 아이디의 존재 여부를 ResponseEntity의 형태로 응답하여 ajax로 존재여부를 표출하도록 한다.
    ```java
        @PostMapping("/findPassword")
        public ResponseEntity<ResultResponse> findUsername(ModelAndView mav, String username, String email){
        Optional<Member> oMember = memberService.findByUsername(username);
        if(!oMember.isPresent()){
            return ResponseEntity.ok(ResultResponse.of("FIND_PWD_FAIL","해당하는 ID가 없습니다.",username));
        }
        memberService.setTempPassword(oMember.get());
        return ResponseEntity.ok(ResultResponse.of("FIND_PWD_OK","%s로 임시 비밀번호를 전송하였습니다.".formatted(email),username));

    }
    ```
    


- [x] 입력한 email로 임시비밀번호를 발송한다.
    1) 임시비밀번호 전송을 위하여 tmp 비밀번호를 생성하는 로직 구현
    
    랜덤한 UUID를 생성하여 비밀번호 생성하여 이전에 작성한 EmailService를 이용하여 메일 전송
    ```java
        public void setTempPassword(Member member) {

        String subject = "[wbook] %s 님의 임시 비밀번호 입니다.".formatted(member.getUsername());
        String tempPassword = UUID.randomUUID().toString().replace("-","");
        member.setEncryptedPassword(passwordEncoder.encode(tempPassword));
        String text = """
                        임시 비밀번호 : %s
                        위의 임시 비밀번호로 로그인 후, 비밀번호를 변경해 주세요.
                        """.formatted(tempPassword);

        emailService.sendMessage(member.getEmail(),subject,text);
    }
    ```
    아래와 같이 임시 비밀번호가 전송되는 것을 볼 수 있다.
    ![img2](https://i.imgur.com/LbDOy6E.png)


2. Post 도메인 체크리스트 별 접근 방법

- [x] 글 등록 폼을 이용하여 글 등록 페이지를 구현한다.
    
- [x] 글 등록시 글 상세 페이지로 이동한다.
    
    
- [x] 글 수정 폼을 이용하여 글 수정 페이지를 구현한다.
    
- [x] 글 수정시 글 상세 페이지로 이동한다.
    
- [x] 모든 글은 본인이 작성한 글만 수정/삭제/상세/리스트 할 수 있다.

- [ ] 글 삭제 시 삭제 여부를 확인 후 "취소" 시 이전 페이지, "확인"시 글 리스트 페이지로 이동한다.
- [ ] 본인이 작성한 글 전체 리스트를 화면에 출력하는 글 리스트 페이지로 이동한다.
- [ ] 해시태그를 클릭하면 해당 해시태그와 관련된 글만을 볼 수 있다.
- [ ] 글 상세 내용을 화면에 출력하는 글 상세 페이지로 이동한다.
- [ ] 글의 내용은 토스트 에디터를 이용하여 마크다운 원문과 HTML을 같이 저장 후, 화면에 HTML원문으로 출력한다.



**[특이사항]**

### 아쉬운점/ 궁금한점

1. @Lob 에너테이션
- Post의 content를 구성할 때, 큰 데이터를 위한 @Lob 에너테이션의 사용을 알게되었는데, 실제 @Lob 에너테이션의 장단점, 특징 등을 더 분석하여 리팩토링 하고 싶습니다.

2. 구현 완성도
- 초기에 모든 코드를 백지에서 작성하려고 하다보니, 시간적으로 부족하였습니다. 기존에 작성했던 코드를 적절히 리팩토링 해보는 방법이 더 맞았던 것 같아 구현을 완성하지 못해 아쉬움이 남습니다. 
- 다음 미션에서는 코드를 리팩토링 하는 형식으로 진행하여 더 완성도 있는 미션을 진행하겠습니다.



### Refcatoring 시 추가적으로 구현하고 싶은 부분

<Member 도메인>
1. 회원가입 시 이메일 인증
2. 회원가입 시 SNS 로그인 : naver 카페 연동을 위한 naver 로그인

<도메인 공통>
1. REST API 적용을 위한 PUT,DELETE 메소드 적용
2. 
