package com.lodny.rwuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RWUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(RWUserApplication.class, args);
	}

}
