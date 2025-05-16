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

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

/**
 * Sa-Token 持久层实现 [ RedisTemplate 存储、JDK默认序列化 ] (可用环境: SpringBoot2、SpringBoot3)
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaTokenDaoForRedisTemplateUseJdkSerializer extends SaTokenDaoForRedisTemplate implements SaTokenDao {

	/**
	 * Object 读写专用
	 */
	public RedisTemplate<String, Object> objectRedisTemplate;

	@Override
	protected void initMore(RedisConnectionFactory connectionFactory) {

		// 指定相应的序列化方案
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer();

		// 构建RedisTemplate
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(keySerializer);
		template.setHashKeySerializer(keySerializer);
		template.setValueSerializer(valueSerializer);
		template.setHashValueSerializer(valueSerializer);
		template.afterPropertiesSet();
		this.objectRedisTemplate = template;
	}

	
	/**
	 * 获取Object，如无返空 
	 */
	@Override
	public Object getObject(String key) {
		return objectRedisTemplate.opsForValue().get(key);
	}

	/**
	 * 获取 Object (指定反序列化类型)，如无返空
	 *
	 * @param key 键名称
	 * @return object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(String key, Class<T> classType) {
		return (T) objectRedisTemplate.opsForValue().get(key);
	}

	/**
	 * 写入Object，并设定存活时间 (单位: 秒) 
	 */
	@Override
	public void setObject(String key, Object object, long timeout) {
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			objectRedisTemplate.opsForValue().set(key, object);
		} else {
			objectRedisTemplate.opsForValue().set(key, object, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 更新Object (过期时间不变) 
	 */
	@Override
	public void updateObject(String key, Object object) {
		@SuppressWarnings("all")
		long expireMs = stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
		// -2 = 无此键
		if (expireMs == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		// -1 = 永不过期
		if(expireMs == SaTokenDao.NEVER_EXPIRE) {
			objectRedisTemplate.opsForValue().set(key, object);
		} else {
			objectRedisTemplate.opsForValue().set(key, object, expireMs, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * 删除Object 
	 */
	@Override
	public void deleteObject(String key) {
		objectRedisTemplate.delete(key);
	}

	/**
	 * 获取Object的剩余存活时间 (单位: 秒)
	 */
	@Override
	public long getObjectTimeout(String key) {
		return objectRedisTemplate.getExpire(key);
	}

	/**
	 * 修改Object的剩余存活时间 (单位: 秒)
	 */
	@Override
	public void updateObjectTimeout(String key, long timeout) {
		// 判断是否想要设置为永久
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			long expire = getObjectTimeout(key);
			if(expire == SaTokenDao.NEVER_EXPIRE) {
				// 如果其已经被设置为永久，则不作任何处理 
			} else {
				// 如果尚未被设置为永久，那么再次set一次
				this.setObject(key, this.getObject(key), timeout);
			}
			return;
		}
		objectRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}


}
