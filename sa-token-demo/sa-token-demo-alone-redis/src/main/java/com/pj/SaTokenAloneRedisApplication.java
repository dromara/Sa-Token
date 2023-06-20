package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.dev33.satoken.SaManager;

/**
 * Sa-Token整合SpringBoot 示例 
 * @author click33
 *
 */
@SpringBootApplication
public class SaTokenAloneRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenAloneRedisApplication.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
	
}