package com.pj;

import cn.dev33.satoken.strategy.SaJsonStrategy;
import com.pj.poc.JacksonDefaultTypingRcePoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Jackson DefaultTyping 反序列化白名单 — 长期回归入口。
 */
@SpringBootApplication
public class JsonTypingSecurityApplication {

	public static void main(String[] args) {
		// 注册 Object 类型，将使白名单机制失效
		// SaJsonStrategy.instance.registerAllowType(Object.class);

		SpringApplication.run(JsonTypingSecurityApplication.class, args);
		System.out.println("\n启动成功。Jackson DefaultTyping 白名单回归见 README。");

		// 攻击成功，会弹出计算器 (windows)
		JacksonDefaultTypingRcePoc.run();
	}

}
