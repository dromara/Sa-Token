package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.spring.SaTokenSetup;

@SaTokenSetup // 必须有这个注解，用来标注加载sa-token
@SpringBootApplication
public class SaTokenDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenDemoApplication.class, args); 
		System.out.println("启动成功：sa-token配置如下：" + SaTokenManager.getConfig());
//		StpUtil.getSessionByLoginId(10001)
	}
	

}