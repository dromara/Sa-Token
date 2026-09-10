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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * SaTokenDaoForRedisTemplate#searchData 的 SCAN 去重测试（真实 Redis 搜索已并入主测试类）
 */
public class SaTokenDaoForRedisTemplateSearchDataTest {

	private static final String PREFIX = "satoken:login:token:";

	/** SCAN 扫到重复 key 时，结果里应该只留一份 */
	@Test
	void searchData_shouldDeduplicateScanResults() {
		String duplicateKey = PREFIX + "dup-key";
		byte[] keyBytes = duplicateKey.getBytes(StandardCharsets.UTF_8);

		Cursor<byte[]> cursor = mock(Cursor.class);
		when(cursor.hasNext()).thenReturn(true, true, true, false);
		when(cursor.next()).thenReturn(keyBytes, keyBytes, keyBytes);

		RedisConnection connection = mock(RedisConnection.class);
		when(connection.scan(any(ScanOptions.class))).thenReturn(cursor);

		RedisConnectionFactory mockFactory = mock(RedisConnectionFactory.class);
		when(mockFactory.getConnection()).thenReturn(connection);

		SaTokenDaoForRedisTemplate mockDao = new SaTokenDaoForRedisTemplate();
		mockDao.stringRedisTemplate = new StringRedisTemplate(mockFactory);
		mockDao.stringRedisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
		mockDao.stringRedisTemplate.setValueSerializer(StringRedisSerializer.UTF_8);
		mockDao.stringRedisTemplate.afterPropertiesSet();

		List<String> list = mockDao.searchData(PREFIX, "dup", 0, -1, true);

		Assertions.assertEquals(1, list.size());
		Assertions.assertEquals(duplicateKey, list.get(0));
	}

}
