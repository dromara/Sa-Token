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
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import cn.dev33.satoken.exception.SaTokenException;
import com.github.fppt.jedismock.RedisServer;
import cn.dev33.satoken.test.redis.JedisMockRedisSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.mock.env.MockEnvironment;

import java.io.IOException;

/**
 * Boot4 {@link SaAloneRedisInject} 跳过注入、单体模式、集群配置与配置失败抛异常测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaAloneRedisInjectPatternTest {

	/** 单体模式应该用独立 Redis 重新初始化 Dao */
	@Test
	void init_shouldReinitDaoInSingleMode() throws IOException {
		RedisServer server = JedisMockRedisSupport.startServer();
		try {
			SaTokenDaoForRedisTemplate dao = new SaTokenDaoForRedisTemplate();
			MockEnvironment env = baseEnv();
			env.setProperty("sa-token.alone-redis.port", String.valueOf(server.getBindPort()));
			env.setProperty("sa-token.alone-redis.pattern", "single");
			new SaAloneRedisInject(dao, env).init();
			Assertions.assertTrue(dao.isInit);
			Assertions.assertNotNull(dao.stringRedisTemplate);
		} finally {
			JedisMockRedisSupport.stopServer(server);
		}
	}

	/** 没配 timeout 时也应该能连上独立 Redis */
	@Test
	void init_shouldReinitDaoWhenTimeoutMissing() throws IOException {
		RedisServer server = JedisMockRedisSupport.startServer();
		try {
			SaTokenDaoForRedisTemplate dao = new SaTokenDaoForRedisTemplate();
			MockEnvironment env = new MockEnvironment();
			env.setProperty("sa-token.alone-redis.host", "127.0.0.1");
			env.setProperty("sa-token.alone-redis.port", String.valueOf(server.getBindPort()));
			env.setProperty("sa-token.alone-redis.database", "0");
			new SaAloneRedisInject(dao, env).init();
			Assertions.assertTrue(dao.isInit);
		} finally {
			JedisMockRedisSupport.stopServer(server);
		}
	}

	/** Dao 是默认实现时应该直接跳过，不去碰 Redis 配置 */
	@Test
	void init_shouldSkipDefaultDao() {
		SaAloneRedisInject inject = new SaAloneRedisInject(new SaTokenDaoDefaultImpl(), new MockEnvironment());
		Assertions.assertDoesNotThrow(inject::init);
	}

	/** 还没注入 Dao 时应该直接跳过 */
	@Test
	void init_shouldSkipNullDao() {
		SaAloneRedisInject inject = new SaAloneRedisInject(null, new MockEnvironment());
		Assertions.assertDoesNotThrow(inject::init);
	}

	/** 没配 sa-token.alone-redis 时应该抛出 SaTokenException */
	@Test
	void init_shouldThrowWhenAloneRedisBindMissing() {
		SaAloneRedisInject inject = new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), new MockEnvironment());
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class, inject::init);
		Assertions.assertEquals("Alone-Redis 注入失败", ex.getMessage());
	}

	/** 不认识的 pattern 时应该抛出 SaTokenException */
	@Test
	void init_shouldThrowWhenPatternUnknown() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "foobar");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
		Assertions.assertTrue(ex.getMessage().contains("foobar"));
	}

	/** 哨兵没配 sentinel 段时应该抛出 SaTokenException */
	@Test
	void init_shouldThrowWhenSentinelMissingNodes() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "sentinel");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
		Assertions.assertTrue(ex.getMessage().contains("sentinel.nodes"));
	}

	/** 哨兵配了 master 但没配 nodes 时应该抛出 SaTokenException */
	@Test
	void init_shouldThrowWhenSentinelNodesNull() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "sentinel");
		env.setProperty("sa-token.alone-redis.sentinel.master", "mymaster");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
		Assertions.assertTrue(ex.getMessage().contains("sentinel.nodes"));
	}

	/** 集群没配 cluster 段时应该抛出 SaTokenException */
	@Test
	void init_shouldThrowWhenClusterMissingNodes() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "cluster");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
		Assertions.assertTrue(ex.getMessage().contains("cluster.nodes"));
	}

	/** 集群配了 max-redirects 但没配 nodes 时应该抛出 SaTokenException */
	@Test
	void init_shouldThrowWhenClusterNodesNull() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "cluster");
		env.setProperty("sa-token.alone-redis.cluster.max-redirects", "3");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
		Assertions.assertTrue(ex.getMessage().contains("cluster.nodes"));
	}

	/** 集群配了节点但 Redis 还没起来时，注入阶段不应该炸（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void init_shouldFinishWhenClusterNotReachable() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "cluster");
		env.setProperty("sa-token.alone-redis.cluster.nodes", "127.0.0.1:7000");
		Assertions.assertDoesNotThrow(() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
	}

	/** 集群配了 max-redirects 时也应该能完成注入（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void init_shouldFinishWhenClusterHasMaxRedirects() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "cluster");
		env.setProperty("sa-token.alone-redis.cluster.nodes", "127.0.0.1:7000");
		env.setProperty("sa-token.alone-redis.cluster.max-redirects", "5");
		Assertions.assertDoesNotThrow(() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
	}

	/** 哨兵配了节点但 Redis 还没起来时，注入阶段不应该炸（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void init_shouldFinishWhenSentinelNotReachable() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "sentinel");
		env.setProperty("sa-token.alone-redis.sentinel.master", "mymaster");
		env.setProperty("sa-token.alone-redis.sentinel.nodes", "127.0.0.1:26379");
		env.setProperty("sa-token.alone-redis.sentinel.password", "abc");
		Assertions.assertDoesNotThrow(() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
	}

	/** socket 文件不存在时，注入阶段不应该炸（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void init_shouldFinishWhenSocketMissing() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "socket");
		env.setProperty("sa-token.alone-redis.socket", "/tmp/not-exist-sa-token.sock");
		Assertions.assertDoesNotThrow(() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
	}

	/** aws 地址连不上时，注入阶段不应该炸（Lettuce 懒连接） */
	@Test
	@Timeout(5)
	void init_shouldFinishWhenAwsNotReachable() {
		MockEnvironment env = baseEnv();
		env.setProperty("sa-token.alone-redis.pattern", "aws");
		env.setProperty("sa-token.alone-redis.host", "127.0.0.1");
		env.setProperty("sa-token.alone-redis.port", "1");
		Assertions.assertDoesNotThrow(() -> new SaAloneRedisInject(new SaTokenDaoForRedisTemplate(), env).init());
	}

	/** 提示用的配置对象方法应该能 new 出 DataRedisProperties */
	@Test
	void getSaAloneRedisConfig_shouldReturnNewProperties() {
		Assertions.assertNotNull(new SaAloneRedisInject(new SaTokenDaoDefaultImpl(), new MockEnvironment()).getSaAloneRedisConfig());
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
