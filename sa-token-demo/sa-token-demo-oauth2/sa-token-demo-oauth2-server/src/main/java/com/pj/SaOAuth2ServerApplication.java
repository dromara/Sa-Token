package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动：Sa-OAuth2 Server端 
 * @author click33 
 */
@SpringBootApplication 
public class SaOAuth2ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaOAuth2ServerApplication.class, args);
		System.out.println("\nSa-Token-OAuth Server端启动成功");
	}
	
}
