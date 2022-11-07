//package com.yejin.exam.wbook.job.test;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.JobScope;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class TestJobConfig {
//    private final JobBuilderFactory jobBuilderFactory;
//
//    private final StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Job testJob() {
//        log.debug("[testJob] start");
//        return jobBuilderFactory.get("testJob")
//                //.incrementer(new RunIdIncrementer()) // 강제로 매번 다른 ID를 실행시에 파라미터로 부여
//                .start(testStep1())
//                .next(testStep2())
//                .build();
//    }
//
//    @Bean
//    @JobScope
//    public Step testStep1() {
//        log.debug("[testJob] step1 start");
//        return stepBuilderFactory.get("testStep1")
//                .tasklet(testStep1Tasklet())
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public Tasklet testStep1Tasklet() {
//        log.debug("[testJob] step1 tasklet start");
//        return (contribution, chunkContext) -> {
//            System.out.println("헬로월드 테스클릿 1");
//
//            return RepeatStatus.FINISHED;
//        };
//    }
//
//    @Bean
//    @JobScope
//    public Step testStep2() {
//        return stepBuilderFactory.get("testStep2")
//                .tasklet(testStep2Tasklet())
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public Tasklet testStep2Tasklet() {
//        return (contribution, chunkContext) -> {
//            System.out.println("헬로월드 테스클릿 2");
//
//            if ( false ) {
//                throw new Exception("실패 : 헬로월드 테스클릿 2");
//            }
//
//            return RepeatStatus.FINISHED;
//        };
//    }
//}