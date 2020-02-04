package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.spring.SaTokenSetup;

@SaTokenSetup // 标注启动 sa-token
@SpringBootApplication
public class SaTokenApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenApplication.class, args); // run-->
		System.out.println(SaTokenManager.getConfig());
	}

}