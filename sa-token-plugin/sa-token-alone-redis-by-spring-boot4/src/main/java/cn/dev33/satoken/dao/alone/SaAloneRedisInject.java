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
package cn.dev33.satoken.dao.alone;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import cn.dev33.satoken.exception.SaTokenException;
import jakarta.annotation.PostConstruct;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 为 SaToken 单独设置 Redis 连接信息，使权限缓存与业务缓存分离 (springboot4 版本专用) 
 *
 * <p>
 *     使用方式：在引入 sa-token redis 集成相关包的前提下，继续引入当前依赖 <br> <br>
 *     注意事项：目前本依赖仅对以下插件有 Redis 分离效果： <br>
 *     sa-token-redis-template  <br>
 *     sa-token-redis-template-jdk-serializer  <br>
 * </p>
 *
 *
 * @author click33
 * @since 1.45.0
 */
@Configuration
public class SaAloneRedisInject {

	/**
	 * 配置信息的前缀
	 */
	public static final String ALONE_PREFIX = "sa-token.alone-redis";

	/**
	 * Sa-Token 持久层接口
	 */
	private final SaTokenDao saTokenDao;
	private final Environment environment;

	public SaAloneRedisInject(SaTokenDao saTokenDao, Environment environment) {
		this.saTokenDao = saTokenDao;
		this.environment = environment;
	}

