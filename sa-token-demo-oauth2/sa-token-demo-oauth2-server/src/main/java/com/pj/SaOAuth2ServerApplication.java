package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动 
 * @author kong 
 */
@SpringBootApplication 
public class SaOAuth2ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaOAuth2ServerApplication.class, args);
		System.out.println("\n服务端启动成功");
	}
	
}
