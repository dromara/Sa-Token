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
package com.pj.test.redis;

import cn.dev33.satoken.dao.SaTokenDao;
import com.github.fppt.jedismock.RedisServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.List;

/**
 * {@link SaTokenDao} 字符串读写公共测试基类：基于内嵌 Redis 做真实往返验证。
 * 各 Redis Dao 插件测试类继承本类，只需实现 {@link #createDao(String, int)}，
 * 测试方法统一调用本类的 protected 断言方法（本类不承载 @Test）。
 *
 * @author click33
 * @since 1.46.0
 */
public abstract class SaTokenDaoStringTestCommon {

	protected static final String SEARCH_PREFIX = "satoken:login:token:";

	protected static RedisServer redisServer;

	protected SaTokenDao dao;

	/** 启动无密码的内嵌 Redis，端口随机 */
	@BeforeAll
	public static void startRedis() throws IOException {
		redisServer = JedisMockRedisSupport.startServer();
	}

	/** 测完把内嵌 Redis 关掉 */
	@AfterAll
	public static void stopRedis() throws IOException {
		JedisMockRedisSupport.stopServer(redisServer);
	}

	/** 每个用例开始前按当前内嵌 Redis 创建 Dao */
	@BeforeEach
	public void setUpDao() {
		dao = createDao("127.0.0.1", redisServer.getBindPort());
	}

	/** 每个用例结束后把 Dao 背后的客户端清掉 */
	@AfterEach
	public void tearDownDao() {
		closeDao(dao);
	}

	/** 按 host/port 创建被测 Dao，并保证当前库是空的 */
	protected abstract SaTokenDao createDao(String host, int port);

	/** 关掉 Dao 背后的客户端；没有可关的就空着 */
	protected void closeDao(SaTokenDao dao) {
	}

	/** 连上内嵌 Redis 后，set/get 应该能正常读写 */
	protected void assertGetShouldReturnValueAfterSet() {
		dao.set("name", "zhangsan", 60);
		Assertions.assertEquals("zhangsan", dao.get("name"));
	}

	/** 不存在的 key 取值时应该返回 null */
	protected void assertGetShouldReturnNullWhenMissing() {
		Assertions.assertNull(dao.get("missing"));
	}

	/** timeout=0 时应该不写入 */
	protected void assertSetShouldIgnoreZeroTimeout() {
		dao.set("avatar", "1.jpg", 0);
		Assertions.assertNull(dao.get("avatar"));
	}

	/** timeout=-2 时应该不写入 */
	protected void assertSetShouldIgnoreNotValueExpireTimeout() {
		dao.set("avatar", "1.jpg", SaTokenDao.NOT_VALUE_EXPIRE);
		Assertions.assertNull(dao.get("avatar"));
	}

	/** timeout 比 -2 更小时也应该不写入 */
	protected void assertSetShouldIgnoreTimeoutLessThanNotValueExpire() {
		dao.set("avatar", "1.jpg", -9);
		Assertions.assertNull(dao.get("avatar"));
	}

	/** timeout 非法时，已经存在的值应该原样保留 */
	protected void assertSetShouldKeepOldValueWhenTimeoutInvalid() {
		dao.set("name", "zhangsan", 60);
		dao.set("name", "lisi", 0);
		Assertions.assertEquals("zhangsan", dao.get("name"));
	}