	/**
	 * 开始注入
	 */
	@PostConstruct
	public void init() {
		try {
			// 如果 saTokenDao 为空或者为默认实现，则不进行任何操作
			if(saTokenDao == null || saTokenDao instanceof SaTokenDaoDefaultImpl) {
				return;
			}

			// ------------------- 开始注入

			// 获取cfg对象，解析开发者配置的 sa-token.alone-redis 相关信息
			DataRedisProperties cfg = Binder.get(environment).bind(ALONE_PREFIX, DataRedisProperties.class).get();

			// 1. Redis配置
			RedisConfiguration redisAloneConfig;
			String pattern = environment.getProperty(ALONE_PREFIX + ".pattern", "single");
			if (pattern.equals("single")) {
				// 单体模式
				RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
				redisConfig.setHostName(cfg.getHost());
				redisConfig.setPort(cfg.getPort());
				redisConfig.setDatabase(cfg.getDatabase());
				redisConfig.setPassword(RedisPassword.of(cfg.getPassword()));
				redisConfig.setUsername(cfg.getUsername());
				redisAloneConfig = redisConfig;

			} else if (pattern.equals("cluster")){
				// 普通集群模式
				RedisClusterConfiguration redisClusterConfig = new RedisClusterConfiguration();
				redisClusterConfig.setUsername(cfg.getUsername());
				redisClusterConfig.setPassword(RedisPassword.of(cfg.getPassword()));

				DataRedisProperties.Cluster cluster = cfg.getCluster();
				if (cluster == null || cluster.getNodes() == null) {
					throw new SaTokenException("Alone-Redis 集群模式需要配置 cluster.nodes");
				}
				List<RedisNode> serverList = cluster.getNodes().stream().map(node -> {
					String[] ipAndPort = node.split(":");
					return new RedisNode(ipAndPort[0].trim(), Integer.parseInt(ipAndPort[1]));
				}).collect(Collectors.toList());
				redisClusterConfig.setClusterNodes(serverList);
				if (cluster.getMaxRedirects() != null) {
					redisClusterConfig.setMaxRedirects(cluster.getMaxRedirects());
				}
				redisAloneConfig = redisClusterConfig;
			} else if (pattern.equals("sentinel")) {
				// 哨兵集群模式
				RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
				redisSentinelConfiguration.setDatabase(cfg.getDatabase());
				redisSentinelConfiguration.setUsername(cfg.getUsername());
				redisSentinelConfiguration.setPassword(RedisPassword.of(cfg.getPassword()));

				DataRedisProperties.Sentinel sentinel = cfg.getSentinel();
				if (sentinel == null || sentinel.getNodes() == null) {
					throw new SaTokenException("Alone-Redis 哨兵模式需要配置 sentinel.nodes");
				}
				redisSentinelConfiguration.setMaster(sentinel.getMaster());
				redisSentinelConfiguration.setSentinelPassword(sentinel.getPassword());
				List<RedisNode> serverList = sentinel.getNodes().stream().map(node -> {
					String[] ipAndPort = node.split(":");
					return new RedisNode(ipAndPort[0].trim(), Integer.parseInt(ipAndPort[1]));
				}).collect(Collectors.toList());
				redisSentinelConfiguration.setSentinels(serverList);

				redisAloneConfig = redisSentinelConfiguration;
			} else if (pattern.equals("socket")) {
				// socket 连接单体 Redis
				RedisSocketConfiguration redisSocketConfiguration = new RedisSocketConfiguration();
				redisSocketConfiguration.setDatabase(cfg.getDatabase());
				redisSocketConfiguration.setUsername(cfg.getUsername());
				redisSocketConfiguration.setPassword(RedisPassword.of(cfg.getPassword()));
				String socket = environment.getProperty(ALONE_PREFIX + ".socket", "");
				redisSocketConfiguration.setSocket(socket);

				redisAloneConfig = redisSocketConfiguration;
			} else if (pattern.equals("aws")) {
				// AWS ElastiCache
				// AWS Redis 远程主机地址: String hoseName = "****.***.****.****.cache.amazonaws.com";
				String hostName = cfg.getHost();
				int port = cfg.getPort();
				RedisStaticMasterReplicaConfiguration redisStaticMasterReplicaConfiguration = new RedisStaticMasterReplicaConfiguration(hostName, port);
				redisStaticMasterReplicaConfiguration.setDatabase(cfg.getDatabase());
				redisStaticMasterReplicaConfiguration.setUsername(cfg.getUsername());
				redisStaticMasterReplicaConfiguration.setPassword(RedisPassword.of(cfg.getPassword()));

				redisAloneConfig = redisStaticMasterReplicaConfiguration;
			} else {
				// 模式无法识别
				throw new SaTokenException("SaToken 无法识别 Alone-Redis 配置的模式: " + pattern);
			}

			// 2. 连接池配置
			GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
			// pool配置
			DataRedisProperties.Lettuce lettuce = cfg.getLettuce();
			if(lettuce.getPool() != null) {
				DataRedisProperties.Pool pool = cfg.getLettuce().getPool();
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
			builder.shutdownTimeout(lettuce.getShutdownTimeout());
			// 创建Factory对象
			LettuceClientConfiguration clientConfig = builder.poolConfig(poolConfig).build();
			LettuceConnectionFactory factory = new LettuceConnectionFactory(redisAloneConfig, clientConfig);
			factory.afterPropertiesSet();

			// 3. 开始初始化 SaTokenDao ，此处需要依次判断开发者引入的是哪个 redis 库

			// 如果开发者引入的是：sa-token-redis-template-jdk-serializer 或 sa-token-redis-template
			try {
				Class.forName("cn.dev33.satoken.dao.SaTokenDaoForRedisTemplateUseJdkSerializer");
				SaTokenDaoForRedisTemplate dao = (SaTokenDaoForRedisTemplate) saTokenDao;
				dao.isInit = false;
				dao.init(factory);
				return;
			} catch (ClassNotFoundException ignored) {
			}
			try {
				Class.forName("cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate");
				SaTokenDaoForRedisTemplate dao = (SaTokenDaoForRedisTemplate) saTokenDao;
				dao.isInit = false;
				dao.init(factory);
				return;
			} catch (ClassNotFoundException ignored) {
			}

			// 至此，说明开发者一个 redis 插件也没引入，或者引入的 redis 插件不在 sa-token-alone-redis 的支持范围内
			throw new SaTokenException("未引入 sa-token-redis-xxx 相关插件，或引入的插件不在 Alone-Redis 支持范围内");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 骗过编辑器，增加配置文件代码提示
	 * @return 配置对象
	 */
	@ConfigurationProperties(prefix = ALONE_PREFIX)
	public DataRedisProperties getSaAloneRedisConfig() {
		return new DataRedisProperties();
	}

}
