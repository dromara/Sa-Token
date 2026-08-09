package com.pj;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 源码首次运行引导 Demo。
 * <p>
 * 强制引入 apikey、sso、oauth2、sign 四个可选模块，引导 IDEA 编译相关插件后再运行其它 demo。
 * </p>
 *
 * @author click33
 */
@SpringBootApplication
public class SaTokenDemoFirstRunApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenDemoFirstRunApplication.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}
