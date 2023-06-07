package com.pj;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.strategy.SaStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Sa-Token 测试  
 * @author click33
 *
 */
@SpringBootApplication
public class SaTokenApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaTokenApplication.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());

		// SaStrategy全局单例，所有方法都用以下形式重写
		SaStrategy.instance.setCreateToken((loginId, loginType) -> {
			// 自定义Token生成的算法
			return "xxxx";
		});

	}

}
