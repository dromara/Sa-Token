package com.pj;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dubbo 服务提供端 
 * 
 * @author click33
 *
 */
@EnableDubbo
@SpringBootApplication
public class DubboProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubboProviderApplication.class, args);
		System.out.println("DubboProviderApplication 启动成功");
	}
	
}
