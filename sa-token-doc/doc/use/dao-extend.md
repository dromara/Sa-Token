# 持久层扩展
--- 
- 每次重启项目就得重新登录一遍，我想把登录数据都放在`redis`里，这样重启项目也不用重新登录，行不行？**行！**
- 你需要做的就是重写`sa-token`的dao层实现方式，参考以下方案：



### 1. 首先在pom.xml引入依赖
``` xml 
	<!-- SpringBoot整合redis -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-redis</artifactId>
		<version>RELEASE</version>
	</dependency>
```


### 2. 实现SaTokenDao接口 
新建文件`SaTokenDaoRedis.java`，实现接口`SaTokenDao`, 并加上注解`@Component`，保证此类被springboot扫描到
- 代码参考：
	
```java
package com.pj.satoken;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;

/**
 * sa-token持久层的实现类 , 基于redis 
 */
@Component	// 打开此注解，保证此类被springboot扫描，即可完成sa-token与redis的集成 
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

```


- 可参考代码：[码云：SaTokenDaoRedis.java](https://gitee.com/sz6/sa-token/blob/master/sa-token-demo-springboot/src/main/java/com/pj/satoken/SaTokenDaoRedis.java)

