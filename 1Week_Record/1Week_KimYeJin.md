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



- [ ] 회원가입 폼을 이용하여 회원가입 폼 입력 페이지를 구현한다.
  - GET /member/join
  - Form : username, password, passwordConfirm, email, nickname
  - valid 조건 : username, password, passwordConfirm, email 필수
    username, email 유니크
    nickname 미입력시 authlevel 3, 입력시 authlevel 4
- [ ] 회원가입 시 authLevel이 지정되며, 회원가입 축하메일을 발송 후, 로그인한다.
  - POST /member/join
- [ ] 회원정보수정 폼을 이용하여 회원정보 수정 페이지를 구현한다.
  - GET /member/modify
  - Form : email, nickname
  - valid 조건 : email 필수, 유니크
    nickname 미입력시 authlevel 3, 입력시 authlevel 4
- [ ] 회원정보 수정시 변경된 정보가 반영되며, 회원정보 페이지로 이동한다.
  - POST /member/modify
- [ ] 비밀번호수정 폼을 이용하여 비밀번호 변경 페이지를 구현한다.
  - GET /member/modifyPassword
  - Form : oldPassword, password, passwordConfirm
  - valid 조건 : oldPassword, password, passwordConfirm 필수
- [ ] 비밀번호 수정시 변경된 정보가 반영되며, 회원정보 페이지로 이동한다.
  - POST /member/modifyPassword
- [ ] 로그인 폼을 이용하여 로그인 페이지를 구현한다.
  - GET /member/login
  - Form : username, password
- [ ] 로그아웃 페이지를 구현한다.
  - GET /member/logout
- [ ] 아이디찾기 폼을 이용하여 아이디찾기 페이지를 구현한다.
  - GET /member/findUsername
  - Form : email
- [ ] email로 부터 가져온 아이디를 화면에 출력한다.
  - POST /member/findUsername
- [ ] 비밀번호 찾기폼을 이용하여 비밀번호찾기 페이지를 구현한다.
  - GET /member/findPassword
  - Form : username, email
- [ ] 입력한 email로 임시비밀번호를 발송한다.
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


- [ ] 글 등록 폼을 이용하여 글 등록 페이지를 구현한다.
    - GET /post/write
    - Form : subject, content, keyword
    - valid 조건 : subject, content 필수
- [ ] 글 등록시 글 상세 페이지로 이동한다.
    - POST /post/write

- [ ] 글 수정 폼을 이용하여 글 수정 페이지를 구현한다.
    - GET /post/{id}/modify
    - Form : subject, content, postKeywordContent
    - valid 조건 : subject, content 필수
- [ ] 글 수정시 글 상세 페이지로 이동한다.
    - POST /post/{id}/modify

- [ ] 글 삭제 시 삭제 여부를 확인 후 "취소" 시 이전 페이지, "확인"시 글 리스트 페이지로 이동한다.
    - GET /post/delete

- [ ] 본인이 작성한 글 전체 리스트를 화면에 출력하는 글 리스트 페이지로 이동한다.
    - GET /post/list
- [ ] 해시태그를 클릭하면 해당 해시태그와 관련된 글만을 볼 수 있다.
    - GET /post/list?tag={tagName}

- [ ] 글 상세 내용을 화면에 출력하는 글 상세 페이지로 이동한다.
    - GET /post/{id}

- [ ] 모든 글은 본인이 작성한 글만 수정/삭제/상세/리스트 할 수 있다.

- [ ] 글의 내용은 토스트 에디터를 이용하여 마크다운 원문과 HTML을 같이 저장 후, 화면에 HTML원문으로 출력한다.



### [필수,추가 기능]

- [ ]  회원가입, 회원정보수정, 로그인, 로그아웃
- [ ]  아이디찾기, 비밀번호 찾기
- [ ]  글 작성, 글 수정, 글 리스트, 글 삭제
- [ ]  상품 등록, 상품 수정, 상품 리스트, 상품 상세페이지


### 1주차 미션 요약

---

**[접근 방법]**

체크리스트를 중심으로 각각의 기능을 구현하기 위해 어떤 생각을 했는지 정리합니다.

- 무엇에 중점을 두고 구현하였는지, 어떤 공식문서나 예제를 참고하여 개발하였는지 뿐만 아니라 미션을 진행하기 전 개인적으로 실습한 것도 포함하여 작성해주시기 바랍니다.
- 실제 개발 과정에서 목표하던 바가 무엇이었는지 작성해주시기 바랍니다.
- 구현 과정에 따라 어떤 결과물이 나오게 되었는지 최대한 상세하게 작성해주시기 바랍니다.

**[특이사항]**

구현 과정에서 아쉬웠던 점 / 궁금했던 점을 정리합니다.

- 추후 리팩토링 시, 어떤 부분을 추가적으로 진행하고 싶은지에 대해 구체적으로 작성해주시기 바랍니다.

  **참고: [Refactoring]**

    - Refactoring 시 주로 다루어야 할 이슈들에 대해 리스팅합니다.
    - 1차 리팩토링은 기능 개발을 종료한 후, 스스로 코드를 다시 천천히 읽어보면서 진행합니다.
    - 2차 리팩토링은 피어리뷰를 통해 전달받은 다양한 의견과 피드백을 조율하여 진행합니다.

1. REST API 적용을 위한 PUT,DELETE 메소드 적용 url 변경