package com.yejin.exam.wbook.rebate;

import com.yejin.exam.wbook.domain.order.controller.OrderController;
import com.yejin.exam.wbook.domain.order.service.OrderService;
import com.yejin.exam.wbook.domain.rebate.controller.RebateController;
import com.yejin.exam.wbook.domain.rebate.service.RebateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class RebateControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private RebateService rebateService;

    @Test
    @DisplayName("정산 데이터 생성 폼")
    @WithUserDetails("admin")
    void t1() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/adm/rebate/makeData"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(RebateController.class))
                .andExpect(handler().methodName("showMakeData"))
                .andExpect(content().string(containsString("정산데이터")));
    }

    @Test
    @DisplayName("정산 데이터 생성")
    @WithUserDetails("admin")
    void t2() throws Exception {

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/adm/rebate/makeData")
                        .param("yearMonth", "2022-11")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(RebateController.class))
                .andExpect(handler().methodName("makeData"))
                .andExpect(redirectedUrlPattern("/adm/rebate/rebateOrderItemList?yearMonth=**"));
    }

    @Test
    @DisplayName("정산 리스트")
    @WithUserDetails("admin")
    void t3() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/adm/rebate/rebateOrderItemList?yearMonth="))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(RebateController.class))
                .andExpect(handler().methodName("showRebateOrderItemList"))
                .andExpect(content().string(containsString("정산데이터")));
    }
    @Test
    @DisplayName("주문 아이템 단건 정산")
    @WithUserDetails("admin")
    void t4() throws Exception {
        // GIVEN
        long orderItemId = 1L;
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/adm/rebate/rebateOne/%d".formatted(orderItemId))
                        .header("Referer","?yearMonth=2022-11"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(RebateController.class))
                .andExpect(handler().methodName("rebateOne"))
                .andExpect(redirectedUrlPattern("/adm/rebate/rebateOrderItemList?yearMonth=**"));
    }

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
}
