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

import cn.dev33.satoken.test.redis.SaTokenDaoStringTestCommon;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * {@link SaTokenDaoForRedisTemplate} 字符串读写、超时与搜索测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenDaoForRedisTemplateTest extends SaTokenDaoStringTestCommon {

	private LettuceConnectionFactory connectionFactory;

	@Override
	protected SaTokenDao createDao(String host, int port) {
		closeFactory();
		connectionFactory = createConnectionFactory(host, port);
		SaTokenDaoForRedisTemplate redisDao = new SaTokenDaoForRedisTemplate();
		redisDao.init(connectionFactory);
		flushDb();
		return redisDao;
	}

	@Override
	protected void closeDao(SaTokenDao dao) {
		closeFactory();
	}

	/** 创建连到当前内嵌 Redis 的 Lettuce 工厂，强制 RESP2 */
	private LettuceConnectionFactory createConnectionFactory(String host, int port) {
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);
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

	/** 关掉本用例里的 Lettuce 工厂 */
	private void closeFactory() {
		if (connectionFactory != null) {
			connectionFactory.destroy();
			connectionFactory = null;
		}
	}

	/** 创建一个会给 Redis 键加前缀的 Dao */
	private SaTokenDaoForRedisTemplate createPrefixedDao(String prefix) {
		SaTokenDaoForRedisTemplate prefixedDao = new SaTokenDaoForRedisTemplate() {
			@Override
			public String wrapKey(String key) {
				return prefix + key;
			}
		};
		prefixedDao.init(connectionFactory);
		return prefixedDao;
	}

	/** 已经初始化过的 Dao，再 init 一次不应该换掉 RedisTemplate */
	@Test
	void init_shouldSkipWhenAlreadyInitialized() {
		SaTokenDaoForRedisTemplate redisDao = (SaTokenDaoForRedisTemplate) dao;
		Assertions.assertTrue(redisDao.isInit);
		StringRedisTemplate first = redisDao.stringRedisTemplate;

		redisDao.init(connectionFactory);

		Assertions.assertTrue(redisDao.isInit);
		Assertions.assertSame(first, redisDao.stringRedisTemplate);
	}

	/** 重写 wrapKey 后，读写删超时都应该走包装后的 Redis 键 */
	@Test
	void wrapKey_shouldAffectGetSetDeleteTimeout() {
		SaTokenDaoForRedisTemplate prefixedDao = createPrefixedDao("app:");
		prefixedDao.set("name", "zhangsan", 60);

		Assertions.assertEquals("zhangsan", prefixedDao.get("name"));
		Assertions.assertEquals("zhangsan", dao.get("app:name"));
		Assertions.assertNull(dao.get("name"));
		Assertions.assertTrue(prefixedDao.getTimeout("name") > 0);

		prefixedDao.delete("name");
		Assertions.assertNull(prefixedDao.get("name"));
		Assertions.assertNull(dao.get("app:name"));
	}

	/** 改成永久时应该继续用原始 key 调 get/set，避免 wrap 两次变成 app:app:xxx */
	@Test
	void wrapKey_shouldNotDoubleWrapWhenUpdateTimeoutToNeverExpire() {
		SaTokenDaoForRedisTemplate prefixedDao = createPrefixedDao("app:");
		prefixedDao.set("name", "zhangsan", 60);

		prefixedDao.updateTimeout("name", SaTokenDao.NEVER_EXPIRE);

		Assertions.assertEquals("zhangsan", prefixedDao.get("name"));
		Assertions.assertEquals("zhangsan", dao.get("app:name"));
		Assertions.assertNull(dao.get("app:app:name"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, prefixedDao.getTimeout("name"));
	}

	/** 默认 wrapKey 应该原样返回 */
	@Test
	void wrapKey_shouldReturnOriginalKeyByDefault() {
		SaTokenDaoForRedisTemplate redisDao = (SaTokenDaoForRedisTemplate) dao;
		Assertions.assertEquals("satoken:login:token:abc", redisDao.wrapKey("satoken:login:token:abc"));
	}

	/** wrapKey 应该作用在完整 pattern 上，搜出来的 key 也带前缀 */
	@Test
	void searchData_shouldRespectWrapKeyOnFullPattern() {
		SaTokenDaoForRedisTemplate prefixedDao = createPrefixedDao("app:");
		prefixedDao.set(SEARCH_PREFIX + "wrapped-a", "1", 60);
		prefixedDao.set(SEARCH_PREFIX + "wrapped-b", "1", 60);
		prefixedDao.set("other:key", "1", 60);

		List<String> list = prefixedDao.searchData(SEARCH_PREFIX, "wrapped", 0, -1, true);

		Assertions.assertEquals(2, list.size());
		for (String key : list) {
			Assertions.assertTrue(key.startsWith("app:" + SEARCH_PREFIX));
		}
	}

	/** 前缀对不上的 key 即使关键字相同也不应该被搜到 */
	@Test
	void searchData_shouldNotMatchKeysOutsidePrefixPattern() {
		dao.set("satoken:other:token:abc", "1", 60);
		dao.set(SEARCH_PREFIX + "abc", "1", 60);

		List<String> list = dao.searchData(SEARCH_PREFIX, "abc", 0, -1, true);

		Assertions.assertEquals(1, list.size());
		Assertions.assertEquals(SEARCH_PREFIX + "abc", list.get(0));
	}

	/** key 比较多时走 SCAN 也应该能搜全 */
	@Test
	void searchData_shouldHandleManyKeysViaScan() {
		for (int i = 0; i < 50; i++) {
			dao.set(SEARCH_PREFIX + "bulk-" + String.format("%02d", i), "1", 60);
		}
		dao.set(SEARCH_PREFIX + "bulk-noise", "1", 60);

		List<String> list = dao.searchData(SEARCH_PREFIX, "bulk-", 0, -1, true);

		Assertions.assertEquals(51, list.size());
	}

	/** 连上内嵌 Redis 后，set/get 应该能正常读写 */
	@Test
	void get_shouldReturnValueAfterSet() {
		assertGetShouldReturnValueAfterSet();
	}

	/** 不存在的 key 取值时应该返回 null */
	@Test
	void get_shouldReturnNullWhenMissing() {
		assertGetShouldReturnNullWhenMissing();
	}

	/** timeout=0 时应该不写入 */
	@Test
	void set_shouldIgnoreZeroTimeout() {
		assertSetShouldIgnoreZeroTimeout();
	}

	/** timeout=-2 时应该不写入 */
	@Test
	void set_shouldIgnoreNotValueExpireTimeout() {
		assertSetShouldIgnoreNotValueExpireTimeout();
	}

	/** timeout 比 -2 更小时也应该不写入 */
	@Test
	void set_shouldIgnoreTimeoutLessThanNotValueExpire() {
		assertSetShouldIgnoreTimeoutLessThanNotValueExpire();
	}

	/** timeout 非法时，已经存在的值应该原样保留 */
	@Test
	void set_shouldKeepOldValueWhenTimeoutInvalid() {
		assertSetShouldKeepOldValueWhenTimeoutInvalid();
	}

	/** timeout=-1 时应该永久存储，getTimeout 应该返回 NEVER_EXPIRE */
	@Test
	void set_shouldStoreNeverExpire() {
		assertSetShouldStoreNeverExpire();
	}

	/** delete 之后应该取不到值 */
	@Test
	void delete_shouldRemoveValue() {
		assertDeleteShouldRemoveValue();
	}

	/** 删除不存在的 key 时应该不抛异常 */
	@Test
	void delete_shouldIgnoreMissingKey() {
		assertDeleteShouldIgnoreMissingKey();
	}

	/** 限时 key 的 getTimeout 应该返回剩余秒数 */
	@Test
	void getTimeout_shouldReturnRemainingSeconds() {
		assertGetTimeoutShouldReturnRemainingSeconds();
	}

	/** 不存在的 key 调用 getTimeout 应该返回 NOT_VALUE_EXPIRE */
	@Test
	void getTimeout_shouldReturnNotValueExpireWhenMissing() {
		assertGetTimeoutShouldReturnNotValueExpireWhenMissing();
	}

	/** update 应该改值但保住原来的 TTL；对不存在的 key 应该什么都不做 */
	@Test
	void update_shouldChangeValueAndKeepTtl() {
		assertUpdateShouldChangeValueAndKeepTtl();
	}

	/** updateTimeout 应该改剩余存活时间 */
	@Test
	void updateTimeout_shouldChangeExpire() {
		assertUpdateTimeoutShouldChangeExpire();
	}

	/** 把限时 key 改成永久时，getTimeout 应该变成 NEVER_EXPIRE */
	@Test
	void updateTimeout_shouldConvertToNeverExpire() {
		assertUpdateTimeoutShouldConvertToNeverExpire();
	}

	/** 本来就是永久的 key，再 updateTimeout(-1) 时应该保持永久 */
	@Test
	void updateTimeout_shouldKeepNeverExpireWhenAlreadyPermanent() {
		assertUpdateTimeoutShouldKeepNeverExpireWhenAlreadyPermanent();
	}

	/** 按前缀搜索时应该只返回该前缀下的 key */
	@Test
	void searchData_shouldReturnKeysUnderPrefix() {
		assertSearchDataShouldReturnKeysUnderPrefix();
	}

	/** 带 keyword 时应该只留下命中关键字的 key */
	@Test
	void searchData_shouldFilterByKeyword() {
		assertSearchDataShouldFilterByKeyword();
	}

	/** 分页参数应该能从完整结果里切出对应窗口 */
	@Test
	void searchData_shouldSupportPagination() {
		assertSearchDataShouldSupportPagination();
	}

	/** sortType=false 时应该把结果倒过来 */
	@Test
	void searchData_shouldSupportReverseSort() {
		assertSearchDataShouldSupportReverseSort();
	}

	/** 关键字一个都对不上时应该返回空列表 */
	@Test
	void searchData_shouldReturnEmptyWhenNoMatch() {
		assertSearchDataShouldReturnEmptyWhenNoMatch();
	}

	/** Redis 里没有数据时搜索应该返回空列表 */
	@Test
	void searchData_shouldReturnEmptyWhenRedisIsEmpty() {
		assertSearchDataShouldReturnEmptyWhenRedisIsEmpty();
	}

	/** start 已经超过结果长度时应该返回空列表 */
	@Test
	void searchData_shouldReturnEmptyWhenStartBeyondEnd() {
		assertSearchDataShouldReturnEmptyWhenStartBeyondEnd();
	}

}
