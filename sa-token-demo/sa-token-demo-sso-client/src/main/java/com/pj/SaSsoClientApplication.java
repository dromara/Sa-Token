package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.dev33.satoken.SaManager;

/**
 * Sa-Token整合SpringBoot 示例 
 * @author kong
 *
 */
@SpringBootApplication
public class SaSsoClientApplication {

	public static void main(String[] args) throws ClassNotFoundException {
		SpringApplication.run(SaSsoClientApplication.class, args);
		System.out.println("\nSa-Token-SSO客户端启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
	
}