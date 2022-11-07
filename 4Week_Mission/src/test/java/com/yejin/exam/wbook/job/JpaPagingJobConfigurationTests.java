package com.yejin.exam.wbook.job;


import com.yejin.exam.wbook.domain.order.entity.OrderItem;
import com.yejin.exam.wbook.domain.order.repository.OrderItemRepository;
import com.yejin.exam.wbook.domain.rebate.entity.RebateOrderItem;
import com.yejin.exam.wbook.domain.rebate.repository.RebateOrderItemRepository;
import com.yejin.exam.wbook.job.rebate.orderItem.MakeRebateOrderItemJobConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"job.name=" + MakeRebateOrderItemJobConfig.JOB_NAME})
public class JpaPagingJobConfigurationTests {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private RebateOrderItemRepository rebateOrderItemRepository;

    @Test
    @DisplayName("paging 테스트")
    public void t1() throws Exception {
        //given
        OrderItem orderItem = orderItemRepository.findById(1L).get();
        rebateOrderItemRepository.save(new RebateOrderItem(orderItem));
        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then


    }
}