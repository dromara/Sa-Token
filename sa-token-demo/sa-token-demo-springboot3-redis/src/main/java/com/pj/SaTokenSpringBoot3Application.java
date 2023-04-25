package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sa-Token 整合 SpringBoot3 示例，整合redis  
 * @author kong
 *
 */
@SpringBootApplication
public class SaTokenSpringBoot3Application {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenSpringBoot3Application.class, args); 
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
	
}
