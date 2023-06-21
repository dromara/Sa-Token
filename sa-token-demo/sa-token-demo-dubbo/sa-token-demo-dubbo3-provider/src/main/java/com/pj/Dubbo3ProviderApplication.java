package com.pj;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dubbo3 服务提供端
 * 
 * @author click33
 *
 */
@EnableDubbo
@SpringBootApplication
public class Dubbo3ProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dubbo3ProviderApplication.class, args);
		System.out.println("Dubbo3ProviderApplication 启动成功");
	}
	
}
