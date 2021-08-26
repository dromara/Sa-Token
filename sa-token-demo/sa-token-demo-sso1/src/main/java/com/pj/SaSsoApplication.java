package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.dev33.satoken.SaManager;

@SpringBootApplication
public class SaSsoApplication {

	/*
	  此 demo 意在使用最少的代码演示一下SSO模式一的认证原理 
	 	1、改hosts(C:\windows\system32\drivers\etc\hosts)
	  		127.0.0.1 sso.stp.com
	  		127.0.0.1 s1.stp.com
			127.0.0.1 s2.stp.com
			127.0.0.1 s3.stp.com
		2、运行项目
			启动 SaSsoApplication
		3、浏览器访问
			http://s1.stp.com:8081/sso/isLogin
			http://s2.stp.com:8081/sso/isLogin
			http://s3.stp.com:8081/sso/isLogin
			均显示未登录
		4、然后访问任意节点的登录接口：
			http://s1.stp.com:8081/sso/doLogin
		5、重复步骤3，刷新三个地址
			均显示已登录 
	 */

	public static void main(String[] args) {
		SpringApplication.run(SaSsoApplication.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}