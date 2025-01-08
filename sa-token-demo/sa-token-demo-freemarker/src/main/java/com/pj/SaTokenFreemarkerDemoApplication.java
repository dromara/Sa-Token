package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 参考链接：https://blog.csdn.net/m0_64210833/article/details/135994864
 */
@SpringBootApplication
public class SaTokenFreemarkerDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SaTokenFreemarkerDemoApplication.class, args);
		System.out.println("\n启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
		System.out.println("\n测试访问：http://localhost:8081/");
	}
	
}