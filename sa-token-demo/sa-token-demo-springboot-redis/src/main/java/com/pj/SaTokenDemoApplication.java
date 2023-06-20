package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sa-Token整合SpringBoot 示例，整合redis
 * @author click33
 *
 */
@SpringBootApplication
public class SaTokenDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenDemoApplication.class, args); 
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
	
}
