package com.lym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lym
 * @description
 * @date 2022/8/26 10:58
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class);
    }
}
