package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sa-Token整合webflux 示例 (springboot3)
 * 
 * @author click33
 * @since 2023年1月3日
 *
 */
@SpringBootApplication
public class SaTokenWebfluxSpringboot3Application {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenWebfluxSpringboot3Application.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
	
}