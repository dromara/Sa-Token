package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Sa-Token 异步方案 测试
 * @author click33
 *
 */
@EnableAsync    // 启用异步
@EnableScheduling // 启动定时任务
@SpringBootApplication
public class SaTokenAsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenAsyncApplication.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}
