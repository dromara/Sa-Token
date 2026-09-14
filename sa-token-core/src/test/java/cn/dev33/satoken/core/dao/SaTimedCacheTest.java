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
package cn.dev33.satoken.core.dao;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.timedcache.SaMapPackageForConcurrentHashMap;
import cn.dev33.satoken.dao.timedcache.SaTimedCache;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaTimedCache put/get/timeout 测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTimedCacheTest {

	private SaTimedCache newCache() {
		return new SaTimedCache(
				new SaMapPackageForConcurrentHashMap<>(),
				new SaMapPackageForConcurrentHashMap<>()
		);
	}

	/** put/get 应正常存取并记录超时 */
	@Test
	void putAndGet() {
		SaTimedCache cache = newCache();
		cache.setObject("k1", "v1", 60);
		Assertions.assertEquals("v1", cache.getObject("k1"));
		Assertions.assertTrue(cache.getObjectTimeout("k1") > 0);
		Assertions.assertTrue(cache.keySet().contains("k1"));
	}

	/** timeout 为 0 或 NOT_VALUE_EXPIRE 时不应写入 */
	@Test
	void ignoreInvalidTimeoutOnSet() {
		SaTimedCache cache = newCache();
		cache.setObject("k", "v", 0);
		cache.setObject("k2", "v2", SaTokenDao.NOT_VALUE_EXPIRE);
		Assertions.assertNull(cache.getObject("k"));
		Assertions.assertNull(cache.getObject("k2"));
	}

	/** NEVER_EXPIRE 时应永久保存 */
	@Test
	void neverExpire() {
		SaTimedCache cache = newCache();
		cache.setObject("k", "v", SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals("v", cache.getObject("k"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, cache.getObjectTimeout("k"));
	}

	/** update/delete 应正确更新或移除缓存 */
	@Test
	void updateAndDelete() {
		SaTimedCache cache = newCache();
		cache.setObject("k", "v1", 60);
		cache.updateObject("k", "v2");
		Assertions.assertEquals("v2", cache.getObject("k"));

		cache.updateObject("missing", "x");
		Assertions.assertNull(cache.getObject("missing"));

		cache.deleteObject("k");
		Assertions.assertNull(cache.getObject("k"));
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, cache.getObjectTimeout("k"));
	}

	/** updateObjectTimeout 应延长剩余有效期 */
	@Test
	void updateObjectTimeout() {
		SaTimedCache cache = newCache();
		cache.setObject("k", "v", 10);
		cache.updateObjectTimeout("k", 120);
		Assertions.assertTrue(cache.getObjectTimeout("k") >= 60);
	}

	/** 过期 key 在 get 时应被清除 */
	@Test
	void expiredKeyClearedOnGet() throws InterruptedException {
		SaTimedCache cache = newCache();
		cache.setObject("k", "v", 1);
		Thread.sleep(1100);
		Assertions.assertNull(cache.getObject("k"));
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, cache.getObjectTimeout("k"));
	}

	/** refreshDataMap 应清除过期 key */
	@Test
	void refreshDataMapClearsExpiredKeys() throws InterruptedException {
		SaTimedCache cache = newCache();
		cache.setObject("k", "v", 1);
		Thread.sleep(1100);
		cache.refreshDataMap();
		Assertions.assertFalse(cache.keySet().contains("k"));
	}

	/** dataRefreshPeriod 为 0 时不启动刷新线程 */
	@Test
	void initRefreshThread_skipsWhenPeriodDisabled() {
		SaManager.getConfig().setDataRefreshPeriod(0);
		SaTimedCache cache = newCache();
		cache.initRefreshThread();
		Assertions.assertNull(cache.refreshThread);
	}

	/** endRefreshThread 应将 refreshFlag 置为 false */
	@Test
	void endRefreshThreadStopsFlag() {
		SaTimedCache cache = newCache();
		cache.refreshFlag = true;
		cache.endRefreshThread();
		Assertions.assertFalse(cache.refreshFlag);
	}

}
