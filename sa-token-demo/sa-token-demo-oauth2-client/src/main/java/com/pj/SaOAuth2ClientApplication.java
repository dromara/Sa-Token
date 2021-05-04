package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动 
 * @author kong 
 */
@SpringBootApplication 
public class SaOAuth2ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaOAuth2ClientApplication.class, args);
		System.out.println("\n客户端启动成功，访问: http://localhost:8002/login.html");
	}
	
}
