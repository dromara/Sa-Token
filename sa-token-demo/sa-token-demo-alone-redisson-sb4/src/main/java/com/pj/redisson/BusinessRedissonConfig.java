package com.pj.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 业务 RedissonClient。Boot 4 下不使用 redisson-spring-boot-starter 3.45，改为按 yaml 自行创建。
 *
 * @author click33
 */
@Configuration
public class BusinessRedissonConfig {

	@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClient(@Value("${spring.redis.redisson.config}") String yaml) throws IOException {
		return Redisson.create(Config.fromYAML(yaml));
	}

}
