package com.pj;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.apikey.SaApiKeyManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaTokenApiKeyApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SaTokenApiKeyApplication.class, args);
		System.out.println("启动成功：Sa-Token 配置如下：" + SaManager.getConfig());
		System.out.println("启动成功：API Key  配置如下：" + SaApiKeyManager.getConfig());
		System.out.println("测试访问：http://localhost:8081/index.html");
	}
	
}