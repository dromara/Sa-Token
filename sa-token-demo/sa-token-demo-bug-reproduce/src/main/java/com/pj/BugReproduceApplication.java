package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Issue / Bug 复现入口。
 * <p>
 * 本模块专门用来复现社区 Issue 上提到的问题，便于本地验证与回归。
 */
@SpringBootApplication
public class BugReproduceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugReproduceApplication.class, args);
		System.out.println("\n启动成功。当前复现用例见 README / TestController。");
	}

}
