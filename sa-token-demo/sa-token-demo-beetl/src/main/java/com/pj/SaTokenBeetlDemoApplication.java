package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.dev33.satoken.SaManager;

@SpringBootApplication
public class SaTokenBeetlDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SaTokenBeetlDemoApplication.class, args);
		System.out.println("\n启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
		System.out.println("\n测试访问：http://localhost:8081/");
	}
	
}