package cn.dev33.satoken.dao.alone;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.dao.SaTokenDaoRedis;
import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;

/**
 * 为 SaTokenDao 单独设置Redis连接信息 
 * @author kong
 */
@Configuration
public class SaAloneRedisInject implements EnvironmentAware{

	/**
	 * 配置信息的前缀 
	 */
	public static final String ALONE_PREFIX = "sa-token.alone-redis";
	
	/**
	 * Sa-Token 持久层接口 
	 */
	@Autowired(required = false)
	public SaTokenDao saTokenDao;
	
	/**
	 * 开始注入 
	 */
	@Override
	public void setEnvironment(Environment environment) {
		
		// 如果为空或者默认实现，则不进行任何操作 
		if(saTokenDao == null || saTokenDao instanceof SaTokenDaoDefaultImpl) {
			return;
		}
		
		// ------------------- 开始注入 
		
		// 获取cfg对象 
		RedisProperties cfg = Binder.get(environment).bind(ALONE_PREFIX, RedisProperties.class).get();
		
		// 1. Redis配置 
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
		redisConfig.setHostName(cfg.getHost());
		redisConfig.setPort(cfg.getPort());
		redisConfig.setDatabase(cfg.getDatabase());
		redisConfig.setPassword(RedisPassword.of(cfg.getPassword())); 
		
		// 2. 连接池配置 
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		// pool配置 
		Lettuce lettuce = cfg.getLettuce();
		if(lettuce.getPool() != null) {
			RedisProperties.Pool pool = cfg.getLettuce().getPool();
			// 连接池最大连接数
			poolConfig.setMaxTotal(pool.getMaxActive());	
			// 连接池中的最大空闲连接 
			poolConfig.setMaxIdle(pool.getMaxIdle());   	
			// 连接池中的最小空闲连接
			poolConfig.setMinIdle(pool.getMinIdle());		
			// 连接池最大阻塞等待时间（使用负值表示没有限制）
			poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
		}
		LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
		// timeout 
		if(cfg.getTimeout() != null) {
			builder.commandTimeout(cfg.getTimeout());
		}
		// shutdownTimeout 
		if(lettuce.getShutdownTimeout() != null) {
			builder.shutdownTimeout(lettuce.getShutdownTimeout());
		}
		// 创建Factory对象 
		LettuceClientConfiguration clientConfig = builder.poolConfig(poolConfig).build();
		LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
		factory.afterPropertiesSet();
		
		// 3. 开始初始化 SaTokenDao 
		// 如果是SaTokenDaoRedis
		try {
			Class.forName("cn.dev33.satoken.dao.SaTokenDaoRedis");
			SaTokenDaoRedis dao = (SaTokenDaoRedis)saTokenDao;
			dao.isInit = false;
			dao.init(factory);
			return;
		} catch (ClassNotFoundException e) {
		}
		// 如果是SaTokenDaoRedisJackson
		try {
			Class.forName("cn.dev33.satoken.dao.SaTokenDaoRedisJackson");
			SaTokenDaoRedisJackson dao = (SaTokenDaoRedisJackson)saTokenDao;
			dao.isInit = false;
			dao.init(factory);
			return;
		} catch (ClassNotFoundException e) {
		}
	}
	
	/**
	 * 骗过编辑器，增加配置文件代码提示 
	 * @return 配置对象
	 */
	@ConfigurationProperties(prefix = ALONE_PREFIX)
	public RedisProperties getSaAloneRedisConfig() {
		return new RedisProperties();
	}
	
}
