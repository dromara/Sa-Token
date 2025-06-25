package cn.dev33.satoken.config;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;

/**
 * Ehcache 配置类
 *
 * @author sanyang176
 */
public class EhcacheConfig {

    /**
     * 创建CacheManager
     *
     * @return CacheManager
     */
    public static CacheManager CreateCacheManager() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        return cacheManager;
    }

    /**
     * 关闭CacheManager
     *
     * @param cacheManager /
     */
    public static void CloseCacheManager(CacheManager cacheManager) {
        cacheManager.close();
    }
}
