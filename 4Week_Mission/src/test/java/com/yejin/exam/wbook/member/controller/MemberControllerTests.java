package com.yejin.exam.wbook.member.controller;

import com.yejin.exam.wbook.domain.member.controller.MemberController;
import com.yejin.exam.wbook.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.hamcrest.Matchers.containsString;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class MemberControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private WebApplicationContext ctx;

    @BeforeEach
    public void setUp() {
        //MockMvc 설정
        this.mvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }
    @Test
    @DisplayName("회원가입 폼")
    void t1() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/join"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showJoin"))
                .andExpect(content().string(containsString("회원가입")));
    }

    @Test
    @DisplayName("회원가입")
    void t2() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/join")
                        .with(csrf())
                        .param("username", "user3")
                        .param("password", "12341234a")
                        .param("passwordConfirm","12341234a")
                        .param("email", "user3@test.com")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"));

        assertThat(memberService.findByUsername("user3").isPresent()).isTrue();
    }

    @Test
    @DisplayName("POST /member/login 정상 데이터 입력 시 / 으로 리디렉션된다")
    void test_login() throws Exception {

        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/member/login")
                                .param("username","user1")
                                .param("password","1234")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(redirectedUrl("/"))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    @DisplayName("아이디찾기 폼")
    void t3() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/findUsername"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showFindUsername"))
                .andExpect(content().string(containsString("아이디 찾기")));
    }

    @Test
    @DisplayName("POST /member/findUsername 아이디 찾기")
    void test__findUsername() throws Exception {

        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/member/findUsername").with(csrf())
                                .param("email","kyj011202@naver.com")
                )
                .andDo(print());

        // Then
        String expectContent = """
                {"resultCode":"FIND_USERNAME_OK","message":"해당하는 ID가 존재합니다.","data":"user1","success":true,"fail":false}""";
        resultActions
                .andExpect(content().string(containsString("FIND_USERNAME_OK")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("")
    void test__modifyPassword() throws Exception{

        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/member/modifyPassword").with(csrf())
                                .content("""
                                        {
                                            "oldPassword": "1234",
                                            "password": "4321",
                                            "passwordConfirm": "4321"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(redirectedUrl("/member/profile"))
                .andExpect(status().is3xxRedirection());
    }
}
