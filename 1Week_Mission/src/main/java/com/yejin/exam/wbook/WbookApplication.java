package com.yejin.exam.wbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WbookApplication {

	public static void main(String[] args) {
		SpringApplication.run(WbookApplication.class, args);
	}

}
