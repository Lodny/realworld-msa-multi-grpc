package com.lodny.rwfavorite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RWFavoriteApplication {

	public static void main(String[] args) {
		SpringApplication.run(RWFavoriteApplication.class, args);
	}

}
