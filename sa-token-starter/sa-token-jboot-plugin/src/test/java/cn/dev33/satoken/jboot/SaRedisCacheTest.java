/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.jboot;

import cn.dev33.satoken.jboot.testsupport.FakeJedisPool;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.support.redis.JbootRedisConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link SaRedisCache} 缓存读写测试。JedisPool 用假池子，不起真 Redis。
 */
public class SaRedisCacheTest {

    private JedisPool jedisPool;
    private Jedis jedis;
    private SaRedisCache cache;

    /** 每个用例开始前用假池子建一份 cache */
    @BeforeEach
    public void setUp() {
        jedis = mock(Jedis.class);
        jedisPool = new FakeJedisPool(jedis);
        cache = new SaRedisCache(jedisPool);
    }

    /** 每个用例结束后把 ThreadLocal 前缀清掉，池子关掉 */
    @AfterEach
    public void tearDown() {
        if (cache != null) {
            cache.removeCurrentCacheNamePrefix();
        }
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    /** 用配置对象建 cache 时，host 带端口、池参数都写上也不该当场连 Redis */
    @Test
    public void constructor_withConfig_shouldCreatePool() {
        JbootRedisConfig config = new JbootRedisConfig();
        config.setHost("127.0.0.1:16379");
        config.setTimeout(2000);
        config.setPassword("secret");
        config.setDatabase(1);
        config.setClientName("sa-test");
        config.setTestWhileIdle(true);
        config.setTestOnBorrow(true);
        config.setTestOnCreate(true);
        config.setTestOnReturn(true);
        config.setMinEvictableIdleTimeMillis(1000L);
        config.setTimeBetweenEvictionRunsMillis(2000L);
        config.setNumTestsPerEvictionRun(3);
        config.setMaxTotal(8);
        config.setMaxIdle(4);
        config.setMinIdle(1);
        config.setMaxWaitMillis(500);
        SaRedisCache built = new SaRedisCache(config);
        Assertions.assertNotNull(built);
        built.jedisPool.close();
    }

    /** 前缀有值时应该写进 ThreadLocal，空串则清掉 */
    @Test
    public void setCurrentCacheNamePrefix_blankShouldRemove() {
        Assertions.assertSame(cache, cache.setCurrentCacheNamePrefix("sa:"));
        Assertions.assertSame(cache, cache.setCurrentCacheNamePrefix(""));
        cache.removeCurrentCacheNamePrefix();
    }

    /** getConfig 目前固定返回 null */
    @Test
    public void getConfig_shouldBeNull() {
        Assertions.assertNull(cache.getConfig());
    }

    /** get 应该走 jedis.get，用完把连接还回去 */
    @Test
    public void get_shouldReadFromJedis() {
        when(jedis.get("k")).thenReturn("v");
        Assertions.assertEquals("v", cache.get("n", "k"));
        verify(jedis).close();
    }

    /** put 应该把值转成字符串再 set */
    @Test
    public void put_shouldSetString() {
        cache.put("n", "k", 123);
        verify(jedis).set("k", "123");
        verify(jedis).close();
    }

    /** 带 ttl 的 put 应该走 setex */
    @Test
    public void put_withLiveSeconds_shouldSetex() {
        cache.put("n", "k", "v", 9);
        verify(jedis).setex(eq("k"), eq(9L), eq("v"));
        verify(jedis).close();
    }

    /** remove 应该 del 掉这个 key */
    @Test
    public void remove_shouldDel() {
        cache.remove("n", "k");
        verify(jedis).del("k");
        verify(jedis).close();
    }

    /** removeAll 现在是空实现，调了也不该碰 Redis */
    @Test
    public void removeAll_shouldBeNoop() {
        cache.removeAll("n");
        org.mockito.Mockito.verifyNoInteractions(jedis);
    }

    /** 带 IDataLoader 的 get 现在直接返回 null，不会去加载 */
    @Test
    public void get_withDataLoader_shouldReturnNull() {
        IDataLoader loader = mock(IDataLoader.class);
        Assertions.assertNull(cache.get("n", "k", loader));
        Assertions.assertNull(cache.get("n", "k", loader, 10));
        verify(loader, org.mockito.Mockito.never()).load();
    }

    /** getTtl 应该把 jedis.ttl 转成 Integer */
    @Test
    public void getTtl_shouldReturnInt() {
        when(jedis.ttl("k")).thenReturn(12L);
        Assertions.assertEquals(Integer.valueOf(12), cache.getTtl("n", "k"));
    }

    /** setTtl 应该走 expire */
    @Test
    public void setTtl_shouldExpire() {
        cache.setTtl("n", "k", 8);
        verify(jedis).expire("k", 8L);
    }

    /** refresh 现在是空实现 */
    @Test
    public void refresh_shouldBeNoop() {
        cache.refresh("n", "k");
        cache.refresh("n");
        org.mockito.Mockito.verifyNoInteractions(jedis);
    }

    /** getNames 现在固定返回 null */
    @Test
    public void getNames_shouldBeNull() {
        Assertions.assertNull(cache.getNames());
    }

    /** scan 一轮就结束时，getKeys 应该把 key 去掉前 3 个字符 */
    @Test
    public void getKeys_completeInOneScan() {
        when(jedis.scan(eq("0"), any(ScanParams.class)))
                .thenReturn(new ScanResult<String>("0", Arrays.asList("satoken:a")));
        @SuppressWarnings("rawtypes")
        List keys = cache.getKeys("n");
        Assertions.assertEquals(Collections.singletonList("oken:a"), keys);
    }

    /** scan 结果是空列表时，getKeys 应该拿到空列表 */
    @Test
    public void getKeys_emptyScan() {
        when(jedis.scan(eq("0"), any(ScanParams.class)))
                .thenReturn(new ScanResult<String>("0", Collections.<String>emptyList()));
        Assertions.assertTrue(cache.getKeys("n").isEmpty());
    }

    /** scan 结果是 null 时，getKeys 也不该炸 */
    @Test
    public void getKeys_nullScanResults() {
        when(jedis.scan(eq("0"), any(ScanParams.class)))
                .thenReturn(new ScanResult<String>("0", null));
        Assertions.assertTrue(cache.getKeys("n").isEmpty());
    }

    /** 游标还没归零时应该继续 scan */
    @Test
    public void getKeys_shouldContinueUntilCursorZero() {
        when(jedis.scan(eq("0"), any(ScanParams.class)))
                .thenReturn(new ScanResult<String>("5", Arrays.asList("abcdef")));
        when(jedis.scan(eq("5"), any(ScanParams.class)))
                .thenReturn(new ScanResult<String>("0", Collections.<String>emptyList()));
        Assertions.assertEquals(Collections.singletonList("def"), cache.getKeys("n"));
    }

    /** 连接失败时，只塞了 JedisPool 的 cache 现在会 NPE（config 是 null） */
    @Test
    public void getJedis_connectionFail_withoutConfig_shouldNpe() {
        FakeJedisPool failing = new FakeJedisPool(new JedisConnectionException("down"));
        SaRedisCache failingCache = new SaRedisCache(failing);
        try {
            Assertions.assertThrows(NullPointerException.class, failingCache::getJedis);
        } finally {
            failing.close();
        }
    }

    /** 用配置对象建出来的 cache，连不上时应该包成 JbootIllegalConfigException */
    @Test
    public void getJedis_connectionFail_withConfig_shouldWrap() {
        JbootRedisConfig config = new JbootRedisConfig();
        config.setHost("127.0.0.1");
        config.setPort(6379);
        config.setTimeout(2000);
        config.setDatabase(0);
        SaRedisCache built = new SaRedisCache(config);
        JedisPool oldPool = built.jedisPool;
        FakeJedisPool failing = new FakeJedisPool(new JedisConnectionException("down"));
        built.jedisPool = failing;
        try {
            Assertions.assertThrows(JbootIllegalConfigException.class, built::getJedis);
        } finally {
            failing.close();
            oldPool.close();
        }
    }

    /** returnResource(null) 不该炸 */
    @Test
    public void returnResource_null_shouldIgnore() {
        cache.returnResource(null);
    }

    /** scan 应该把 pattern / count 交给 jedis，并包成 RedisScanResult */
    @Test
    public void scan_shouldWrapJedisResult() {
        when(jedis.scan(eq("0"), any(ScanParams.class)))
                .thenReturn(new ScanResult<String>("0", Arrays.asList("a")));
        Assertions.assertEquals("0", cache.scan("*", "0", 10).getCursor());
        Assertions.assertEquals(Collections.singletonList("a"), cache.scan("*", "0", 10).getResults());
    }
}
