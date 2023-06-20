package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动：Sa-OAuth2 ClientServer端 
 * @author click33 
 */
@SpringBootApplication 
public class SaOAuth2ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaOAuth2ClientApplication.class, args);
		System.out.println("\nSa-Token-OAuth Client端启动成功\n\n" + str);
	}

	static String str = "-------------------- Sa-Token-OAuth2 示例 --------------------\n\n" + 
			"首先在host文件 (C:\\windows\\system32\\drivers\\etc\\hosts) 添加以下内容: \r\n" + 
			"	127.0.0.1 sa-oauth-server.com \r\n" + 
			"	127.0.0.1 sa-oauth-client.com \r\n" + 
			"再从浏览器访问：\r\n" + 
			"	http://sa-oauth-client.com:8002";
	
}
