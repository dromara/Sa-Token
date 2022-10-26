package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaSso2ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaSso2ClientApplication.class, args);
		System.out.println("\nSa-Token SSO模式二 Client端启动成功");
	}
	
}