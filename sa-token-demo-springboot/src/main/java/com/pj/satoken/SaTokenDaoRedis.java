package com.pj.satoken;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;

/**
 * sa-token持久层的实现类 , 基于redis 
 */
//@Component	// 打开此注解，保证此类被springboot扫描，即可完成sa-token与redis的集成 
public class SaTokenDaoRedis implements SaTokenDao {


	// string专用
	@Autowired
	StringRedisTemplate stringRedisTemplate;	

	// SaSession专用 
	RedisTemplate<String, SaSession> redisTemplate;
	@Autowired
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setRedisTemplate(RedisTemplate redisTemplate) {
	    redisTemplate.setKeySerializer(new StringRedisSerializer());
	    redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		this.redisTemplate = redisTemplate;
	}
	
	
	// 根据key获取value ，如果没有，则返回空 
	@Override
	public String getValue(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	// 写入指定key-value键值对，并设定过期时间(单位：秒)
	@Override
	public void setValue(String key, String value, long timeout) {
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			stringRedisTemplate.opsForValue().set(key, value);
		} else {
			stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		}
	}

	// 更新指定key-value键值对 (过期时间取原来的值)
	@Override
	public void updateValue(String key, String value) {
		long expire = getTimeout(key);
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {	// -2 = 无此键 
			return;
		}
		this.setValue(key, value, expire);
	}
	
	// 删除一个指定的key 
	@Override
	public void deleteKey(String key) {
		stringRedisTemplate.delete(key);
	}

	// 获取指定key的剩余存活时间 (单位: 秒)
	@Override
	public long getTimeout(String key) {
		return stringRedisTemplate.getExpire(key);
	}
	
	
	
	// 根据指定key的session，如果没有，则返回空 
	@Override
	public SaSession getSession(String sessionId) {
		return redisTemplate.opsForValue().get(sessionId);
	}

	// 将指定session持久化 
	@Override
	public void saveSession(SaSession session, long timeout) {
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			redisTemplate.opsForValue().set(session.getId(), session);
		} else {
			redisTemplate.opsForValue().set(session.getId(), session, timeout, TimeUnit.SECONDS);
		}
	}

	// 更新指定session 
	@Override
	public void updateSession(SaSession session) {
		long expire = getSessionTimeout(session.getId());
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {	// -2 = 无此键 
			return;
		}
		this.saveSession(session, expire);
	}

	// 删除一个指定的session 
	@Override
	public void deleteSession(String sessionId) {
		redisTemplate.delete(sessionId);
	}

	// 获取指定SaSession的剩余存活时间 (单位: 秒)
	@Override
	public long getSessionTimeout(String sessionId) {
		return redisTemplate.getExpire(sessionId);
	}


	

	

	
	
	
	
	
}
