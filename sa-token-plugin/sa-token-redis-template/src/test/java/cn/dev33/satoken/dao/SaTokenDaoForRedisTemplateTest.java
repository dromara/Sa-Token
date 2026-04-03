/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SaTokenDaoForRedisTemplate 单元测试
 * 
 * 需要 Redis 环境，使用 SCAN 命令测试 searchData 方法
 * 
 * @author click33
 * @since 1.45.0
 */
class SaTokenDaoForRedisTemplateTest {

    private SaTokenDaoForRedisTemplate dao;
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        // 连接本地 Redis（无密码）
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        
        redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.afterPropertiesSet();
        
        dao = new SaTokenDaoForRedisTemplate();
        dao.stringRedisTemplate = redisTemplate;
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        redisTemplate.delete(redisTemplate.keys("test_scan_*"));
    }

    @Test
    void testSearchData_Empty() {
        // 测试空结果
        List<String> result = dao.searchData("test_scan_", "notexist", 0, 10, true);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchData_SingleKey() {
        // 准备测试数据
        redisTemplate.opsForValue().set("test_scan_key1", "value1");
        
        // 测试搜索
        List<String> result = dao.searchData("test_scan_", "key", 0, 10, true);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("test_scan_key1"));
    }

    @Test
    void testSearchData_MultipleKeys() {
        // 准备测试数据 - 创建多个 key
        for (int i = 1; i <= 5; i++) {
            redisTemplate.opsForValue().set("test_scan_key" + i, "value" + i);
        }
        
        // 测试搜索
        List<String> result = dao.searchData("test_scan_", "key", 0, 10, true);
        
        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    void testSearchData_Pagination() {
        // 准备测试数据 - 创建多个 key
        for (int i = 1; i <= 10; i++) {
            redisTemplate.opsForValue().set("test_scan_page" + i, "value" + i);
        }
        
        // 测试分页 - 第一页
        List<String> page1 = dao.searchData("test_scan_", "page", 0, 5, true);
        assertEquals(5, page1.size());
        
        // 测试分页 - 第二页
        List<String> page2 = dao.searchData("test_scan_", "page", 5, 5, true);
        assertEquals(5, page2.size());
    }

    @Test
    void testSearchData_Pattern() {
        // 准备测试数据
        redisTemplate.opsForValue().set("test_scan_user_1001", "user1");
        redisTemplate.opsForValue().set("test_scan_user_1002", "user2");
        redisTemplate.opsForValue().set("test_scan_token_1001", "token1");
        
        // 测试模式匹配 - 只匹配 user
        List<String> result = dao.searchData("test_scan_", "user", 0, 10, true);
        assertEquals(2, result.size());
        
        // 测试模式匹配 - 只匹配 token
        List<String> result2 = dao.searchData("test_scan_", "token", 0, 10, true);
        assertEquals(1, result2.size());
    }

    @Test
    void testBasicOperations() {
        // 测试基本的 get/set 操作
        dao.set("test_scan_basic", "hello", 60);
        assertEquals("hello", dao.get("test_scan_basic"));
        
        // 测试 update
        dao.update("test_scan_basic", "world");
        assertEquals("world", dao.get("test_scan_basic"));
        
        // 测试 delete
        dao.delete("test_scan_basic");
        assertNull(dao.get("test_scan_basic"));
    }
}
