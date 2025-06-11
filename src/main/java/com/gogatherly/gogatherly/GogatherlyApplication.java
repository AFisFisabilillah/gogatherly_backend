package com.gogatherly.gogatherly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
public class GogatherlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GogatherlyApplication.class, args);
	}

}
