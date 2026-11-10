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

import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplateUseJdkSerializer;
import cn.dev33.satoken.exception.SaTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.mock.env.MockEnvironment;

import java.util.Set;

/** 验证节点地址通过配置绑定后保留主机和端口，不建立 Redis 连接。 */
class SaAloneRedisInjectNodeTest {

	@ParameterizedTest
	@CsvSource({
			"cluster, 127.0.0.1:7000, 127.0.0.1, 7000",
			"cluster, localhost:7001, localhost, 7001",
			"cluster, [2001:db8::1]:7002, 2001:db8::1, 7002",
			"cluster, [::1]:7003, ::1, 7003",
			"sentinel, 127.0.0.1:26379, 127.0.0.1, 26379",
			"sentinel, localhost:26380, localhost, 26380",
			"sentinel, [2001:db8::1]:26381, 2001:db8::1, 26381",
			"sentinel, [::1]:26382, ::1, 26382"
	})
	void shouldBindNodeHostAndPort(String pattern, String address, String host, int port) {
		SaTokenDaoForRedisTemplateUseJdkSerializer dao = new SaTokenDaoForRedisTemplateUseJdkSerializer();
		MockEnvironment env = nodeEnvironment(pattern, address);
		try {
			SaAloneRedisInject inject = new SaAloneRedisInject();
			inject.saTokenDao = dao;
			inject.setEnvironment(env);
			LettuceConnectionFactory factory = (LettuceConnectionFactory) dao.stringRedisTemplate.getConnectionFactory();
			Set<RedisNode> nodes = "cluster".equals(pattern)
					? factory.getClusterConfiguration().getClusterNodes()
					: factory.getSentinelConfiguration().getSentinels();
			Assertions.assertEquals(1, nodes.size());
			RedisNode node = nodes.iterator().next();
			Assertions.assertEquals(host, node.getHost());
			Assertions.assertEquals(port, node.getPort().intValue());
		} finally {
			if (dao.stringRedisTemplate != null) {
				((LettuceConnectionFactory) dao.stringRedisTemplate.getConnectionFactory()).destroy();
			}
		}
	}

	@ParameterizedTest
	@CsvSource({
			"cluster, localhost",
			"cluster, localhost:",
			"cluster, localhost:invalid",
			"sentinel, localhost",
			"sentinel, localhost:",
			"sentinel, localhost:invalid"
	})
	void shouldRejectMissingOrInvalidPort(String pattern, String address) {
		SaTokenDaoForRedisTemplateUseJdkSerializer dao = new SaTokenDaoForRedisTemplateUseJdkSerializer();
		MockEnvironment env = nodeEnvironment(pattern, address);
		SaTokenException error = Assertions.assertThrows(SaTokenException.class, () -> {
			SaAloneRedisInject inject = new SaAloneRedisInject();
			inject.saTokenDao = dao;
			inject.setEnvironment(env);
		});
		Assertions.assertEquals("Alone-Redis 注入失败", error.getMessage());
	}

	private MockEnvironment nodeEnvironment(String pattern, String address) {
		MockEnvironment env = new MockEnvironment();
		env.setProperty("sa-token.alone-redis.pattern", pattern);
		env.setProperty("sa-token.alone-redis." + pattern + ".nodes", address);
		if ("cluster".equals(pattern)) {
			env.setProperty("sa-token.alone-redis.cluster.max-redirects", "3");
		}
		if ("sentinel".equals(pattern)) {
			env.setProperty("sa-token.alone-redis.sentinel.master", "mymaster");
		}
		return env;
	}
}
