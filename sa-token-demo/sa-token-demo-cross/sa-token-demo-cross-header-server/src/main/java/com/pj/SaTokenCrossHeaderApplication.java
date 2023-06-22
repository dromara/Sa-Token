package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Sa-Token 跨域测试（header参数版）
 * @author click33
 */
@SpringBootApplication
public class SaTokenCrossHeaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenCrossHeaderApplication.class, args);
		System.out.println("\n启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
		System.out.println("\n后端地址使用 http://localhost 访问，前端页面用 http://127.0.0.1 访问");
	}

}
