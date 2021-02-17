package cn.dev33.satoken.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.util.SaTokenInsideUtil;

/**
 * sa-token持久层的实现类, 基于redis 
 * 
 * @author kong
 *
 */
@Component
public class SaTokenDaoRedis implements SaTokenDao {

	/**
	 * String专用 
	 */
	@Autowired
	public StringRedisTemplate stringRedisTemplate;	

	/**
	 * Objecy专用 
	 */
	public RedisTemplate<String, Object> objectRedisTemplate;
	@Autowired
	public void setObjectRedisTemplate(RedisConnectionFactory connectionFactory) {
		// 指定相应的序列化方案 
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer();
		// 构建RedisTemplate
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(keySerializer);
		template.setHashKeySerializer(keySerializer);
		template.setValueSerializer(valueSerializer);
		template.setHashValueSerializer(valueSerializer);
		template.afterPropertiesSet();
		if(this.objectRedisTemplate == null) {
			this.objectRedisTemplate = template;
		}
	}
	
	
	/**
	 * 根据key获取value，如果没有，则返回空 
	 */
	@Override
	public String get(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	/**
	 * 写入指定key-value键值对，并设定过期时间(单位：秒)
	 */
	@Override
	public void set(String key, String value, long timeout) {
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			stringRedisTemplate.opsForValue().set(key, value);
		} else {
			stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 修改指定key-value键值对 (过期时间不变) 
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
	 * 删除一个指定的key
	 */
	@Override
	public void delete(String key) {
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
	 * 根据key获取Object，如果没有，则返回空 
	 */
	@Override
	public Object getObject(String key) {
		return objectRedisTemplate.opsForValue().get(key);
	}

	/**
	 * 写入指定键值对，并设定过期时间 (单位: 秒)
	 */
	@Override
	public void setObject(String key, Object object, long timeout) {
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			objectRedisTemplate.opsForValue().set(key, object);
		} else {
			objectRedisTemplate.opsForValue().set(key, object, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 修改指定键值对 (过期时间不变)
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
	 * 删除一个指定的object 
	 */
	@Override
	public void deleteObject(String key) {
		objectRedisTemplate.delete(key);
	}

	/**
	 * 获取指定key的剩余存活时间 (单位: 秒)
	 */
	@Override
	public long getObjectTimeout(String key) {
		return objectRedisTemplate.getExpire(key);
	}

	/**
	 * 修改指定key的剩余存活时间 (单位: 秒)
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


	
	/**
	 * 搜索数据 
	 */
	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size) {
		Set<String> keys = stringRedisTemplate.keys(prefix + "*" + keyword + "*");
		List<String> list = new ArrayList<String>(keys);
		return SaTokenInsideUtil.searchList(list, start, size);
	}
	
	
}
