package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Sa-Token 跨域测试（Cookie 版）
 * @author click33
 */
@SpringBootApplication
public class SaTokenCrossCookieApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenCrossCookieApplication.class, args);
		System.out.println("\n启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
		System.out.println("\n后端地址使用 https://xxx.com 访问（必须为 https 连接），前端页面用 http://127.0.0.1 访问");
	}

}
