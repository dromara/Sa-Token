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
package cn.dev33.satoken.solon.dao;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.auto.SaTokenDaoBySessionFollowObject;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sa-Token 持久层实现  [ Redisson客户端、Redis存储、Jackson序列化 ]
 * 
 * @author 疯狂的狮子Li
 * @author noear
 * @since 1.34.0
 */
public class SaTokenDaoOfRedissonJackson implements SaTokenDaoBySessionFollowObject {

	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String TIME_PATTERN = "HH:mm:ss";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

	/**
	 * ObjectMapper 对象 (以 public 作用域暴露出此对象，方便开发者二次更改配置)
	 *
	 * <p> 例如：
	 * 	<pre>
	 *      SaTokenDaoRedisJackson redisJackson = (SaTokenDaoRedisJackson) SaManager.getSaTokenDao();
	 *      redisJackson.objectMapper.xxx = xxx;
	 * 	</pre>
	 * </p>
	 */
	public final ObjectMapper objectMapper;

	/**
	 * 序列化方式
	 */
	public final Codec codec;

	/**
	 * redisson 客户端
	 */
	public final RedissonClient redissonClient;

	public SaTokenDaoOfRedissonJackson(RedissonClient redissonClient) {
		this.objectMapper = new ObjectMapper();

		this.objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

		// 配置[忽略未知字段]
		this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 配置[时间类型转换]
		JavaTimeModule timeModule = new JavaTimeModule();
		// LocalDateTime序列化与反序列化
		timeModule.addSerializer(new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
		timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
		// LocalDate序列化与反序列化
		timeModule.addSerializer(new LocalDateSerializer(DATE_FORMATTER));
		timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
		// LocalTime序列化与反序列化
		timeModule.addSerializer(new LocalTimeSerializer(TIME_FORMATTER));
		timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
		this.objectMapper.registerModule(timeModule);

		// 重写 SaSession 生成策略
		SaStrategy.instance.createSession = (sessionId) -> new SaSessionForJacksonCustomized(sessionId);


		// 开始初始化相关组件
		this.codec = new JsonJacksonCodec(objectMapper);
		this.redissonClient = redissonClient;
	}
	
	
	/**
	 * 获取Value，如无返空 
	 */
	@Override
	public String get(String key) {
		RBucket<String> rBucket = redissonClient.getBucket(key, codec);
		return rBucket.get();
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
			RBucket<String> bucket = redissonClient.getBucket(key, codec);
			bucket.set(value);
		} else {
			RBatch batch = redissonClient.createBatch();
			RBucketAsync<String> bucket = batch.getBucket(key, codec);
			bucket.setAsync(value);
			bucket.expireAsync(Duration.ofSeconds(timeout));
			batch.execute();
		}
	}

	/**
	 * 修修改指定key-value键值对 (过期时间不变) 
	 */
	@Override
	public void update(String key, String value) {
		long expire = getTimeout(key);
		// -2 = 无此键 
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		this.set(key, value, expire);
	}
	
	/**
	 * 删除Value 
	 */
	@Override
	public void delete(String key) {
		redissonClient.getBucket(key, codec).delete();
	}

	/**
	 * 获取Value的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public long getTimeout(String key) {
		RBucket<String> rBucket = redissonClient.getBucket(key, codec);
		long timeout = rBucket.remainTimeToLive();
		return timeout < 0 ? timeout : timeout / 1000;
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
		RBucket<String> rBucket = redissonClient.getBucket(key, codec);
		rBucket.expire(Duration.ofSeconds(timeout));
	}
	
	

	/**
	 * 获取Object，如无返空 
	 */
	@Override
	public Object getObject(String key) {
		RBucket<Object> rBucket = redissonClient.getBucket(key, codec);
		return rBucket.get();
	}

	@Override
	public <T> T getObject(String key, Class<T> classType) {
		// TODO 待实现
		return null;
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
			RBucket<Object> bucket = redissonClient.getBucket(key, codec);
			bucket.set(object);
		} else {
			RBatch batch = redissonClient.createBatch();
			RBucketAsync<Object> bucket = batch.getBucket(key, codec);
			bucket.setAsync(object);
			bucket.expireAsync(Duration.ofSeconds(timeout));
			batch.execute();
		}

	}

	/**
	 * 更新Object (过期时间不变) 
	 */
	@Override
	public void updateObject(String key, Object object) {
		long expire = getObjectTimeout(key);
		// -2 = 无此键 
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		this.setObject(key, object, expire);
	}

	/**
	 * 删除Object 
	 */
	@Override
	public void deleteObject(String key) {
		redissonClient.getBucket(key, codec).delete();
	}

	/**
	 * 获取Object的剩余存活时间 (单位: 秒)
	 */
	@Override
	public long getObjectTimeout(String key) {
		RBucket<String> rBucket = redissonClient.getBucket(key, codec);
		long timeout = rBucket.remainTimeToLive();
		return timeout < 0 ? timeout : timeout / 1000;
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
		RBucket<Object> rBucket = redissonClient.getBucket(key, codec);
		rBucket.expire(Duration.ofSeconds(timeout));
	}

	
	/**
	 * 搜索数据 
	 */
	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
		Stream<String> stream = redissonClient.getKeys().getKeysStreamByPattern(prefix + "*" + keyword + "*");
		List<String> list = stream.collect(Collectors.toList());
		return SaFoxUtil.searchList(list, start, size, sortType);
	}
}
