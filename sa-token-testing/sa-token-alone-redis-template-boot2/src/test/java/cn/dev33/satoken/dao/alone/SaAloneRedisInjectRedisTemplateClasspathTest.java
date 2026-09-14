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

import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import com.github.fppt.jedismock.RedisServer;
import cn.dev33.satoken.test.redis.JedisMockRedisSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.io.IOException;

/**
 * classpath 上没有 jdk-serializer 时，Alone-Redis 应该走 redis-template 那条初始化
 *
 * @author click33
 * @since 1.46.0
 */
public class SaAloneRedisInjectRedisTemplateClasspathTest {

	/** 这个模块就是为了缺 jdk-serializer，类路径上不该能加载到它 */
	@Test
	void classpath_shouldNotHaveJdkSerializerDao() {
		Assertions.assertThrows(ClassNotFoundException.class,
				() -> Class.forName("cn.dev33.satoken.dao.SaTokenDaoForRedisTemplateUseJdkSerializer"));
	}

	/** 只有 redis-template 时应该用它重新初始化独立 Redis 的 Dao */
	@Test
	void setEnvironment_shouldReinitRedisTemplateDaoWhenJdkSerializerMissing() throws IOException {
		RedisServer server = JedisMockRedisSupport.startServer();
		try {
			SaTokenDaoForRedisTemplate dao = new SaTokenDaoForRedisTemplate();
			SaAloneRedisInject inject = new SaAloneRedisInject();
			inject.saTokenDao = dao;
			MockEnvironment env = new MockEnvironment();
			env.setProperty("sa-token.alone-redis.host", "127.0.0.1");
			env.setProperty("sa-token.alone-redis.port", String.valueOf(server.getBindPort()));
			env.setProperty("sa-token.alone-redis.database", "0");
			env.setProperty("sa-token.alone-redis.timeout", "50ms");
			env.setProperty("sa-token.alone-redis.pattern", "single");
			inject.setEnvironment(env);
			Assertions.assertTrue(dao.isInit);
			Assertions.assertNotNull(dao.stringRedisTemplate);
		} finally {
			JedisMockRedisSupport.stopServer(server);
		}
	}

}
