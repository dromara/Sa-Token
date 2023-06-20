package com.pj;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dubbo3 服务消费端
 * 
 * @author click33
 *
 */
@EnableDubbo
@SpringBootApplication
public class Dubbo3ConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dubbo3ConsumerApplication.class, args);
		System.out.println("Dubbo3ConsumerApplication 启动成功");
	}

}
