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
package cn.dev33.satoken.apikey.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaApiKeyConfig} 默认值、getter/setter、toString 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaApiKeyConfigTest {

    /** 默认 prefix 应该是 AK-、timeout 应该是 30 天、isRecordIndex 应该是 true */
    @Test
    public void defaults() {
        SaApiKeyConfig config = new SaApiKeyConfig();
        Assertions.assertEquals("AK-", config.getPrefix());
        Assertions.assertEquals(2592000L, config.getTimeout());
        Assertions.assertTrue(config.getIsRecordIndex());
    }

    /** setter 链式调用读写应该一致 */
    @Test
    public void setter_chainReadWrite() {
        SaApiKeyConfig config = new SaApiKeyConfig()
                .setPrefix("BK-").setTimeout(-1L).setIsRecordIndex(false);
        Assertions.assertEquals("BK-", config.getPrefix());
        Assertions.assertEquals(-1L, config.getTimeout());
        Assertions.assertFalse(config.getIsRecordIndex());
    }

    /** toString 应该包含三个字段 */
    @Test
    public void toString_containsFields() {
        String str = new SaApiKeyConfig().setPrefix("X-").toString();
        Assertions.assertTrue(str.contains("prefix='X-'"));
        Assertions.assertTrue(str.contains("timeout="));
        Assertions.assertTrue(str.contains("isRecordIndex="));
    }
}
