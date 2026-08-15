package com.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Issue / Bug 复现入口（临时沙盒，用例随 Issue 更换）。
 */
@SpringBootApplication
public class BugReproduceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugReproduceApplication.class, args);
	}

}
