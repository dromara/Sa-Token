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

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.jboot.testsupport.FakeJedisPool;
import io.jboot.exception.JbootIllegalConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link SaTokenCacheDao} 字符串 / 对象读写测试。Redis 用 mock JedisPool，不起真库。
 */
public class SaTokenCacheDaoTest {

    private Jedis jedis;
    private FakeJedisPool jedisPool;
    private SaTokenCacheDao dao;
    private Map<String, String> strStore;
    private Map<String, Long> strTtl;
    private Map<String, byte[]> binStore;
    private Map<String, Long> binTtl;

    /** 每个用例开始前用默认构造建 dao，再换成 mock 缓存，别连真 Redis */
    @BeforeEach
    public void setUp() {
        jedis = mock(Jedis.class);
        jedisPool = new FakeJedisPool(jedis);

        dao = new SaTokenCacheDao();
        dao.saRedisCache.jedisPool.close();
        dao.saRedisCache = new SaRedisCache(jedisPool);

        strStore = new HashMap<String, String>();
        strTtl = new HashMap<String, Long>();
        binStore = new HashMap<String, byte[]>();
        binTtl = new HashMap<String, Long>();
        stubJedis();
    }

    /** 每个用例结束后把假池子关掉 */
    @AfterEach
    public void tearDown() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    /** 没配 jboot.redis.xxx.host 时，按名字取缓存应该直接炸 */
    @Test
    public void constructor_missingCacheName_shouldThrow() {
        Assertions.assertThrows(JbootIllegalConfigException.class, () -> new SaTokenCacheDao("no-such-cache"));
    }

    /** 配置里有 sa 这一组时，按名字 new 应该能建出来 */
    @Test
    public void constructor_namedCache_shouldCreate() {
        SaTokenCacheDao named = new SaTokenCacheDao("sa");
        Assertions.assertNotNull(named.saRedisCache);
        named.saRedisCache.jedisPool.close();
    }

    /** 读字符串应该走 jedis.get，用完要 close */
    @Test
    public void get_shouldReadFromJedis() {
        strStore.put("k", "v");
        Assertions.assertEquals("v", dao.get("k"));
        verify(jedis).close();
    }

    /** timeout 是 0 或已经过期标记时，set 应该直接返回，别去碰 Redis */
    @Test
    public void set_timeoutNotStore_shouldSkip() {
        dao.set("k", "v", 0);
        dao.set("k", "v", SaTokenDao.NOT_VALUE_EXPIRE);
        verify(jedis, never()).set(anyString(), anyString());
        verify(jedis, never()).setex(anyString(), anyLong(), anyString());
    }

    /** timeout=-1 时应该 set 成永久 */
    @Test
    public void set_neverExpire() {
        dao.set("k", "v", SaTokenDao.NEVER_EXPIRE);
        Assertions.assertEquals("v", strStore.get("k"));
        Assertions.assertEquals(Long.valueOf(SaTokenDao.NEVER_EXPIRE), strTtl.get("k"));
    }

    /** 带过期时间的 set 应该走 setex */
    @Test
    public void set_withTimeout() {
        dao.set("k", "v", 30);
        Assertions.assertEquals("v", strStore.get("k"));
        Assertions.assertEquals(Long.valueOf(30), strTtl.get("k"));
    }

    /** 键不存在时 update 应该啥也不做 */
    @Test
    public void update_missingKey_shouldSkip() {
        dao.update("missing", "v");
        Assertions.assertFalse(strStore.containsKey("missing"));
    }

    /** 键还在时 update 应该改值但沿用原来的 ttl */
    @Test
    public void update_existingKey_shouldKeepTtl() {
        dao.set("k", "old", 40);
        dao.update("k", "new");
        Assertions.assertEquals("new", strStore.get("k"));
        Assertions.assertEquals(Long.valueOf(40), strTtl.get("k"));
    }

    /** delete 应该把键删掉 */
    @Test
    public void delete_shouldRemoveKey() {
        strStore.put("k", "v");
        dao.delete("k");
        Assertions.assertFalse(strStore.containsKey("k"));
    }

    /** getTimeout 应该返回 jedis.ttl */
    @Test
    public void getTimeout_shouldReturnTtl() {
        strTtl.put("k", 12L);
        Assertions.assertEquals(12L, dao.getTimeout("k"));
    }

