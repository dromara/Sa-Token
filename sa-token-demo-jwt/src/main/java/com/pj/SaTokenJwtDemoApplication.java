package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.dev33.satoken.SaTokenManager;

@SpringBootApplication
public class SaTokenJwtDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SaTokenJwtDemoApplication.class, args); 
		System.out.println("\n启动成功：sa-token配置如下：" + SaTokenManager.getConfig());
	}
	
}