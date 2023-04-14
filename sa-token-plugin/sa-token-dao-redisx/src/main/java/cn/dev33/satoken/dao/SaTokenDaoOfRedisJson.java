package cn.dev33.satoken.dao;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import org.noear.redisx.RedisClient;
import org.noear.redisx.plus.RedisBucket;
import org.noear.snack.ONode;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * SaTokenDao 的 redis 适配（基于json序列化，不能完全精准还原所有类型）
 *
 * @author noear
 * @since 1.6
 */
public class SaTokenDaoOfRedisJson implements SaTokenDao {
    private final RedisBucket redisBucket;

    public SaTokenDaoOfRedisJson(Properties props) {
        this(new RedisClient(props));
    }

    public SaTokenDaoOfRedisJson(RedisClient redisClient) {
        redisBucket = redisClient.getBucket();

        // 重写 SaSession 生成策略
        SaStrategy.me.createSession = (sessionId) -> new SaSessionForJson(sessionId);

    }

    @Override
    public SaSession getSession(String sessionId) {
        Object obj = getObject(sessionId);
        if (obj == null) {
            return null;
        }
        return ONode.deserialize(obj.toString(), SaSessionForJson.class);
    }


    /**
     * 获取Value，如无返空
     */
    @Override
    public String get(String key) {
        return redisBucket.get(key);
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout > 0 || timeout == SaTokenDao.NEVER_EXPIRE) {
            redisBucket.store(key, value, (int) timeout);
        }
    }

    /**
     * 修改指定key-value键值对 (过期时间不变)
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        this.set(key, value, expire);
    }

    /**
     * 删除Value
     */
    @Override
    public void delete(String key) {
        redisBucket.remove(key);
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getTimeout(String key) {
        return redisBucket.ttl(key);
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        if (redisBucket.exists(key)) {
            redisBucket.delay(key, (int) timeout);
        }
    }


    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        return get(key);
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout > 0 || timeout == SaTokenDao.NEVER_EXPIRE) {
            String value = ONode.serialize(object);
            set(key, value, timeout);
        }
    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        this.setObject(key, object, expire);
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        redisBucket.remove(key);
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getObjectTimeout(String key) {
        return redisBucket.ttl(key);
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        if (redisBucket.exists(key)) {
            redisBucket.delay(key, (int) timeout);
        }
    }


    /**
     * 搜索数据
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Set<String> keys = redisBucket.keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<String>(keys);
        return SaFoxUtil.searchList(list, start, size, sortType);
    }
}