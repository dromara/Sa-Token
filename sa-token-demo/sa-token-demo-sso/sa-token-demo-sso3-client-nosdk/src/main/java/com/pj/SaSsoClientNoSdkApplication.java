package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaSsoClientNoSdkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaSsoClientNoSdkApplication.class, args);

		System.out.println();
		System.out.println("---------------------- Sa-Token SSO 模式三 (NoSdk版) demo 启动成功 ----------------------");
		System.out.println("测试访问应用端一: http://sa-sso-client1.com:9004");
		System.out.println("测试访问应用端二: http://sa-sso-client2.com:9004");
		System.out.println("测试访问应用端三: http://sa-sso-client3.com:9004");
		System.out.println("测试前需要根据官网文档修改hosts文件，测试账号密码：sa / 123456");
		System.out.println();

		System.err.println("自 v1.43.0 版本起，Sa-Token SSO 不再维护 NoSdk 示例，此项目仅做留档");
		System.err.println("如您需要非 Sa-Token 技术栈项目接入 SSO-Server 认证中心，请参考 ReSdk 版本示例");
	}

}