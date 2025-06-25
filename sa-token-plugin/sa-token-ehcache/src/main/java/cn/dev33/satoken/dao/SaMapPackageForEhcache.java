package cn.dev33.satoken.dao;

import cn.dev33.satoken.config.EhcacheConfig;
import cn.dev33.satoken.dao.timedcache.SaMapPackage;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Map 包装类 (Ehcache 版)
 *
 * @author sanyang176
 */
public class SaMapPackageForEhcache<V> implements SaMapPackage<V> {

    private final Cache<String, V> cache;
    private static final CacheManager cacheManager = EhcacheConfig.CreateCacheManager();

    /**
     * 初始化缓存
     * @param clazzType / 创建cache需要的具体泛型类型
     * @param cacheName / 创建cache的名字
     */
    public SaMapPackageForEhcache(Class<V> clazzType,String cacheName) {
        this.cache = cacheManager.createCache(
                cacheName,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        String.class,
                        clazzType,
                        ResourcePoolsBuilder.heap(Integer.MAX_VALUE)
                ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(Long.MAX_VALUE)))
        );
    }

    /**
     * 获取缓存
     */
    @Override
    public Object getSource() {
        return cache;
    }

    /**
     * 读
     *
     * @param key /
     * @return /
     */
    @Override
    public V get(String key) {
        return cache.get(key);
    }

    /**
     * 写
     *
     * @param key /
     * @param value /
     */
    @Override
    public void put(String key, V value) {
        cache.put(key, value);
    }

    /**
     * 删
     * @param key /
     */
    @Override
    public void remove(String key) { cache.remove(key);  }

    /**
     * 所有 key
     */
    @Override
    public Set<String> keySet() {
        return StreamSupport.stream(cache.spliterator(), false)
                .map(Cache.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * CacheManager 关闭
     */
    public void closeCacheManager() {
        EhcacheConfig.CloseCacheManager(cacheManager);
    }
}
