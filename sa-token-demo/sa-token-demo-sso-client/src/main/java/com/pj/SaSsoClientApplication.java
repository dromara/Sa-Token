package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sa-Token整合SpringBoot 示例 
 * @author kong
 *
 */
@SpringBootApplication
public class SaSsoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaSsoClientApplication.class, args);
		System.out.println("\nSa-Token-SSO Client端启动成功");
	}
	
}