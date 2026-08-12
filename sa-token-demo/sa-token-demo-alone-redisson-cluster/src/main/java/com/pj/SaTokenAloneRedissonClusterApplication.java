package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sa-Token 整合 SpringBoot 示例，整合 alone-redisson 插件（集群模式）
 *
 * @author click33
 */
@SpringBootApplication
public class SaTokenAloneRedissonClusterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenAloneRedissonClusterApplication.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}
