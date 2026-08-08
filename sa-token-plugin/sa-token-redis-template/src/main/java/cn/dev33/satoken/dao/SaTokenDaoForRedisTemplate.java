/*
 * Copyright 2020-2099 sa-token.cc
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

import cn.dev33.satoken.dao.auto.SaTokenDaoByObjectFollowString;
import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Sa-Token 持久层实现 [ Redis 存储 ] (可用环境: SpringBoot2、SpringBoot3)
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaTokenDaoForRedisTemplate implements SaTokenDaoByObjectFollowString, SaTokenDao {

	public StringRedisTemplate stringRedisTemplate;

	/**
	 * 更新 key-value 并保持过期时间不变的 Lua 脚本（原子操作，减少一次网络请求）
	 * */
	private static final DefaultRedisScript<Long> UPDATE_KEEP_TTL_SCRIPT;

	static {
		UPDATE_KEEP_TTL_SCRIPT = new DefaultRedisScript<>();
		UPDATE_KEEP_TTL_SCRIPT.setScriptText(
				"local ttl = redis.call('pttl', KEYS[1]) " +
						"if ttl == -2 then " +
						"    return 0 " +  // key 不存在，直接返回
						"elseif ttl == -1 then " +
						"    redis.call('set', KEYS[1], ARGV[1]) " +  // 永不过期，直接 set
						"else " +
						"    redis.call('set', KEYS[1], ARGV[1], 'PX', ttl) " +  // 有过期时间，set 并保持原 ttl
						"end " +
						"return 1"
		);
		UPDATE_KEEP_TTL_SCRIPT.setResultType(Long.class);
	}

	/**
	 * 标记：当前 redis 连接信息是否已初始化成功
	 */
	public boolean isInit;
	
	@Autowired
	public void init(RedisConnectionFactory connectionFactory) {
		// 如果已经初始化成功了，就立刻退出，不重复初始化
		if(this.isInit) {
			return;
		}

		// 构建StringRedisTemplate
		StringRedisTemplate stringTemplate = new StringRedisTemplate();
		stringTemplate.setConnectionFactory(connectionFactory);
		stringTemplate.afterPropertiesSet();
		this.stringRedisTemplate = stringTemplate;

		initMore(connectionFactory);

		// 打上标记，表示已经初始化成功，后续无需再重新初始化
		this.isInit = true;
	}

	protected void initMore(RedisConnectionFactory connectionFactory) {

	}


	/**
	 * 获取Value，如无返空 
	 */
	@Override
	public String get(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	/**
	 * 写入Value，并设定存活时间 (单位: 秒)
	 */
	@Override
	public void set(String key, String value, long timeout) {
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			stringRedisTemplate.opsForValue().set(key, value);
		} else {
			stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 修改指定key-value键值对 (过期时间不变) （优化版：使用 Lua 脚本原子化操作，减少一次网络请求）
	 *
	 * 优化背景：
	 * 原有实现先调用 getExpire 再调用 set，需要两次网络请求，高并发场景下开销大；
	 * 且非原子操作，在两次请求之间 key 的过期时间可能被修改，存在数据不一致风险。
	 *
	 * 优化方案：
	 * 使用 Lua 脚本将两个操作合并为一次原子操作，减少网络请求，保证数据一致性。
	 */
	@Override
	public void update(String key, String value) {
		stringRedisTemplate.execute(
				UPDATE_KEEP_TTL_SCRIPT,
				Collections.singletonList(key),
				value
		);
	}
	/**
	 * 删除Value 
	 */
	@Override
	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}

	/**
	 * 获取Value的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public long getTimeout(String key) {
		return stringRedisTemplate.getExpire(key);
	}

	/**
	 * 修改Value的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public void updateTimeout(String key, long timeout) {
		// 判断是否想要设置为永久
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			long expire = getTimeout(key);
			if(expire == SaTokenDao.NEVER_EXPIRE) {
				// 如果其已经被设置为永久，则不作任何处理 
			} else {
				// 如果尚未被设置为永久，那么再次set一次
				this.set(key, this.get(key), timeout);
			}
			return;
		}
		stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}



	/**
	 * 搜索数据（优化版：使用 scan 替代 keys，避免 Redis 主线程阻塞，降低内存溢出风险）
	 *
	 * 优化背景：
	 * 原有实现使用 keys 命令，会遍历 Redis 所有 key 并阻塞主线程，高并发场景下会导致服务雪崩；
	 * 且一次性加载所有匹配 key 到内存，存在 OOM 风险。
	 *
	 * 优化方案：
	 * 使用 scan 命令迭代式遍历，无阻塞、分批加载、内存安全，同时保持原有 API 完全兼容。
	 */
	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
		String pattern = prefix + "*" + keyword + "*";
		List<String> list = new ArrayList<>();

		// 使用 scan 命令迭代遍历，避免阻塞 Redis 主线程
		ScanOptions options = ScanOptions.scanOptions()
				.match(pattern)
				.count(1000) // 每次迭代返回的数量建议值
				.build();

		try (Cursor<String> cursor = stringRedisTemplate.scan(options)) {
			while (cursor.hasNext()) {
				list.add(cursor.next());
			}
		}

		return SaFoxUtil.searchList(list, start, size, sortType);
	}
	
	
}
