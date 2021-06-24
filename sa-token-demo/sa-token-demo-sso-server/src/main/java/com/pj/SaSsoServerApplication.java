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
public class SaSsoServerApplication {

	public static void main(String[] args) throws ClassNotFoundException {
		SpringApplication.run(SaSsoServerApplication.class, args);
		System.out.println("\nSa-Token-SSO 服务端启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
	
}