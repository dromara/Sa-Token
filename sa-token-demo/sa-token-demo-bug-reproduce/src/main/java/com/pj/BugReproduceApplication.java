package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Issue / Bug 复现入口（临时沙盒，用例随 Issue 更换）。
 * <p> Jackson DefaultTyping 白名单长期回归见 {@code sa-token-demo-json-typing-security}。 </p>
 */
@SpringBootApplication
public class BugReproduceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugReproduceApplication.class, args);
		System.out.println("\n启动成功。当前复现用例见 README。");
	}

}
