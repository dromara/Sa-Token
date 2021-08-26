package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SSO模式一，Server端 Demo 
 * @author kong
 *
 */
@SpringBootApplication
public class SaSsoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaSsoServerApplication.class, args);
		System.out.println("\nSa-Token-SSO 认证中心启动成功");
	}
	
}