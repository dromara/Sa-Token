package cn.dev33.satoken.jboot;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaFoxUtil;
import io.jboot.utils.CacheUtil;
import io.jboot.utils.StrUtil;

import java.util.List;

/**
 * 使用jBoot的缓存方法存取Token数据
 */
public class SaTokenCacheDao implements SaTokenDao {

    private final String cacheName;

    /**
     * 调用的Cache名称
     *
     * @param cacheName 使用的缓存配置名，默认为 default
     */
    public SaTokenCacheDao(String cacheName) {
        if (StrUtil.isBlank(cacheName)) {
            cacheName = "default";
        }
        this.cacheName = cacheName;
    }

    @Override
    public String get(String s) {
        return CacheUtil.use(cacheName).get("SA", s);
    }

    @Override
    public void set(String s, String s1, long l) {
        CacheUtil.use(cacheName).put("SA", s, s1, Integer.parseInt(l + ""));
    }

    @Override
    public void update(String s, String s1) {
        CacheUtil.use(cacheName).put("SA", s, s1);
    }

    @Override
    public void delete(String s) {
        CacheUtil.use(cacheName).remove("SA", s);
    }

    @Override
    public long getTimeout(String s) {
        return CacheUtil.use(cacheName).getTtl("SA", s);
    }

    @Override
    public void updateTimeout(String s, long l) {
        CacheUtil.use(cacheName).setTtl("SA", s, Integer.parseInt(l + ""));
    }

    @Override
    public Object getObject(String s) {
        return CacheUtil.use(cacheName).get("SA", s);
    }

    @Override
    public void setObject(String s, Object o, long l) {
        CacheUtil.use(cacheName).put("SA", s, o, Integer.parseInt(l + ""));
    }

    @Override
    public void updateObject(String s, Object o) {
        CacheUtil.use(cacheName).put("SA", s, o);
    }

    @Override
    public void deleteObject(String s) {
        CacheUtil.use(cacheName).remove("SA", s);
    }

    @Override
    public long getObjectTimeout(String s) {
        return CacheUtil.use(cacheName).getTtl("SA", s);
    }

    @Override
    public void updateObjectTimeout(String s, long l) {
        CacheUtil.use(cacheName).setTtl("SA", s, Integer.parseInt(l + ""));
    }

    @Override
    public SaSession getSession(String sessionId) {
        return SaTokenDao.super.getSession(sessionId);
    }

    @Override
    public void setSession(SaSession session, long timeout) {
        SaTokenDao.super.setSession(session, timeout);
    }

    @Override
    public void updateSession(SaSession session) {
        SaTokenDao.super.updateSession(session);
    }

    @Override
    public void deleteSession(String sessionId) {
        SaTokenDao.super.deleteSession(sessionId);
    }

    @Override
    public long getSessionTimeout(String sessionId) {
        return SaTokenDao.super.getSessionTimeout(sessionId);
    }

    @Override
    public void updateSessionTimeout(String sessionId, long timeout) {
        SaTokenDao.super.updateSessionTimeout(sessionId, timeout);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size) {
        List<String> list = CacheUtil.use(cacheName).getKeys(prefix + "*" + keyword + "*");
        return SaFoxUtil.searchList(list, start, size);
    }
}
