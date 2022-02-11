package cn.dev33.satoken.jboot;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaFoxUtil;
import io.jboot.Jboot;
import io.jboot.exception.JbootIllegalConfigException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SaTokenDaoRedis implements SaTokenDao {

    private SaJedisImpl redis;
    /**
     * 标记：是否已初始化成功
     */
    public boolean isInit;

    public SaTokenDaoRedis(){
        SaRedisConfig redisConfig = Jboot.config(SaRedisConfig.class);
        redisConfig.setSerializer("cn.dev33.satoken.jboot.SaJdkSerializer");
        //优先使用 jboot.cache.redis 的配置
        if (redisConfig.isConfigOk()) {
            redis = SaRedisManager.me().getRedis(redisConfig);
        }

        if (redis == null) {
            this.isInit = false;
            throw new JbootIllegalConfigException("can not get redis, please check your jboot.properties , please correct config jboot.cache.redis.host or jboot.redis.host ");
        }else{
            this.isInit = true;
        }
    }

    /**
     * 获取Value，如无返空
     * @param key
     * @return
     */
    @Override
    public String get(String key) {
        return redis.get(key);
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     * @param key
     * @param value
     * @param timeout
     */
    @Override
    public void set(String key, String value, long timeout) {
        if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
            return;
        }
        if(timeout == SaTokenDao.NEVER_EXPIRE) {
            redis.set(key, value);
        }else{
            redis.setex(key,value,timeout);
        }
    }

    /**
     * 修改指定key-value键值对 (过期时间不变)
     * @param key
     * @param value
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key,value,expire);
    }

    /**
     * 删除Value
     * @param key
     */
    @Override
    public void delete(String key) {
        redis.del(key);
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     * @param key
     * @return
     */
    @Override
    public long getTimeout(String key) {
        return redis.ttl(key);
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     * @param key
     * @param timeout
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        //判断是否想要设置为永久
        if(timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if(expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.set(key, this.get(key), timeout);
            }
            return;
        }
        redis.expire(key,timeout);
    }

    /**
     * 获取Object，如无返空
     * @param key
     * @return
     */
    @Override
    public Object getObject(String key) {
        return redis.getObject(key);
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     * @param key
     * @param object
     * @param timeout
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
            return;
        }
        if(timeout == SaTokenDao.NEVER_EXPIRE) {
            redis.setObject(key, object);
        }else{
            redis.setexObject(key,object,timeout);
        }
    }

    /**
     * 更新Object (过期时间不变)
     * @param key
     * @param object
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * 删除Object
     * @param key
     */
    @Override
    public void deleteObject(String key) {
        redis.deleteObject(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return redis.getObjectTimeout(key);
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     * @param key
     * @param timeout
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        //判断是否想要设置为永久
        if(timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if(expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.setObject(key, this.get(key), timeout);
            }
            return;
        }
        redis.expireObject(key,timeout);
    }

    /**
     * 搜索数据
     * @param prefix
     * @param keyword
     * @param start
     * @param size
     * @return
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size) {
        Set<String> keys = redis.keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<String>(keys);
        return SaFoxUtil.searchList(list, start, size);
    }
}
