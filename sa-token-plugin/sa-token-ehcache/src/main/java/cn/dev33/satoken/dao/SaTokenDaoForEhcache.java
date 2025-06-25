package cn.dev33.satoken.dao;

import cn.dev33.satoken.dao.auto.SaTokenDaoByStringFollowObject;
import cn.dev33.satoken.dao.timedcache.SaTimedCache;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.List;

public class SaTokenDaoForEhcache implements SaTokenDaoByStringFollowObject, SaTokenDao{

    private static final String DataCacheName = "DataCache";
    private static final String ExpireCacheName = "ExpireCache";

    public SaTimedCache timedCache = new SaTimedCache(
            new SaMapPackageForEhcache<>(Object.class,DataCacheName),
            new SaMapPackageForEhcache<>(Long.class,ExpireCacheName)
    );

    // ------------------------ Object 读写操作

    @Override
    public Object getObject(String key) {
        return timedCache.getObject(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> classType) {
        return (T) timedCache.getObject(key);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        timedCache.setObject(key, object, timeout);
    }

    @Override
    public void updateObject(String key, Object object) {
        timedCache.updateObject(key, object);
    }

    @Override
    public void deleteObject(String key) {
        timedCache.deleteObject(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return timedCache.getObjectTimeout(key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        timedCache.updateObjectTimeout(key, timeout);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        return SaFoxUtil.searchList(timedCache.keySet(), prefix, keyword, start, size, sortType);
    }

    // --------- 组件生命周期

    /**
     * 组件被安装时，开始刷新数据线程
     */
    @Override
    public void init() {
        timedCache.initRefreshThread();
    }

    /**
     * 组件被卸载时，结束定时任务，不再定时清理过期数据。 并且关闭Ehcache缓存防止内存泄漏
     */
    @Override
    public void destroy() {
        timedCache.endRefreshThread();
        if(timedCache.dataMap instanceof SaMapPackageForEhcache){
            ((SaMapPackageForEhcache<Object>)timedCache.dataMap).closeCacheManager();
        }
        if(timedCache.expireMap instanceof SaMapPackageForEhcache){
            ((SaMapPackageForEhcache<Long>)timedCache.expireMap).closeCacheManager();
        }
    }
}
