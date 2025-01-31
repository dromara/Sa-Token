package com.pj;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动：Sa-OAuth2 Server端
 * @author click33
 */
@SpringBootApplication
public class SaOAuth2ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaOAuth2ServerApplication.class, args);
		System.out.println("\nSa-Token-OAuth2 Server端启动成功，配置如下：");
		System.out.println(SaOAuth2Manager.getServerConfig());
		SaOAuth2Util.getCode("xxxxxxxxx");
	}
	
}
