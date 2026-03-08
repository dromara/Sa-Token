package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sa-Token 整合 SpringBoot4 示例，整合 alone-redis 插件（权限缓存与业务缓存分离）
 *
 * @author click33
 */
@SpringBootApplication
public class SaTokenAloneRedisSb4Application {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenAloneRedisSb4Application.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}
