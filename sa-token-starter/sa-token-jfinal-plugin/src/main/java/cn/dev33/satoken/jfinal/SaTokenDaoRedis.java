package cn.dev33.satoken.jfinal;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaFoxUtil;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.serializer.ISerializer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SaTokenDaoRedis implements SaTokenDao {

    protected Cache redis;
    protected ISerializer serializer;
    /**
     * 标记：是否已初始化成功
     */
    public boolean isInit;

    public SaTokenDaoRedis(String confName) {
        redis = Redis.use(confName);
        serializer = new SaJdkSerializer();
    }

    /**
     * 获取Value，如无返空
     */
    @Override
    public String get(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.get(key);
        } finally {
            close(jedis);
        }
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        Jedis jedis = getJedis();
        try {
            if (timeout == SaTokenDao.NEVER_EXPIRE) {
                jedis.set(key, value);
            } else {
                jedis.setex(key, timeout, value);
            }
        } finally {
            close(jedis);
        }
    }

    /**
     * 修改指定key-value键值对 (过期时间不变)
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    /**
     * 删除Value
     */
    @Override
    public void delete(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(key);
        } finally {
            close(jedis);
        }
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getTimeout(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.ttl(key);
        } finally {
            close(jedis);
        }
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        //判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.set(key, this.get(key), timeout);
            }
            return;
        }
        Jedis jedis = getJedis();
        try {
            jedis.expire(key, timeout);
        } finally {
            close(jedis);
        }
    }

    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        Jedis jedis = getJedis();
        try {
            return valueFromBytes(jedis.get(keyToBytes(key)));
        } finally {
            close(jedis);
        }
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        Jedis jedis = getJedis();
        try {
            if (timeout == SaTokenDao.NEVER_EXPIRE) {
                jedis.set(keyToBytes(key), valueToBytes(object));
            } else {
                jedis.setex(keyToBytes(key), timeout, valueToBytes(object));
            }
        } finally {
            close(jedis);
        }
    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(keyToBytes(key));
        } finally {
            close(jedis);
        }
    }

    @Override
    public long getObjectTimeout(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.ttl(keyToBytes(key));
        } finally {
            close(jedis);
        }
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        //判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getObjectTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        Jedis jedis = getJedis();
        try {
            jedis.expire(keyToBytes(key), timeout);
        } finally {
            close(jedis);
        }
    }

    /**
     * 搜索数据
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Set<String> keys = redis.keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<String>(keys);
        return SaFoxUtil.searchList(list, start, size, sortType);
    }

    public Jedis getJedis() {
        return redis.getJedis();
    }

    public void close(Jedis jedis) {
        if (jedis != null)
            jedis.close();
    }

    protected byte[] keyToBytes(Object key) {
        return key.toString().getBytes();
    }

    protected byte[] valueToBytes(Object value) {
        return serializer.valueToBytes(value);
    }

    protected Object valueFromBytes(byte[] bytes) {
        return serializer.valueFromBytes(bytes);
    }
}
