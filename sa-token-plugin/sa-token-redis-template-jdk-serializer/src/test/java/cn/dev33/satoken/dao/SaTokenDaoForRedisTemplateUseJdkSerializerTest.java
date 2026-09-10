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
package cn.dev33.satoken.dao;

import com.github.fppt.jedismock.RedisServer;
import com.pj.test.redis.JedisMockRedisSupport;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * {@link SaTokenDaoForRedisTemplateUseJdkSerializer} Object 读写与 JDK 序列化测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenDaoForRedisTemplateUseJdkSerializerTest {

	private static RedisServer redisServer;

	private LettuceConnectionFactory connectionFactory;

	private SaTokenDaoForRedisTemplateUseJdkSerializer dao;

	/** 启动无密码的内嵌 Redis，端口随机 */
	@BeforeAll
	static void startRedis() throws IOException {
		redisServer = JedisMockRedisSupport.startServer();
	}

	/** 测完把内嵌 Redis 关掉 */
	@AfterAll
	static void stopRedis() throws IOException {
		JedisMockRedisSupport.stopServer(redisServer);
	}

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		connectionFactory = createConnectionFactory();
		dao = new SaTokenDaoForRedisTemplateUseJdkSerializer();
		dao.init(connectionFactory);
		flushDb();
	}

	/** 每个用例结束后把测试现场清掉 */
	@AfterEach
	void tearDown() {
		if (connectionFactory != null) {
			connectionFactory.destroy();
			connectionFactory = null;
		}
	}

	/** 创建连到当前内嵌 Redis 的 Lettuce 工厂，强制 RESP2 */
	private LettuceConnectionFactory createConnectionFactory() {
		RedisStandaloneConfiguration redisConfig =
				new RedisStandaloneConfiguration("127.0.0.1", redisServer.getBindPort());
		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
				.clientOptions(ClientOptions.builder()
						.protocolVersion(ProtocolVersion.RESP2)
						.build())
				.build();
		LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
		factory.afterPropertiesSet();
		return factory;
	}

	/** 清空当前库，避免用例互相脏数据 */
	private void flushDb() {
		try (RedisConnection connection = connectionFactory.getConnection()) {
			connection.serverCommands().flushDb();
		}
	}

	/** init 之后应该配好 JDK 序列化的 Object RedisTemplate */
	@Test
	void init_shouldBuildJdkObjectRedisTemplate() {
		Assertions.assertNotNull(dao.objectRedisTemplate);
		Assertions.assertTrue(dao.objectRedisTemplate.getKeySerializer() instanceof StringRedisSerializer);
		Assertions.assertTrue(dao.objectRedisTemplate.getHashKeySerializer() instanceof StringRedisSerializer);
		Assertions.assertTrue(dao.objectRedisTemplate.getValueSerializer() instanceof JdkSerializationRedisSerializer);
		Assertions.assertTrue(dao.objectRedisTemplate.getHashValueSerializer() instanceof JdkSerializationRedisSerializer);
	}

	/** 已经初始化过再 init，不应该换掉 Object RedisTemplate */
	@Test
	void init_shouldSkipWhenAlreadyInitialized() {
		RedisTemplate<String, Object> first = dao.objectRedisTemplate;

		dao.init(connectionFactory);

		Assertions.assertTrue(dao.isInit);
		Assertions.assertSame(first, dao.objectRedisTemplate);
	}

	/** setObject + getObject 应该能把自定义对象原样读回来 */
	@Test
	void setObject_getObject_roundtrip() {
		DemoUser user = new DemoUser("zhangsan", 18);
		dao.setObject("user:1", user, 200);

		Assertions.assertEquals(user, dao.getObject("user:1"));
		Assertions.assertEquals(user, dao.getObject("user:1", DemoUser.class));
	}

	/** 不存在的 key 取 Object 时应该返回 null */
	@Test
	void getObject_shouldReturnNullWhenMissing() {
		Assertions.assertNull(dao.getObject("missing"));
		Assertions.assertNull(dao.getObject("missing", DemoUser.class));
	}

	/** timeout=-1 时 Object 应该永久存储 */
	@Test
	void setObject_shouldStoreNeverExpire() {
		dao.setObject("user:1", new DemoUser("zhangsan", 18), SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals(new DemoUser("zhangsan", 18), dao.getObject("user:1"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, dao.getObjectTimeout("user:1"));
	}

	/** timeout=0 或 -2 时应该不存储 Object */
	@Test
	void setObject_shouldIgnoreInvalidTimeout() {
		dao.setObject("user:1", new DemoUser("zhangsan", 18), 0);
		Assertions.assertNull(dao.getObject("user:1"));

		dao.setObject("user:2", new DemoUser("lisi", 20), SaTokenDao.NOT_VALUE_EXPIRE);
		Assertions.assertNull(dao.getObject("user:2"));

		dao.setObject("user:3", new DemoUser("wangwu", 22), -9);
		Assertions.assertNull(dao.getObject("user:3"));
	}

	/** timeout 非法时，已经存在的 Object 应该原样保留 */
	@Test
	void setObject_shouldKeepOldValueWhenTimeoutInvalid() {
		dao.setObject("user:1", new DemoUser("zhangsan", 18), 60);
		dao.setObject("user:1", new DemoUser("lisi", 20), 0);
		Assertions.assertEquals(new DemoUser("zhangsan", 18), dao.getObject("user:1"));
	}

	/** 限时 Object 的 getObjectTimeout 应该返回剩余秒数 */
	@Test
	void getObjectTimeout_shouldReturnRemainingSeconds() {
		dao.setObject("user:1", new DemoUser("zhangsan", 18), 200);
		long timeout = dao.getObjectTimeout("user:1");
		Assertions.assertTrue(timeout > 195 && timeout <= 200);
	}

	/** 不存在的 key 调用 getObjectTimeout 应该返回 NOT_VALUE_EXPIRE */
	@Test
	void getObjectTimeout_shouldReturnNotValueExpireWhenMissing() {
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, dao.getObjectTimeout("missing"));
	}

	/** updateObject 应该改对象但保住原来的 TTL；对不存在的 key 应该什么都不做 */
	@Test
	void updateObject_shouldChangeValueAndKeepTtl() {
		dao.setObject("user:1", new DemoUser("zhangsan", 18), 200);
		dao.updateObject("user:1", new DemoUser("lisi", 20));
		Assertions.assertEquals(new DemoUser("lisi", 20), dao.getObject("user:1"));
		Assertions.assertTrue(dao.getObjectTimeout("user:1") > 195);

		dao.updateObject("missing", new DemoUser("wangwu", 22));
		Assertions.assertNull(dao.getObject("missing"));
	}

	/** updateObjectTimeout 应该改 Object 的剩余存活时间 */
	@Test
	void updateObjectTimeout_shouldChangeExpire() {
		dao.setObject("user:1", new DemoUser("zhangsan", 18), 200);
		dao.updateObjectTimeout("user:1", 500);
		long timeout = dao.getObjectTimeout("user:1");
		Assertions.assertTrue(timeout > 495 && timeout <= 500);
	}

	/** 把限时 Object 改成永久时，getObjectTimeout 应该变成 NEVER_EXPIRE */
	@Test
	void updateObjectTimeout_shouldConvertToNeverExpire() {
		dao.setObject("user:1", new DemoUser("zhangsan", 18), 200);
		dao.updateObjectTimeout("user:1", SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals(new DemoUser("zhangsan", 18), dao.getObject("user:1"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, dao.getObjectTimeout("user:1"));
	}

	/** 本来就是永久的 Object，再 updateObjectTimeout(-1) 时应该保持永久 */
	@Test
	void updateObjectTimeout_shouldKeepNeverExpireWhenAlreadyPermanent() {
		dao.setObject("user:1", new DemoUser("zhangsan", 18), SaTokenDao.NEVER_EXPIRE);
		dao.updateObjectTimeout("user:1", SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals(new DemoUser("zhangsan", 18), dao.getObject("user:1"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, dao.getObjectTimeout("user:1"));
	}

	/** deleteObject 之后应该取不到值 */
	@Test
	void deleteObject_shouldRemoveValue() {
		dao.setObject("user:1", new DemoUser("zhangsan", 18), 60);
		dao.deleteObject("user:1");
		Assertions.assertNull(dao.getObject("user:1"));
	}

	/** 重写 wrapKey 后，Object 读写删超时都应该走包装后的 Redis 键 */
	@Test
	void wrapKey_shouldAffectObjectGetSetDeleteTimeout() {
		SaTokenDaoForRedisTemplateUseJdkSerializer prefixedDao = createPrefixedDao("app:");
		DemoUser user = new DemoUser("zhangsan", 18);
		prefixedDao.setObject("user:1", user, 60);

		Assertions.assertEquals(user, prefixedDao.getObject("user:1"));
		Assertions.assertEquals(user, dao.getObject("app:user:1"));
		Assertions.assertNull(dao.getObject("user:1"));
		Assertions.assertTrue(prefixedDao.getObjectTimeout("user:1") > 0);

		prefixedDao.deleteObject("user:1");
		Assertions.assertNull(prefixedDao.getObject("user:1"));
		Assertions.assertNull(dao.getObject("app:user:1"));
	}

	/** 改成永久时应该继续用原始 key 调 getObject/setObject，避免 wrap 两次 */
	@Test
	void wrapKey_shouldNotDoubleWrapWhenUpdateObjectTimeoutToNeverExpire() {
		SaTokenDaoForRedisTemplateUseJdkSerializer prefixedDao = createPrefixedDao("app:");
		DemoUser user = new DemoUser("zhangsan", 18);
		prefixedDao.setObject("user:1", user, 60);

		prefixedDao.updateObjectTimeout("user:1", SaTokenDao.NEVER_EXPIRE);

		Assertions.assertEquals(user, prefixedDao.getObject("user:1"));
		Assertions.assertEquals(user, dao.getObject("app:user:1"));
		Assertions.assertNull(dao.getObject("app:app:user:1"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, prefixedDao.getObjectTimeout("user:1"));
	}

	/** 创建一个会给 Redis 键加前缀的 JDK 序列化 Dao */
	private SaTokenDaoForRedisTemplateUseJdkSerializer createPrefixedDao(String prefix) {
		SaTokenDaoForRedisTemplateUseJdkSerializer prefixedDao = new SaTokenDaoForRedisTemplateUseJdkSerializer() {
			@Override
			public String wrapKey(String key) {
				return prefix + key;
			}
		};
		prefixedDao.init(connectionFactory);
		return prefixedDao;
	}

	/** 测试用可序列化对象，用来确认走的是 JDK 序列化而不是纯字符串 */
	public static class DemoUser implements Serializable {

		private static final long serialVersionUID = 1L;

		public String name;

		public int age;

		public DemoUser() {
		}

		public DemoUser(String name, int age) {
			this.name = name;
			this.age = age;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			DemoUser demoUser = (DemoUser) o;
			return age == demoUser.age && Objects.equals(name, demoUser.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, age);
		}
	}

}