    /** 已经是永久的 key，再 updateTimeout 成永久时应该原样不动 */
    @Test
    public void updateTimeout_alreadyNeverExpire_shouldKeep() {
        dao.set("k", "v", SaTokenDao.NEVER_EXPIRE);
        dao.updateTimeout("k", SaTokenDao.NEVER_EXPIRE);
        Assertions.assertEquals("v", strStore.get("k"));
        Assertions.assertEquals(Long.valueOf(SaTokenDao.NEVER_EXPIRE), strTtl.get("k"));
    }

    /** 限时 key 改成永久时，应该再 set 一次 */
    @Test
    public void updateTimeout_toNeverExpire_shouldReset() {
        dao.set("k", "v", 20);
        dao.updateTimeout("k", SaTokenDao.NEVER_EXPIRE);
        Assertions.assertEquals("v", strStore.get("k"));
        Assertions.assertEquals(Long.valueOf(SaTokenDao.NEVER_EXPIRE), strTtl.get("k"));
    }

    /** 普通改 ttl 应该走 expire */
    @Test
    public void updateTimeout_expire() {
        strStore.put("k", "v");
        dao.updateTimeout("k", 8);
        Assertions.assertEquals(Long.valueOf(8), strTtl.get("k"));
    }

    /** 对象读写应该能 round-trip */
    @Test
    public void object_roundTrip() {
        Payload payload = new Payload("zhang");
        dao.setObject("obj", payload, 15);
        Object restored = dao.getObject("obj");
        Assertions.assertEquals(payload, restored);
        Assertions.assertEquals(payload, dao.getObject("obj", Payload.class));
        Assertions.assertEquals(15L, dao.getObjectTimeout("obj"));
    }

    /** 对象 timeout 非法时不应该写入 */
    @Test
    public void setObject_timeoutNotStore_shouldSkip() {
        dao.setObject("obj", new Payload("x"), 0);
        dao.setObject("obj", new Payload("x"), SaTokenDao.NOT_VALUE_EXPIRE);
        Assertions.assertTrue(binStore.isEmpty());
    }

    /** 对象永久写入应该走 set */
    @Test
    public void setObject_neverExpire() {
        dao.setObject("obj", new Payload("x"), SaTokenDao.NEVER_EXPIRE);
        Assertions.assertNotNull(dao.getObject("obj"));
    }

    /** 对象不存在时 updateObject 应该跳过 */
    @Test
    public void updateObject_missingKey_shouldSkip() {
        dao.updateObject("missing", new Payload("x"));
        Assertions.assertTrue(binStore.isEmpty());
    }

    /** 对象还在时 updateObject 应该改值 */
    @Test
    public void updateObject_existingKey() {
        dao.setObject("obj", new Payload("old"), 9);
        dao.updateObject("obj", new Payload("new"));
        Assertions.assertEquals(new Payload("new"), dao.getObject("obj"));
    }

    /** deleteObject 应该删掉二进制 key */
    @Test
    public void deleteObject_shouldRemove() {
        dao.setObject("obj", new Payload("x"), 9);
        dao.deleteObject("obj");
        Assertions.assertNull(dao.getObject("obj"));
    }

    /** 对象已经是永久时，再改成永久应该不动 */
    @Test
    public void updateObjectTimeout_alreadyNeverExpire() {
        dao.setObject("obj", new Payload("x"), SaTokenDao.NEVER_EXPIRE);
        dao.updateObjectTimeout("obj", SaTokenDao.NEVER_EXPIRE);
        Assertions.assertEquals(new Payload("x"), dao.getObject("obj"));
    }

    /** 限时对象改成永久时应该再写一遍 */
    @Test
    public void updateObjectTimeout_toNeverExpire() {
        dao.setObject("obj", new Payload("x"), 11);
        dao.updateObjectTimeout("obj", SaTokenDao.NEVER_EXPIRE);
        Assertions.assertEquals(new Payload("x"), dao.getObject("obj"));
    }

    /** 对象普通改 ttl 应该走 expire */
    @Test
    public void updateObjectTimeout_expire() {
        dao.setObject("obj", new Payload("x"), 11);
        dao.updateObjectTimeout("obj", 3);
        Assertions.assertEquals(3L, dao.getObjectTimeout("obj"));
    }

