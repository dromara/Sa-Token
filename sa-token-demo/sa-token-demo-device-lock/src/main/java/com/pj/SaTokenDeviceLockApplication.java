package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Sa-Token 测试  
 * @author click33
 *
 */
@SpringBootApplication
public class SaTokenDeviceLockApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenDeviceLockApplication.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}
