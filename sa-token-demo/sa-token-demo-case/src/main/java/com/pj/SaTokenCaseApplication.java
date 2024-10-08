package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sa-Token 示例 
 * @author click33
 *
 */
@SpringBootApplication
public class SaTokenCaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenCaseApplication.class, args); 
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
	
}
