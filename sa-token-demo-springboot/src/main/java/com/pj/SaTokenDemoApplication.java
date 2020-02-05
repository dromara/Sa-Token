package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.spring.SaTokenSetup;

@SaTokenSetup // 标注启动 sa-token
@SpringBootApplication
public class SaTokenDemoApplication {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(SaTokenDemoApplication.class, args); // run-->
		System.out.println("启动成功：sa-token配置如下：" + SaTokenManager.getConfig());
	}
	
	
		
	
	

}