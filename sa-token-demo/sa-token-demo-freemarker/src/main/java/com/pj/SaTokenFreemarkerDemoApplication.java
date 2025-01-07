package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaTokenFreemarkerDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SaTokenFreemarkerDemoApplication.class, args);
		System.out.println("\n启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
		System.out.println("\n测试访问：http://localhost:8081/");
	}
	
}