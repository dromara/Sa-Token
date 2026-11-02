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
package cn.dev33.satoken.dao;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaMapPackageForCaffeine} Map 包装类测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaMapPackageForCaffeineTest {

    /** put + get 应该能正常读写 */
    @Test
    public void put_get_roundtrip() {
        SaMapPackageForCaffeine<String> map = new SaMapPackageForCaffeine<>();
        map.put("k1", "v1");
        Assertions.assertEquals("v1", map.get("k1"));
    }

    /** remove 之后应该取不到值 */
    @Test
    public void remove() {
        SaMapPackageForCaffeine<String> map = new SaMapPackageForCaffeine<>();
        map.put("k1", "v1");
        map.remove("k1");
        Assertions.assertNull(map.get("k1"));
    }

    /** keySet 应该包含所有已写入的 key */
    @Test
    public void keySet() {
        SaMapPackageForCaffeine<String> map = new SaMapPackageForCaffeine<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        Assertions.assertTrue(map.keySet().contains("k1"));
        Assertions.assertTrue(map.keySet().contains("k2"));
    }

    /** getSource 应该返回底层 Caffeine Cache 对象 */
    @Test
    public void getSource() {
        SaMapPackageForCaffeine<String> map = new SaMapPackageForCaffeine<>();
        Assertions.assertTrue(map.getSource() instanceof Cache);
        Assertions.assertSame(map.cache, map.getSource());
    }

}
