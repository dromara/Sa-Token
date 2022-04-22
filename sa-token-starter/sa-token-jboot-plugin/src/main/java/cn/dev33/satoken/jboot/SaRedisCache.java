package cn.dev33.satoken.jboot;

import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheConfig;
import io.jboot.core.spi.JbootSpi;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.support.redis.JbootRedisConfig;
import io.jboot.support.redis.RedisScanResult;
import io.jboot.utils.StrUtil;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ArrayList;
import java.util.List;

/**
 * sa 缓存处理
 */
@JbootSpi("sacache")
public class SaRedisCache implements JbootCache {
    protected JbootRedisConfig config;
    protected JedisPool jedisPool;
    private ThreadLocal<String> CACHE_NAME_PREFIX_TL = new ThreadLocal<>();

    public SaRedisCache(JbootRedisConfig config) {
        this.config = config;

        String host = config.getHost();
        Integer port = config.getPort();
        Integer timeout = config.getTimeout();
        String password = config.getPassword();
        Integer database = config.getDatabase();
        String clientName = config.getClientName();

        if (host.contains(":")) {
            port = Integer.valueOf(host.split(":")[1]);
        }


        JedisPoolConfig poolConfig = new JedisPoolConfig();

        if (StrUtil.isNotBlank(config.getTestWhileIdle())) {
            poolConfig.setTestWhileIdle(config.getTestWhileIdle());
        }

        if (StrUtil.isNotBlank(config.getTestOnBorrow())) {
            poolConfig.setTestOnBorrow(config.getTestOnBorrow());
        }

        if (StrUtil.isNotBlank(config.getTestOnCreate())) {
            poolConfig.setTestOnCreate(config.getTestOnCreate());
        }

        if (StrUtil.isNotBlank(config.getTestOnReturn())) {
            poolConfig.setTestOnReturn(config.getTestOnReturn());
        }

        if (StrUtil.isNotBlank(config.getMinEvictableIdleTimeMillis())) {
            poolConfig.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
        }

        if (StrUtil.isNotBlank(config.getTimeBetweenEvictionRunsMillis())) {
            poolConfig.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
        }

        if (StrUtil.isNotBlank(config.getNumTestsPerEvictionRun())) {
            poolConfig.setNumTestsPerEvictionRun(config.getNumTestsPerEvictionRun());
        }

        if (StrUtil.isNotBlank(config.getMaxTotal())) {
            poolConfig.setMaxTotal(config.getMaxTotal());
        }

        if (StrUtil.isNotBlank(config.getMaxIdle())) {
            poolConfig.setMaxIdle(config.getMaxIdle());
        }

        if (StrUtil.isNotBlank(config.getMinIdle())) {
            poolConfig.setMinIdle(config.getMinIdle());
        }

        if (StrUtil.isNotBlank(config.getMaxWaitMillis())) {
            poolConfig.setMaxWaitMillis(config.getMaxWaitMillis());
        }

        this.jedisPool = new JedisPool(poolConfig, host, port, timeout, timeout, password, database, clientName);
    }

    public SaRedisCache(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public JbootCache setCurrentCacheNamePrefix(String cacheNamePrefix) {
        if (StrUtil.isNotBlank(cacheNamePrefix)) {
            CACHE_NAME_PREFIX_TL.set(cacheNamePrefix);
        } else {
            CACHE_NAME_PREFIX_TL.remove();
        }
        return this;
    }

    @Override
    public void removeCurrentCacheNamePrefix() {
        CACHE_NAME_PREFIX_TL.remove();
    }

    @Override
    public JbootCacheConfig getConfig() {
        return null;
    }

    @Override
    public <T> T get(String cacheName, Object key) {
        Jedis jedis = getJedis();
        try {
            return (T) (jedis.get(key.toString()));
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key.toString(), value.toString());
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        Jedis jedis = getJedis();
        try {
            jedis.setex(key.toString(), Long.parseLong(liveSeconds + ""), value.toString());
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void remove(String cacheName, Object key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(key.toString());
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void removeAll(String cacheName) {

    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        return null;
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
        return null;
    }

    @Override
    public Integer getTtl(String cacheName, Object key) {
        Jedis jedis = getJedis();
        try {
            return jedis.ttl(key.toString()).intValue();
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void setTtl(String cacheName, Object key, int seconds) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(key.toString(), Long.parseLong(seconds + ""));
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void refresh(String cacheName, Object key) {

    }

    @Override
    public void refresh(String cacheName) {

    }

    @Override
    public List getNames() {
        return null;
    }

    @Override
    public List getKeys(String cacheName) {
        List<String> keys = new ArrayList<>();
        String cursor = "0";
        int scanCount = 1000;
        boolean continueState = true;
        do {
            RedisScanResult<String> redisScanResult = this.scan("*", cursor, scanCount);
            List<String> scanKeys = redisScanResult.getResults();
            cursor = redisScanResult.getCursor();

            if (scanKeys != null && scanKeys.size() > 0) {
                for (String key : scanKeys) {
                    keys.add(key.substring(3));
                }
            }

            if (redisScanResult.isCompleteIteration()) {
                continueState = false;
            }
        } while (continueState);

        return keys;
    }

    public Jedis getJedis() {
        try {
            return jedisPool.getResource();
        } catch (JedisConnectionException e) {
            throw new JbootIllegalConfigException("can not connect to redis host  " + config.getHost() + ":" + config.getPort() + " ," +
                    " cause : " + e.toString(), e);
        }
    }


    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public RedisScanResult<String> scan(String pattern, String cursor, int scanCount) {
        ScanParams params = new ScanParams();
        params.match(pattern).count(scanCount);
        try (Jedis jedis = getJedis()) {
            ScanResult<String> scanResult = jedis.scan(cursor, params);
            return new RedisScanResult<>(scanResult.getCursor(), scanResult.getResult());
        }
    }


}