    /** 空字节读对象应该返回 null */
    @Test
    public void getObject_emptyBytes_shouldReturnNull() {
        binStore.put("empty", new byte[0]);
        Assertions.assertNull(dao.getObject("empty"));
    }

    /** searchData 应该用 jedis.keys 再按框架规则裁一段出来 */
    @Test
    public void searchData_shouldUseJedisKeys() {
        Set<String> keys = new HashSet<String>(Arrays.asList("satoken:login:1", "satoken:login:2"));
        when(jedis.keys("satoken:*login*")).thenReturn(keys);
        List<String> result = dao.searchData("satoken:", "login", 0, 10, false);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains("satoken:login:1"));
        verify(jedis).close();
    }

    /** Session 读写应该跟对象存储走同一套 */
    @Test
    public void session_roundTrip() {
        SaSession session = new SaSession("sess-1");
        dao.setSession(session, 20);
        Assertions.assertNotNull(dao.getSession("sess-1"));
        dao.updateSession(session);
        Assertions.assertTrue(dao.getSessionTimeout("sess-1") > 0 || dao.getSessionTimeout("sess-1") == SaTokenDao.NEVER_EXPIRE);
        dao.updateSessionTimeout("sess-1", 7);
        dao.deleteSession("sess-1");
        Assertions.assertNull(dao.getSession("sess-1"));
    }

    /** 给 Jedis mock 接上本地 Map，模拟一份最小 Redis */
    private void stubJedis() {
        when(jedis.get(anyString())).thenAnswer(invocation -> strStore.get(invocation.getArgument(0)));
        when(jedis.set(anyString(), anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            strStore.put(key, invocation.getArgument(1));
            strTtl.put(key, SaTokenDao.NEVER_EXPIRE);
            return "OK";
        });
        when(jedis.setex(anyString(), anyLong(), anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            strStore.put(key, invocation.getArgument(2));
            strTtl.put(key, (Long) invocation.getArgument(1));
            return "OK";
        });
        when(jedis.del(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            strStore.remove(key);
            strTtl.remove(key);
            return 1L;
        });
        when(jedis.ttl(anyString())).thenAnswer(invocation -> {
            Long ttl = strTtl.get(invocation.getArgument(0));
            return ttl == null ? SaTokenDao.NOT_VALUE_EXPIRE : ttl;
        });
        when(jedis.expire(anyString(), anyLong())).thenAnswer(invocation -> {
            strTtl.put(invocation.getArgument(0), invocation.getArgument(1));
            return 1L;
        });

        when(jedis.get(any(byte[].class))).thenAnswer(invocation -> binStore.get(binKey(invocation.getArgument(0))));
        when(jedis.set(any(byte[].class), any(byte[].class))).thenAnswer(invocation -> {
            String key = binKey(invocation.getArgument(0));
            binStore.put(key, invocation.getArgument(1));
            binTtl.put(key, SaTokenDao.NEVER_EXPIRE);
            return "OK";
        });
        when(jedis.setex(any(byte[].class), anyLong(), any(byte[].class))).thenAnswer(invocation -> {
            String key = binKey(invocation.getArgument(0));
            binStore.put(key, invocation.getArgument(2));
            binTtl.put(key, (Long) invocation.getArgument(1));
            return "OK";
        });
        when(jedis.del(any(byte[].class))).thenAnswer(invocation -> {
            String key = binKey(invocation.getArgument(0));
            binStore.remove(key);
            binTtl.remove(key);
            return 1L;
        });
        when(jedis.ttl(any(byte[].class))).thenAnswer(invocation -> {
            Long ttl = binTtl.get(binKey(invocation.getArgument(0)));
            return ttl == null ? SaTokenDao.NOT_VALUE_EXPIRE : ttl;
        });
        when(jedis.expire(any(byte[].class), anyLong())).thenAnswer(invocation -> {
            binTtl.put(binKey(invocation.getArgument(0)), invocation.getArgument(1));
            return 1L;
        });
    }

    /** 把二进制 key 转成 Map 用的字符串 */
    private String binKey(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /** 给对象读写用的小 payload */
    public static class Payload implements Serializable {
        private static final long serialVersionUID = 1L;
        public String name;

        public Payload(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Payload && name.equals(((Payload) obj).name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}
