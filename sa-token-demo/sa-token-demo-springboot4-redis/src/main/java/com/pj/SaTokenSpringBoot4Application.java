package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sa-Token 整合 SpringBoot4 示例，整合 redis  
 * @author click33
 *
 */
@SpringBootApplication
public class SaTokenSpringBoot4Application {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenSpringBoot4Application.class, args); 
		System.out.println("\n🎉 启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}
