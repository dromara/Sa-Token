package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaSsoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaSsoServerApplication.class, args);
		System.out.println("\n------ Sa-Token-SSO 统一认证中心启动成功 ");
	}
	
}