	/** timeout=-1 时应该永久存储，getTimeout 应该返回 NEVER_EXPIRE */
	protected void assertSetShouldStoreNeverExpire() {
		dao.set("age", "20", SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals("20", dao.get("age"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, dao.getTimeout("age"));
	}

	/** delete 之后应该取不到值 */
	protected void assertDeleteShouldRemoveValue() {
		dao.set("name", "zhangsan", 60);
		dao.delete("name");
		Assertions.assertNull(dao.get("name"));
	}

	/** 删除不存在的 key 时应该不抛异常 */
	protected void assertDeleteShouldIgnoreMissingKey() {
		Assertions.assertDoesNotThrow(() -> dao.delete("missing"));
	}

	/** 限时 key 的 getTimeout 应该返回剩余秒数 */
	protected void assertGetTimeoutShouldReturnRemainingSeconds() {
		dao.set("name", "zhangsan", 200);
		long timeout = dao.getTimeout("name");
		Assertions.assertTrue(timeout > 195 && timeout <= 200);
	}

	/** 不存在的 key 调用 getTimeout 应该返回 NOT_VALUE_EXPIRE */
	protected void assertGetTimeoutShouldReturnNotValueExpireWhenMissing() {
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, dao.getTimeout("missing"));
	}

	/** update 应该改值但保住原来的 TTL；对不存在的 key 应该什么都不做 */
	protected void assertUpdateShouldChangeValueAndKeepTtl() {
		dao.set("name", "zhangsan", 200);
		dao.update("name", "lisi");
		Assertions.assertEquals("lisi", dao.get("name"));
		Assertions.assertTrue(dao.getTimeout("name") > 195);

		dao.update("missing", "wangwu");
		Assertions.assertNull(dao.get("missing"));
	}

	/** updateTimeout 应该改剩余存活时间 */
	protected void assertUpdateTimeoutShouldChangeExpire() {
		dao.set("name", "zhangsan", 200);
		dao.updateTimeout("name", 500);
		long timeout = dao.getTimeout("name");
		Assertions.assertTrue(timeout > 495 && timeout <= 500);
	}

	/** 把限时 key 改成永久时，getTimeout 应该变成 NEVER_EXPIRE */
	protected void assertUpdateTimeoutShouldConvertToNeverExpire() {
		dao.set("name", "zhangsan", 200);
		dao.updateTimeout("name", SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals("zhangsan", dao.get("name"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, dao.getTimeout("name"));
	}

	/** 本来就是永久的 key，再 updateTimeout(-1) 时应该保持永久 */
	protected void assertUpdateTimeoutShouldKeepNeverExpireWhenAlreadyPermanent() {
		dao.set("age", "20", SaTokenDao.NEVER_EXPIRE);
		dao.updateTimeout("age", SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals("20", dao.get("age"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, dao.getTimeout("age"));
	}

	/** 按前缀搜索时应该只返回该前缀下的 key */
	protected void assertSearchDataShouldReturnKeysUnderPrefix() {
		dao.set(SEARCH_PREFIX + "token-a", "1", 60);
		dao.set(SEARCH_PREFIX + "token-b", "1", 60);
		dao.set("satoken:login:session:s1", "1", 60);

		List<String> list = dao.searchData(SEARCH_PREFIX, "", 0, -1, true);

		Assertions.assertEquals(2, list.size());
		Assertions.assertTrue(list.contains(SEARCH_PREFIX + "token-a"));
		Assertions.assertTrue(list.contains(SEARCH_PREFIX + "token-b"));
	}

	/** 带 keyword 时应该只留下命中关键字的 key */
	protected void assertSearchDataShouldFilterByKeyword() {
		dao.set(SEARCH_PREFIX + "user-10001", "1", 60);
		dao.set(SEARCH_PREFIX + "user-10002", "1", 60);
		dao.set(SEARCH_PREFIX + "guest-10001", "1", 60);

		List<String> list = dao.searchData(SEARCH_PREFIX, "10001", 0, -1, true);

		Assertions.assertEquals(2, list.size());
		Assertions.assertTrue(list.contains(SEARCH_PREFIX + "user-10001"));
		Assertions.assertTrue(list.contains(SEARCH_PREFIX + "guest-10001"));
	}

	/** 分页参数应该能从完整结果里切出对应窗口 */
	protected void assertSearchDataShouldSupportPagination() {
		dao.set(SEARCH_PREFIX + "k-01", "1", 60);
		dao.set(SEARCH_PREFIX + "k-02", "1", 60);
		dao.set(SEARCH_PREFIX + "k-03", "1", 60);
		dao.set(SEARCH_PREFIX + "k-04", "1", 60);
		dao.set(SEARCH_PREFIX + "k-05", "1", 60);

		List<String> all = dao.searchData(SEARCH_PREFIX, "", 0, -1, true);
		List<String> page = dao.searchData(SEARCH_PREFIX, "", 1, 2, true);

		Assertions.assertEquals(2, page.size());
		Assertions.assertEquals(all.get(1), page.get(0));
		Assertions.assertEquals(all.get(2), page.get(1));
	}

	/** sortType=false 时应该把结果倒过来 */
	protected void assertSearchDataShouldSupportReverseSort() {
		dao.set(SEARCH_PREFIX + "k-01", "1", 60);
		dao.set(SEARCH_PREFIX + "k-02", "1", 60);
		dao.set(SEARCH_PREFIX + "k-03", "1", 60);

		List<String> asc = dao.searchData(SEARCH_PREFIX, "", 0, -1, true);
		List<String> desc = dao.searchData(SEARCH_PREFIX, "", 0, -1, false);

		Assertions.assertEquals(asc.size(), desc.size());
		Assertions.assertEquals(asc.get(0), desc.get(desc.size() - 1));
		Assertions.assertEquals(asc.get(asc.size() - 1), desc.get(0));
	}

	/** 关键字一个都对不上时应该返回空列表 */
	protected void assertSearchDataShouldReturnEmptyWhenNoMatch() {
		dao.set(SEARCH_PREFIX + "only-one", "1", 60);

		List<String> list = dao.searchData(SEARCH_PREFIX, "not-exist-keyword", 0, -1, true);

		Assertions.assertTrue(list.isEmpty());
	}

	/** Redis 里没有数据时搜索应该返回空列表 */
	protected void assertSearchDataShouldReturnEmptyWhenRedisIsEmpty() {
		List<String> list = dao.searchData(SEARCH_PREFIX, "", 0, -1, true);
		Assertions.assertTrue(list.isEmpty());
	}

	/** start 已经超过结果长度时应该返回空列表 */
	protected void assertSearchDataShouldReturnEmptyWhenStartBeyondEnd() {
		dao.set(SEARCH_PREFIX + "only", "1", 60);

		List<String> list = dao.searchData(SEARCH_PREFIX, "", 10, 5, true);

		Assertions.assertTrue(list.isEmpty());
	}

}
