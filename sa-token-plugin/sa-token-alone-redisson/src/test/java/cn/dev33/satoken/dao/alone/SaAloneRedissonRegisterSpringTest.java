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
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import com.github.fppt.jedismock.RedisServer;
import cn.dev33.satoken.test.redis.JedisMockRedisSupport;
import cn.dev33.satoken.test.redis.SaTokenDaoLoginAsserts;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

/**
 * 自动装配应该用独立 Redisson 连接注册 {@link SaTokenDaoForRedisson}
 *
 * @author click33
 * @since 1.46.0
 */
@SpringBootTest(classes = SaAloneRedissonRegisterSpringTest.App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SaAloneRedissonRegisterSpringTest {

	private static final RedisServer redisServer = startRedis();

	@Autowired
	private SaTokenDao saTokenDao;

	/** 启动无密码的内嵌 Redis，给独立 RedissonClient 连 */
	private static RedisServer startRedis() {
		try {
			return JedisMockRedisSupport.startServer();
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/** 把内嵌 Redis 写进独立 Redisson yaml；protocol=RESP2 是 jedis-mock 需要的 */
	@DynamicPropertySource
	static void redisProps(DynamicPropertyRegistry registry) {
		registry.add("sa-token.alone-redisson.config",
				() -> "protocol: RESP2\nsingleServerConfig:\n  address: \"redis://127.0.0.1:" + redisServer.getBindPort() + "\"\n");
	}

	/** 测完把内嵌 Redis 关掉 */
	@AfterAll
	static void stopRedis() throws IOException {
		JedisMockRedisSupport.stopServer(redisServer);
	}

	/** 自动装配后应该用独立 Redisson 注册 Dao，并且能读写 */
	@Test
	void autoConfig_shouldRegisterAloneRedissonDao() {
		Assertions.assertTrue(saTokenDao instanceof SaTokenDaoForRedisson);
		saTokenDao.set("alone-key", "ok", 60);
		Assertions.assertEquals("ok", saTokenDao.get("alone-key"));
	}

	/** 登录后 token 应该写进独立 Redisson */
	@Test
	void login_shouldWriteTokenToAloneRedisson() {
		SaTokenDaoLoginAsserts.assertLoginWritesTokenToDao(saTokenDao);
	}

	@SpringBootApplication
	static class App {
	}

}
