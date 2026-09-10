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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.Protocol;

/**
 * {@link SaTokenDaoForRedisson} 字符串读写、超时与搜索测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenDaoForRedissonTest extends SaTokenDaoStringTestCommon {

	private RedissonClient client;

	@Override
	protected SaTokenDao createDao(String host, int port) {
		closeClient();
		Config config = new Config();
		config.setProtocol(Protocol.RESP2);
		config.useSingleServer().setAddress("redis://" + host + ":" + port);
		client = Redisson.create(config);
		client.getKeys().flushdb();
		return new SaTokenDaoForRedisson(client);
	}

	@Override
	protected void closeDao(SaTokenDao dao) {
		closeClient();
	}

	/** 关掉本用例里的 Redisson 客户端 */
	private void closeClient() {
		if (client != null && !client.isShuttingDown()) {
			client.shutdown();
			client = null;
		}
	}

	/** 默认构造应该用 StringCodec */
	@Test
	void constructor_shouldUseStringCodecByDefault() {
		SaTokenDaoForRedisson redissonDao = (SaTokenDaoForRedisson) dao;
		Assertions.assertNotNull(redissonDao.redissonClient);
		Assertions.assertSame(StringCodec.INSTANCE, redissonDao.codec);
	}

	/** 指定 codec 时应该按这个 codec 读写 */
	@Test
	void constructor_shouldKeepCustomCodec() {
		SaTokenDaoForRedisson custom = new SaTokenDaoForRedisson(client, StringCodec.INSTANCE);
		Assertions.assertSame(StringCodec.INSTANCE, custom.codec);
		custom.set("codec-key", "v", 60);
		Assertions.assertEquals("v", custom.get("codec-key"));
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
