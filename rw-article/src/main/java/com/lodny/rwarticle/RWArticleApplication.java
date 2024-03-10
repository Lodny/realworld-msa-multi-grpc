package com.lodny.rwarticle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

@EnableJpaAuditing
@SpringBootApplication
public class RWArticleApplication {

	public static void main(String[] args) {
		SpringApplication.run(RWArticleApplication.class, args);
	}
}
