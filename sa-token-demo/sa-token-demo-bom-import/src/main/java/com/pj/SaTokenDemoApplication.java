package com.pj;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Sa-Token 使用 bom 包引入框架
 */
@SpringBootApplication
public class SaTokenDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenDemoApplication.class, args); 
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

	// Sa-Token 整合 jwt (Simple 简单模式)
	@Bean
	public StpLogic getStpLogicJwt() {
		return new StpLogicJwtForSimple();
	}

}
