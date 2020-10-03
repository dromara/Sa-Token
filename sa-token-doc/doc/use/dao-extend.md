# 持久层扩展
--- 
- 每次重启项目就得重新登录一遍，我想把登录数据都放在`redis`里，这样重启项目也不用重新登录，行不行？
- 行！
- 你需要做的就是重写`sa-token`的dao层实现方式，参考以下方案：


## 具体代码
- 新建文件`SaTokenDaoRedis.java`，实现接口`SaTokenDao`, 并加上注解`@Component`，保证此类被springboot扫描到
- 代码参考：
	
```java

package com.pj.satoken;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;

/**
 * sa-token持久层的实现类 , 基于redis 
 */
@Component	// 保证此类被springboot扫描，即可完成sa-token与redis的集成 
public class SaTokenDaoRedis implements SaTokenDao {


	// string专用
	@Autowired
	StringRedisTemplate stringRedisTemplate;	

	// SaSession专用 
	RedisTemplate<String, SaSession> redisTemplate;
	@Autowired
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		RedisSerializer stringSerializer = new StringRedisSerializer();
	    redisTemplate.setKeySerializer(stringSerializer);
	    JdkSerializationRedisSerializer jrSerializer = new JdkSerializationRedisSerializer();
	    redisTemplate.setValueSerializer(jrSerializer);
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
		stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
	}

	// 删除一个指定的key 
	@Override
	public void delKey(String key) {
		stringRedisTemplate.delete(key);
	}

	
	// 根据指定key的session，如果没有，则返回空 
	@Override
	public SaSession getSaSession(String sessionId) {
		return redisTemplate.opsForValue().get(sessionId);
	}

	// 将指定session持久化 
	@Override
	public void saveSaSession(SaSession session, long timeout) {
		redisTemplate.opsForValue().set(session.getId(), session, timeout, TimeUnit.SECONDS);
	}

	// 更新指定session 
	@Override
	public void updateSaSession(SaSession session) {
		long expire = redisTemplate.getExpire(session.getId());
		if(expire == -2) {	// -2 = 无此键 
			return;
		}
		redisTemplate.opsForValue().set(session.getId(), session, expire, TimeUnit.SECONDS);
	}

	// 删除一个指定的session 
	@Override
	public void delSaSession(String sessionId) {
		redisTemplate.delete(sessionId);
	}

	
	
}


```


- 可参考代码：[码云：SaTokenDaoRedis.java](https://gitee.com/sz6/sa-token/blob/master/sa-token-demo-springboot/src/main/java/com/pj/satoken/SaTokenDaoRedis.java)

