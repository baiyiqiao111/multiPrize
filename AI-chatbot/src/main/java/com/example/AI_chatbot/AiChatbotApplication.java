package com.example.AI_chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.TimeZone;

@EnableFeignClients
@SpringBootApplication
public class AiChatbotApplication {

	public static void main(String[] args) {
		// 服务器（EC2）默认是 UTC，而数据库 serverTimezone、Jackson 都按 America/Los_Angeles，
		// 不统一的话 LocalDateTime.now()、new Date() 格式化出来的时间会快 7~8 小时
		TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
		SpringApplication.run(AiChatbotApplication.class, args);
	}
}