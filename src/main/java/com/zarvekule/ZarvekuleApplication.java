package com.zarvekule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ZarvekuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZarvekuleApplication.class, args);
	}

}
