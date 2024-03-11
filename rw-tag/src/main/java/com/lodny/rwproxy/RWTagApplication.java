package com.lodny.rwproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RWTagApplication {

	public static void main(String[] args) {
		SpringApplication.run(RWTagApplication.class, args);
	}

}
