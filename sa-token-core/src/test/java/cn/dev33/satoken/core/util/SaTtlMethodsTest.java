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
package cn.dev33.satoken.core.util;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.temp.SaTempTemplate;
import cn.dev33.satoken.util.SaTtlMethods;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SaTtlMethods 默认方法测试（通过 SaTempTemplate 实现类调用）
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTtlMethodsTest {

	private final SaTtlMethods ttl = new SaTempTemplate();

	/** newTokenValueList/newTokenIndexMap 应返回空 ArrayList 与 LinkedHashMap */
	@Test
	void newCollections() {
		List<String> tokens = ttl.newTokenValueList();
		Map<String, Long> indexMap = ttl.newTokenIndexMap();
		Assertions.assertNotNull(tokens);
		Assertions.assertNotNull(indexMap);
		Assertions.assertTrue(tokens.isEmpty());
		Assertions.assertTrue(indexMap.isEmpty());
		Assertions.assertInstanceOf(ArrayList.class, tokens);
		Assertions.assertInstanceOf(LinkedHashMap.class, indexMap);
	}

	/** getMaxTtl 应取最大 TTL，含 NEVER_EXPIRE 时优先返回永不过期 */
	@Test
	void getMaxTtl_picksHighestOrNeverExpire() {
		ArrayList<Long> ttlList = new ArrayList<>(Arrays.asList(100L, 200L, 50L));
		Assertions.assertEquals(200L, ttl.getMaxTtl(ttlList));

		ArrayList<Long> withNever = new ArrayList<>(Arrays.asList(100L, SaTokenDao.NEVER_EXPIRE, 50L));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, ttl.getMaxTtl(withNever));
	}

	/** getMaxTtlByExpireTime 应根据过期时间戳计算最大剩余秒数 */
	@Test
	void getMaxTtlByExpireTime() {
		long future = System.currentTimeMillis() + 120_000L;
		long ttlSeconds = ttl.getMaxTtlByExpireTime(Arrays.asList(future, future + 60_000L));
		Assertions.assertTrue(ttlSeconds >= 119 && ttlSeconds <= 181);

		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE,
				ttl.getMaxTtlByExpireTime(Arrays.asList(SaTokenDao.NEVER_EXPIRE, future)));
	}

	/** expireTimeToTtl 应将过期时间戳转换为剩余秒数或特殊常量 */
	@Test
	void expireTimeToTtl() {
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, ttl.expireTimeToTtl(SaTokenDao.NEVER_EXPIRE));
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, ttl.expireTimeToTtl(SaTokenDao.NOT_VALUE_EXPIRE));
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, ttl.expireTimeToTtl(System.currentTimeMillis() - 1000));

		long future = System.currentTimeMillis() + 10_000L;
		long seconds = ttl.expireTimeToTtl(future);
		Assertions.assertTrue(seconds >= 9 && seconds <= 10);
	}

	/** ttlToExpireTime 应将 TTL 秒数转换为过期时间戳或特殊常量 */
	@Test
	void ttlToExpireTime() {
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, ttl.ttlToExpireTime(SaTokenDao.NEVER_EXPIRE));
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, ttl.ttlToExpireTime(-5));

		long before = System.currentTimeMillis();
		long expireTime = ttl.ttlToExpireTime(30);
		long after = System.currentTimeMillis();
		Assertions.assertTrue(expireTime >= before + 30_000L);
		Assertions.assertTrue(expireTime <= after + 30_000L);
	}

}
