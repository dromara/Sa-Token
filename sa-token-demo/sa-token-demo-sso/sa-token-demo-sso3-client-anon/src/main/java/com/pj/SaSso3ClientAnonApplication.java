package com.pj;

import cn.dev33.satoken.sso.SaSsoManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaSso3ClientAnonApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaSso3ClientAnonApplication.class, args);

		System.out.println();
		System.out.println("---------------------- Sa-Token SSO 模式三 (匿名应用) Client 端启动成功 ----------------------");
		System.out.println("配置信息：" + SaSsoManager.getClientConfig());
		System.out.println("测试访问应用端一: http://sa-sso-client1.com:9006");
		System.out.println("测试访问应用端二: http://sa-sso-client2.com:9006");
		System.out.println("测试访问应用端三: http://sa-sso-client3.com:9006");
		System.out.println("测试前需要根据官网文档修改 hosts 文件，测试账号密码：sa / 123456");
		System.out.println();
	}
	
}