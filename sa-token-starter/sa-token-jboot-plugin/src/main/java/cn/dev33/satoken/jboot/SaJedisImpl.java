package cn.dev33.satoken.jboot;

import cn.dev33.satoken.util.SaFoxUtil;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.components.serializer.JbootSerializer;
import io.jboot.components.serializer.JbootSerializerManager;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StrUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SaJedisImpl {

    private final JbootSerializer serializer;

    protected JedisPool jedisPool;
    @SuppressWarnings("unused")
	private static final Log LOG = Log.getLog(SaJedisImpl.class);
    private final SaRedisConfig config;
    public SaJedisImpl(SaRedisConfig config) {
        if (config == null || StrUtil.isBlank(config.getSerializer())) {
            serializer = Jboot.getSerializer();
        } else {
            serializer = JbootSerializerManager.me().getSerializer(config.getSerializer());
        }
        this.config = config;
        assert this.config != null;
        String host = this.config.getHost();
        Integer port = this.config.getPort();
        Integer timeout = this.config.getTimeout();
        String password = this.config.getPassword();
        Integer database = this.config.getSaDb()==null?this.config.getDatabase():this.config.getSaDb();
        String clientName = this.config.getClientName();

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
            poolConfig.setMinEvictableIdleTime(Duration.ofMillis(config.getMinEvictableIdleTimeMillis()));
        }

        if (StrUtil.isNotBlank(config.getTimeBetweenEvictionRunsMillis())) {
            poolConfig.setSoftMinEvictableIdleTime(Duration.ofMillis(config.getTimeBetweenEvictionRunsMillis()));
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

    /**
     * 获取Value，如无返空
     * @param key
     * @return
     */
    public String get(String key){
        Jedis jedis = getJedis();
        try{
            return jedis.get(key);
        }finally {
            returnResource(jedis);
        }
    }
    /**
     * 写入Value
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        Jedis jedis = getJedis();
        try{
            jedis.set(key, value);
        }finally {
            returnResource(jedis);
        }
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     * @param key
     * @param value
     * @param timeout
     */
    public void setex(String key, String value, long timeout){
        Jedis jedis = getJedis();
        try{
            jedis.setex(key,timeout,value);
        }finally {
            returnResource(jedis);
        }
    }

    /**
     * 删除Value
     * @param key
     */
    public void del(String key) {
        Jedis jedis = getJedis();
        try{
            jedis.del(key);
        }finally {
            returnResource(jedis);
        }
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     * @param key
     * @return
     */
    public long ttl(String key) {
        Jedis jedis = getJedis();
        try{
            return jedis.ttl(key);
        }finally {
            returnResource(jedis);
        }
    }

    public void expire(String key,long timeout){
        Jedis jedis = getJedis();
        try {
            jedis.expire(key,timeout);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 获取Object，如无返空
     * @param key
     * @return
     */
    public Object getObject(String key) {
        Jedis jedis = getJedis();
        try {
            return valueFromBytes(jedis.get(keyToBytes(key)));
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 写入Object
     * @param key
     * @param value
     */
    public void setObject(Object key, Object value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(keyToBytes(key), valueToBytes(value));
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     * @param key
     * @param object
     * @param timeout
     */
    public void setexObject(String key, Object object,long timeout) {
        Jedis jedis = getJedis();
        try {
            jedis.setex(keyToBytes(key),timeout, valueToBytes(object));
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 删除Object
     * @param key
     */
    public void deleteObject(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(keyToBytes(key));
        } finally {
            returnResource(jedis);
        }
    }

    public long getObjectTimeout(String key) {
        Jedis jedis = getJedis();
        try {
            return getJedis().ttl(keyToBytes(key));
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     * @param key
     * @param timeout
     */
    public void expireObject(String key, long timeout) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(keyToBytes(key), timeout);
        }
        finally {
            returnResource(jedis);
        }
    }

    /**
     * 查找所有符合给定模式 pattern 的 key 。
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     * 特殊符号用 \ 隔开
     */
    public Set<String> keys(String pattern) {
        Jedis jedis = getJedis();
        try {
            return jedis.keys(pattern);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。
     * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
     */
    public Long hset(String key, String field, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hset(key, field, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。
     * 此命令会覆盖哈希表中已存在的域。
     * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
     */
    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = getJedis();
        try {
            return jedis.hmset(key,hash);
        } finally {
            returnResource(jedis);
        }
    }
    /**
     * 返回哈希表 key 中给定域 field 的值。
     * @param key
     * @param field
     * @return
     */
    public String hget(String key,String field){
        Jedis jedis = getJedis();
        try {
            return jedis.hget(key,field);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。
     * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     */
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = getJedis();
        try {
            return jedis.hmget(key,fields);
        } finally {
            returnResource(jedis);
        }
    }
    /**
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     * @param key
     * @param fields
     * @return
     */
    public Long hdel(String key, String... fields) {
        Jedis jedis = getJedis();
        try {
            return jedis.hdel(key,fields);
        } finally {
            returnResource(jedis);
        }
    }
    /**
     * 查看哈希表 key 中，给定域 field 是否存在。
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(String key, String field){
        Jedis jedis = getJedis();
        try {
            return jedis.hexists(key,field);
        } finally {
            returnResource(jedis);
        }
    }
    /**
     * 返回哈希表 key 中，所有的域和值。
     * 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
     */
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hgetAll(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 返回哈希表 key 中所有域的值。
     * @param key
     * @return
     */
    public List<String> hvals(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hvals(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 返回哈希表 key 中的所有域。
     * @param key
     * @return
     */
    public Set<String> hkeys(String key){
        Jedis jedis = getJedis();
        try {
            return jedis.hkeys(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 返回哈希表 key 中域的数量。
     * @param key
     * @return
     */
    public Long hlen(String key){
        Jedis jedis = getJedis();
        try {
            return jedis.hlen(key);
        } finally {
            returnResource(jedis);
        }
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

    public byte[] keyToBytes(Object key) {
        return key.toString().getBytes();
    }

    public String bytesToKey(byte[] bytes) {
        return new String(bytes);
    }

    public byte[] valueToBytes(Object value) {
        return serializer.serialize(value);
    }

    public Object valueFromBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return serializer.deserialize(bytes);
    }

    /**
     * 搜索数据
     * @param prefix
     * @param keyword
     * @param start
     * @param size
     * @return
     */
    public List<String> searchData(String prefix, String keyword, int start, int size) {
        Set<String> keys = getJedis().keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<String>(keys);
        return SaFoxUtil.searchList(list, start, size);
    }

}
