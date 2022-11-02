package com.yejin.exam.wbook.job.rebate.orderItem;

import com.yejin.exam.wbook.domain.order.entity.OrderItem;
import com.yejin.exam.wbook.domain.order.repository.OrderItemRepository;
import com.yejin.exam.wbook.domain.rebate.entity.RebateOrderItem;
import com.yejin.exam.wbook.domain.rebate.repository.RebateOrderItemRepository;
import com.yejin.exam.wbook.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MakeRebateOrderItemJobConfig {

    public static final String JOB_NAME = "makeRebateOrderItem";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;
    private final OrderItemRepository orderItemRepository;
    private final RebateOrderItemRepository rebateOrderItemRepository;

    @Bean(JOB_NAME+"Job")
    //    @Scheduled(cron= "0 0 4 15 * ?"  )
//    @Scheduled(cron= "0 1 * * * ?"  )
    public Job makeRebateOrderItemJob(Step makeRebateOrderItemStep1, CommandLineRunner initData) throws Exception {
        initData.run();
        log.debug("[rebateJob] start");
        return jobBuilderFactory.get(JOB_NAME+"Job")
                .start(makeRebateOrderItemStep1)
                .incrementer(new RunIdIncrementer())
//                .start(step2(null))
                .build();
    }
//    @Bean
//    @JobScope
//    public Step step2(@Value("#{jobParameters[month]}") String date) {
//        return stepBuilderFactory.get("step2")
//                .tasklet((contribution, chunkContext) -> {
//                    log.info(">>>>> This is step2");
//                    log.info(">>>>> date = {}", date);
//                    return RepeatStatus.FINISHED;
//                })
//                .build();
//    }
    @Bean(JOB_NAME+"Step1")
    @JobScope
    public Step makeRebateOrderItemStep1(
            ItemReader orderItemReader,
            ItemProcessor orderItemToRebateOrderItemProcessor,
            ItemWriter rebateOrderItemWriter
    ) {
        log.debug("[rebateJob] step1 start");
        Step step = stepBuilderFactory.get(JOB_NAME+"Step1")
                .<OrderItem, RebateOrderItem>chunk(5)
                .reader(orderItemReader)
                .processor(orderItemToRebateOrderItemProcessor)
                .writer(rebateOrderItemWriter)
                .build();
        log.debug("[step] processor : "+step.toString());
        return step;
    }


    @StepScope
    @Bean("orderItemReader")
    public JpaPagingItemReader<OrderItem> orderItemReader(@Value("#{jobParameters['month']}") String yearMonth) throws Exception{

        log.debug("[rebateJob] reader start");
        if(yearMonth==null){
            yearMonth = "2022-11";
        }
//        int monthEndDay = Util.date.getEndDayOf(jobParameter.getCreateDate());
        int monthEndDay = Util.date.getEndDayOf(yearMonth);
        LocalDateTime fromDate = Util.date.parse(yearMonth + "-01 00:00:00.000000");
        LocalDateTime toDate = Util.date.parse(yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay));

        log.debug("[batch] findAllByPayDateBetween : ");

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
    }

//    @StepScope
//    @Bean("orderItemReader")
//    public RepositoryItemReader<OrderItem> orderItemReader(
//            @Value("#{jobParameters['month']}") String yearMonth
//    ) throws Exception {
//        log.debug("[rebateJob] reader start");
//        if(yearMonth==null){
//            yearMonth = "2022-11";
//        }
////        int monthEndDay = Util.date.getEndDayOf(jobParameter.getCreateDate());
//        int monthEndDay = Util.date.getEndDayOf(yearMonth);
//        LocalDateTime fromDate = Util.date.parse(yearMonth + "-01 00:00:00.000000");
//        LocalDateTime toDate = Util.date.parse(yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay));
//
//        log.debug("[batch] findAllByPayDateBetween : ");
//        RepositoryItemReader<OrderItem> repositoryItemReader = new RepositoryItemReaderBuilder<OrderItem>()
//                .name(JOB_NAME+"reader")
//                .repository(orderItemRepository)
//                .methodName("findAllByPayDateBetween")
//                .pageSize(5)
//                .arguments(Arrays.asList(fromDate, toDate))
//                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
//                .build();
////        log.debug("[batch] respositoryReader : " + repositoryItemReader.read().getId());
//        return repositoryItemReader;
//    }

    @StepScope
    @Bean
    public ItemProcessor<OrderItem, RebateOrderItem> orderItemToRebateOrderItemProcessor() {
        log.debug("[rebateJob] processor start");
        return orderItem -> new RebateOrderItem(orderItem);
    }

    @StepScope
    @Bean("rebateOrderItemWriter")
    public ItemWriter<RebateOrderItem> rebateOrderItemWriter() {
        log.debug("[rebateJob] writer start");
        return items -> items.forEach(item -> {
            log.debug("[batch] getOrderItem : "+item.getOrderItem().getProduct().getSubject());
            log.debug("[batch] getOrderItem id : "+item.getOrderItem().getId());
            log.debug("[batch] rebate Item paydate : "+item.getPayDate());
            RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

            if (oldRebateOrderItem != null) {
                rebateOrderItemRepository.delete(oldRebateOrderItem);
            }

            rebateOrderItemRepository.save(item);
        });
    }
}