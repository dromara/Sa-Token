package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.dev33.satoken.SaTokenManager;

@SpringBootApplication
public class SaTokenDemoApplication {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SaTokenDemoApplication.class, args); 
		System.out.println("\n启动成功：sa-token配置如下：" + SaTokenManager.getConfig());
	}
	
}