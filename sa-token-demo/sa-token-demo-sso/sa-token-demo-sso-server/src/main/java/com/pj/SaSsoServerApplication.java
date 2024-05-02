package com.pj;

import cn.dev33.satoken.sso.SaSsoManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaSsoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaSsoServerApplication.class, args);

		System.out.println();
		System.out.println("---------------------- Sa-Token SSO 统一认证中心启动成功 ----------------------");
		System.out.println("配置信息：" + SaSsoManager.getServerConfig());
		System.out.println("统一认证登录地址：http://sa-sso-server.com:9000/sso/auth");
		System.out.println("测试前需要根据官网文档修改hosts文件，测试账号密码：sa / 123456");
		System.out.println();
	}
	
}