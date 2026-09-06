package com.example.prize_sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PrizeSenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrizeSenderApplication.class, args);
	}

}
