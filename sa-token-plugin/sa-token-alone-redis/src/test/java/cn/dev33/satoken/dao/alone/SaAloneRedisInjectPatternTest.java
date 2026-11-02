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

import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplateUseJdkSerializer;
import cn.dev33.satoken.exception.SaTokenException;
import com.github.fppt.jedismock.RedisServer;
import cn.dev33.satoken.test.redis.JedisMockRedisSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.mock.env.MockEnvironment;

import java.io.IOException;

/**
 * {@link SaAloneRedisInject} 跳过注入、单体模式、集群配置与配置失败抛异常测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaAloneRedisInjectPatternTest {

	/** 单体模式应该用独立 Redis 重新初始化 Dao */
	@Test
	void setEnvironment_shouldReinitDaoInSingleMode() throws IOException {
		RedisServer server = JedisMockRedisSupport.startServer();
		try {
			SaTokenDaoForRedisTemplateUseJdkSerializer dao = new SaTokenDaoForRedisTemplateUseJdkSerializer();
			SaAloneRedisInject inject = new SaAloneRedisInject();
			inject.saTokenDao = dao;
			MockEnvironment env = baseEnv();
			env.setProperty("sa-token.alone-redis.port", String.valueOf(server.getBindPort()));
			env.setProperty("sa-token.alone-redis.pattern", "single");
			inject.setEnvironment(env);
			Assertions.assertTrue(dao.isInit);
			Assertions.assertNotNull(dao.stringRedisTemplate);
		} finally {
			JedisMockRedisSupport.stopServer(server);
		}
	}

	/** 没配 timeout 时也应该能连上独立 Redis */
	@Test
	void setEnvironment_shouldReinitDaoWhenTimeoutMissing() throws IOException {
		RedisServer server = JedisMockRedisSupport.startServer();
		try {
			SaTokenDaoForRedisTemplateUseJdkSerializer dao = new SaTokenDaoForRedisTemplateUseJdkSerializer();
			SaAloneRedisInject inject = new SaAloneRedisInject();
			inject.saTokenDao = dao;
			MockEnvironment env = new MockEnvironment();
			env.setProperty("sa-token.alone-redis.host", "127.0.0.1");
			env.setProperty("sa-token.alone-redis.port", String.valueOf(server.getBindPort()));
			env.setProperty("sa-token.alone-redis.database", "0");
			inject.setEnvironment(env);
			Assertions.assertTrue(dao.isInit);
		} finally {
			JedisMockRedisSupport.stopServer(server);
		}
	}

	/** Dao 是默认实现时应该直接跳过，不去碰 Redis 配置 */
	@Test
	void setEnvironment_shouldSkipDefaultDao() {
		SaAloneRedisInject inject = new SaAloneRedisInject();
		inject.saTokenDao = new SaTokenDaoDefaultImpl();
		Assertions.assertDoesNotThrow(() -> inject.setEnvironment(new MockEnvironment()));
	}

	/** 还没注入 Dao 时应该直接跳过 */
	@Test
	void setEnvironment_shouldSkipNullDao() {
		SaAloneRedisInject inject = new SaAloneRedisInject();
		inject.saTokenDao = null;
		Assertions.assertDoesNotThrow(() -> inject.setEnvironment(new MockEnvironment()));
	}

	/** 没配 sa-token.alone-redis 时应该抛出 SaTokenException */
	@Test
	void setEnvironment_shouldThrowWhenAloneRedisBindMissing() {
		SaAloneRedisInject inject = new SaAloneRedisInject();
		inject.saTokenDao = new SaTokenDaoForRedisTemplateUseJdkSerializer();
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> inject.setEnvironment(new MockEnvironment()));
		Assertions.assertEquals("Alone-Redis 注入失败", ex.getMessage());
	}

	/** 不认识的 pattern 时应该抛出 SaTokenException */
	@Test
	void setEnvironment_shouldThrowWhenPatternUnknown() {
		SaAloneRedisInject inject = newInject();
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "foobar");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () -> inject.setEnvironment(env));
		Assertions.assertTrue(ex.getMessage().contains("foobar"));
	}

	/** 集群没配 cluster 段时应该抛出 SaTokenException */
	@Test
	void setEnvironment_shouldThrowWhenClusterMissingNodes() {
		SaAloneRedisInject inject = newInject();
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "cluster");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () -> inject.setEnvironment(env));
		Assertions.assertEquals("Alone-Redis 注入失败", ex.getMessage());
	}

	/** 集群配了 max-redirects 但没配 nodes 时应该抛出 SaTokenException */
	@Test
	void setEnvironment_shouldThrowWhenClusterNodesNull() {
		SaAloneRedisInject inject = newInject();
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "cluster");
		env.setProperty("sa-token.alone-redis.cluster.max-redirects", "3");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () -> inject.setEnvironment(env));
		Assertions.assertEquals("Alone-Redis 注入失败", ex.getMessage());
	}

	/** 集群配了节点但 Redis 还没起来时，注入阶段不应该炸（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void setEnvironment_shouldFinishWhenClusterNotReachable() {
		SaAloneRedisInject inject = newInject();
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "cluster");
		env.setProperty("sa-token.alone-redis.cluster.nodes", "127.0.0.1:7000");
		env.setProperty("sa-token.alone-redis.cluster.max-redirects", "3");
		Assertions.assertDoesNotThrow(() -> inject.setEnvironment(env));
	}

	/** 集群配了 max-redirects 时也应该能完成注入（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void setEnvironment_shouldFinishWhenClusterHasMaxRedirects() {
		SaAloneRedisInject inject = newInject();
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "cluster");
		env.setProperty("sa-token.alone-redis.cluster.nodes", "127.0.0.1:7000");
		env.setProperty("sa-token.alone-redis.cluster.max-redirects", "5");
		Assertions.assertDoesNotThrow(() -> inject.setEnvironment(env));
	}

	/** 哨兵配了节点但 Redis 还没起来时，注入阶段不应该炸（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void setEnvironment_shouldFinishWhenSentinelNotReachable() {
		SaAloneRedisInject inject = newInject();
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "sentinel");
		env.setProperty("sa-token.alone-redis.sentinel.master", "mymaster");
		env.setProperty("sa-token.alone-redis.sentinel.nodes", "127.0.0.1:26379");
		env.setProperty("sa-token.alone-redis.sentinel.password", "abc");
		Assertions.assertDoesNotThrow(() -> inject.setEnvironment(env));
	}

	/** socket 文件不存在时，注入阶段不应该炸（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void setEnvironment_shouldFinishWhenSocketMissing() {
		SaAloneRedisInject inject = newInject();
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "socket");
		env.setProperty("sa-token.alone-redis.socket", "/tmp/not-exist-sa-token.sock");
		Assertions.assertDoesNotThrow(() -> inject.setEnvironment(env));
	}

	/** aws 地址连不上时，注入阶段不应该炸（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void setEnvironment_shouldFinishWhenAwsNotReachable() {
		SaAloneRedisInject inject = newInject();
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "aws");
		env.setProperty("sa-token.alone-redis.host", "127.0.0.1");
		env.setProperty("sa-token.alone-redis.port", "1");
		Assertions.assertDoesNotThrow(() -> inject.setEnvironment(env));
	}

	/** 提示用的配置对象方法应该能 new 出 RedisProperties */
	@Test
	void getSaAloneRedisConfig_shouldReturnNewProperties() {
		Assertions.assertNotNull(new SaAloneRedisInject().getSaAloneRedisConfig());
	}

	/** 造一个挂了 JDK 序列化 Dao 的注入器 */
	private SaAloneRedisInject newInject() {
		SaAloneRedisInject inject = new SaAloneRedisInject();
		inject.saTokenDao = new SaTokenDaoForRedisTemplateUseJdkSerializer();
		return inject;
	}

	/** 填一份能绑定成功、超时尽量短的独立 Redis 配置 */
	private MockEnvironment baseEnv() {
		MockEnvironment env = new MockEnvironment();
		env.setProperty("sa-token.alone-redis.host", "127.0.0.1");
		env.setProperty("sa-token.alone-redis.port", "1");
		env.setProperty("sa-token.alone-redis.database", "0");
		env.setProperty("sa-token.alone-redis.username", "default");
		env.setProperty("sa-token.alone-redis.password", "pwd");
		env.setProperty("sa-token.alone-redis.timeout", "50ms");
		env.setProperty("sa-token.alone-redis.lettuce.shutdown-timeout", "50ms");
		return env;
	}

}
