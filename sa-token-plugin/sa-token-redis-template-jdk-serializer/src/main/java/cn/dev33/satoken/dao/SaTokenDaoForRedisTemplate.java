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

import cn.dev33.satoken.dao.auto.SaTokenDaoByObjectFollowString;
import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Sa-Token 持久层实现 [ RedisTemplate 存储 ] (可用环境: SpringBoot2、SpringBoot3、SpringBoot4)
 * <br> copy by: sa-token-redis-template 插件
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaTokenDaoForRedisTemplate implements SaTokenDaoByObjectFollowString, SaTokenDao {

	public StringRedisTemplate stringRedisTemplate;

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
		String finalKey = wrapKey(key);
		return stringRedisTemplate.opsForValue().get(finalKey);
	}

	/**
	 * 写入Value，并设定存活时间 (单位: 秒)
	 */
	@Override
	public void set(String key, String value, long timeout) {
		String finalKey = wrapKey(key);
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			stringRedisTemplate.opsForValue().set(finalKey, value);
		} else {
			stringRedisTemplate.opsForValue().set(finalKey, value, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 修改指定key-value键值对 (过期时间不变) 
	 */
	@Override
	public void update(String key, String value) {
		String finalKey = wrapKey(key);
		@SuppressWarnings("all")
		long expireMs = stringRedisTemplate.getExpire(finalKey, TimeUnit.MILLISECONDS);
		// -2 = 无此键
		if (expireMs == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		// -1 = 永不过期
		if(expireMs == SaTokenDao.NEVER_EXPIRE) {
			stringRedisTemplate.opsForValue().set(finalKey, value);
		} else {
			stringRedisTemplate.opsForValue().set(finalKey, value, expireMs, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * 删除Value 
	 */
	@Override
	public void delete(String key) {
		String finalKey = wrapKey(key);
		stringRedisTemplate.delete(finalKey);
	}

	/**
	 * 获取Value的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public long getTimeout(String key) {
		String finalKey = wrapKey(key);
		return stringRedisTemplate.getExpire(finalKey);
	}

	/**
	 * 修改Value的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public void updateTimeout(String key, long timeout) {
		String finalKey = wrapKey(key);
		// 判断是否想要设置为永久
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			// 调用本类其它方法时使用原始 key，避免二次 wrap
			long expire = getTimeout(key);
			if(expire == SaTokenDao.NEVER_EXPIRE) {
				// 如果其已经被设置为永久，则不作任何处理 
			} else {
				// 如果尚未被设置为永久，那么再次set一次
				this.set(key, this.get(key), timeout);
			}
			return;
		}
		stringRedisTemplate.expire(finalKey, timeout, TimeUnit.SECONDS);
	}


	
	/**
	 * 搜索数据 
	 */
	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
		// 对完整匹配串做 wrap，避免用户在 key 尾部等位置加工时只 wrap(prefix) 拼错 pattern
		String finalPattern = wrapKey(prefix + "*" + keyword + "*");
		Set<String> keys = new HashSet<>();
		ScanOptions options = ScanOptions.scanOptions().match(finalPattern).count(1000).build();
		stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
			try (Cursor<byte[]> cursor = connection.scan(options)) {
				while (cursor.hasNext()) {
					keys.add(stringRedisTemplate.getStringSerializer().deserialize(cursor.next()));
				}
			}
			return null;
		});
		return SaFoxUtil.searchList(new ArrayList<>(keys), start, size, sortType);
	}

	/**
	 * 包装 key（默认原样返回）。需要给 Redis 键加统一前缀时，可重写此方法。
	 *
	 * @param key 原始 key
	 * @return 包装后的 key
	 */
	public String wrapKey(String key) {
		return key;
	}
	
	
}
