package cn.dev33.satoken.dao;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.dev33.satoken.session.SaSession;

/**
 * sa-token持久层的实现类, 基于redis (to jackson)
 */
@Component
public class SaTokenDaoRedisJackson implements SaTokenDao {
	
	/**
	 * ObjectMapper对象 (以public作用于暴露出此对象，方便开发者二次更改配置)
	 */
	public ObjectMapper objectMapper;
	
	/**
	 * string专用
	 */
	@Autowired
	public StringRedisTemplate stringRedisTemplate;	

	/**
	 * SaSession专用 
	 */
	public RedisTemplate<String, SaSession> sessionRedisTemplate;
	@Autowired
	public void setSessionRedisTemplate(RedisConnectionFactory connectionFactory) {
		// 指定相应的序列化方案 
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
		// 通过反射获取Mapper对象, 配置[忽略未知字段], 增强兼容性
		try {
			Field field = GenericJackson2JsonRedisSerializer.class.getDeclaredField("mapper");
			field.setAccessible(true);
			ObjectMapper objectMapper = (ObjectMapper) field.get(valueSerializer);
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			this.objectMapper = objectMapper;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		// 构建RedisTemplate
		RedisTemplate<String, SaSession> template = new RedisTemplate<String, SaSession>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(keySerializer);
		template.setHashKeySerializer(keySerializer);
		template.setValueSerializer(valueSerializer);
		template.setHashValueSerializer(valueSerializer);
		template.afterPropertiesSet();
		if(this.sessionRedisTemplate == null) {
			this.sessionRedisTemplate = template;
		}
	}
	
	
	/**
	 * 根据key获取value，如果没有，则返回空
	 */
	@Override
	public String getValue(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	/**
	 * 写入指定key-value键值对，并设定过期时间(单位：秒)
	 */
	@Override
	public void setValue(String key, String value, long timeout) {
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			stringRedisTemplate.opsForValue().set(key, value);
		} else {
			stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 修改指定key-value键值对 (过期时间取原来的值) 
	 */
	@Override
	public void updateValue(String key, String value) {
		long expire = getTimeout(key);
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {	// -2 = 无此键 
			return;
		}
		this.setValue(key, value, expire);
	}
	
	/**
	 * 删除一个指定的key
	 */
	@Override
	public void deleteKey(String key) {
		stringRedisTemplate.delete(key);
	}

	/**
	 * 根据key获取value，如果没有，则返回空 
	 */
	@Override
	public long getTimeout(String key) {
		return stringRedisTemplate.getExpire(key);
	}

	/**
	 * 修改指定key的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public void updateTimeout(String key, long timeout) {
		stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}
	
	
	
	/**
	 * 根据指定key的Session，如果没有，则返回空 
	 */
	@Override
	public SaSession getSession(String sessionId) {
		return sessionRedisTemplate.opsForValue().get(sessionId);
	}

	/**
	 * 将指定Session持久化 
	 */
	@Override
	public void saveSession(SaSession session, long timeout) {
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			sessionRedisTemplate.opsForValue().set(session.getId(), session);
		} else {
			sessionRedisTemplate.opsForValue().set(session.getId(), session, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 更新指定session 
	 */
	@Override
	public void updateSession(SaSession session) {
		long expire = getSessionTimeout(session.getId());
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {	// -2 = 无此键 
			return;
		}
		this.saveSession(session, expire);
	}

	/**
	 * 删除一个指定的session 
	 */
	@Override
	public void deleteSession(String sessionId) {
		sessionRedisTemplate.delete(sessionId);
	}

	/**
	 * 获取指定SaSession的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public long getSessionTimeout(String sessionId) {
		return sessionRedisTemplate.getExpire(sessionId);
	}

	/**
	 * 修改指定SaSession的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public void updateSessionTimeout(String sessionId, long timeout) {
		sessionRedisTemplate.expire(sessionId, timeout, TimeUnit.SECONDS);
	}
	
}
