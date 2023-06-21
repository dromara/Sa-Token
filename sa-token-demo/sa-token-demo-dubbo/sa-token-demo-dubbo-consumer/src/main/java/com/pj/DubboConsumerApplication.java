package com.pj;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dubbo 服务消费端 
 * 
 * @author click33
 *
 */
@EnableDubbo
@SpringBootApplication
public class DubboConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubboConsumerApplication.class, args);
		System.out.println("DubboConsumerApplication 启动成功");
	}

}
