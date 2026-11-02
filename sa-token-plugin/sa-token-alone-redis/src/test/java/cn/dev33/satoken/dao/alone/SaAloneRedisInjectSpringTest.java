/*
 * Copyright 2020-2099 sa-token.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.dao.alone;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplateUseJdkSerializer;
import com.github.fppt.jedismock.RedisServer;
import cn.dev33.satoken.test.redis.JedisMockRedisSupport;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

/**
 * 自动装配应该用 sa-token.alone-redis 重连 Dao，和业务 Redis 隔开
 *
 * @author click33
 * @since 1.46.0
 */
@SpringBootTest(classes = SaAloneRedisInjectSpringTest.App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SaAloneRedisInjectSpringTest {

	private static final RedisServer businessRedis = startRedis();
	private static final RedisServer aloneRedis = startRedis();

	@Autowired
	private SaTokenDao saTokenDao;

	@Autowired
	private StringRedisTemplate businessRedisTemplate;

	/** 启动无密码的内嵌 Redis */
	private static RedisServer startRedis() {
		try {
			return JedisMockRedisSupport.startServer();
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/** 业务 Redis 走 spring.redis，Sa-Token 走 sa-token.alone-redis */
	@DynamicPropertySource
	static void redisProps(DynamicPropertyRegistry registry) {
		registry.add("spring.redis.host", () -> "127.0.0.1");
		registry.add("spring.redis.port", () -> businessRedis.getBindPort());
		registry.add("spring.redis.database", () -> 0);
		registry.add("spring.redis.timeout", () -> "3s");
		registry.add("sa-token.alone-redis.host", () -> "127.0.0.1");
		registry.add("sa-token.alone-redis.port", () -> aloneRedis.getBindPort());
		registry.add("sa-token.alone-redis.database", () -> 0);
		registry.add("sa-token.alone-redis.timeout", () -> "3s");
		registry.add("sa-token.alone-redis.lettuce.shutdown-timeout", () -> "100ms");
		registry.add("sa-token.alone-redis.lettuce.pool.max-active", () -> 8);
		registry.add("sa-token.alone-redis.lettuce.pool.max-idle", () -> 8);
		registry.add("sa-token.alone-redis.lettuce.pool.min-idle", () -> 0);
		registry.add("sa-token.alone-redis.lettuce.pool.max-wait", () -> "1s");
	}

	/** 测完把两台内嵌 Redis 关掉 */
	@AfterAll
	static void stopRedis() throws IOException {
		JedisMockRedisSupport.stopServer(businessRedis);
		JedisMockRedisSupport.stopServer(aloneRedis);
	}

	/** 两个 Redis 插件都在 classpath 时，Alone-Redis 会按 jdk-serializer 这条重连到独立端口 */
	@Test
	void autoConfig_shouldReinitJdkSerializerDaoAgainstAlonePort() {
		Assertions.assertTrue(saTokenDao instanceof SaTokenDaoForRedisTemplateUseJdkSerializer);
		SaTokenDaoForRedisTemplateUseJdkSerializer dao = (SaTokenDaoForRedisTemplateUseJdkSerializer) saTokenDao;
		Assertions.assertTrue(dao.isInit);
		Assertions.assertNotNull(dao.stringRedisTemplate);

		int daoPort = ((LettuceConnectionFactory) dao.stringRedisTemplate.getConnectionFactory()).getPort();
		int businessPort = ((LettuceConnectionFactory) businessRedisTemplate.getConnectionFactory()).getPort();
		Assertions.assertEquals(aloneRedis.getBindPort(), daoPort);
		Assertions.assertEquals(businessRedis.getBindPort(), businessPort);
		Assertions.assertNotEquals(businessPort, daoPort);
	}

	@SpringBootApplication(exclude = {
			RedisAutoConfiguration.class,
			RedisRepositoriesAutoConfiguration.class,
			SaTokenDaoForRedisTemplate.class
	})
	static class App {

		/** 业务 Redis 工厂：jedis-mock 必须 RESP2，Boot2 自动装配还会踩到仓库里的旧 pool2 */
		@Bean
		RedisConnectionFactory redisConnectionFactory() {
			RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration("127.0.0.1", businessRedis.getBindPort());
			LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
					.clientOptions(ClientOptions.builder()
							.protocolVersion(ProtocolVersion.RESP2)
							.build())
					.build();
			LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
			factory.afterPropertiesSet();
			return factory;
		}

		/** 业务侧 StringRedisTemplate，用来核对 Dao 没有连到业务 Redis 端口 */
		@Bean
		StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
			return new StringRedisTemplate(redisConnectionFactory);
		}

	}

}
