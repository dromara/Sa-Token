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
package cn.dev33.satoken.apikey.loader;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaApiKeyDataLoaderDefaultImpl} 默认行为测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaApiKeyDataLoaderDefaultImplTest {

    private SaApiKeyConfig backupConfig;

    @BeforeEach
    public void backup() {
        backupConfig = SaApiKeyManager.getConfig();
    }

    @AfterEach
    public void restore() {
        SaApiKeyManager.setConfig(backupConfig);
    }

    /** getIsRecordIndex 应该跟随全局 SaApiKeyConfig */
    @Test
    public void getIsRecordIndex_followsGlobalConfig() {
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setIsRecordIndex(true));
        Assertions.assertTrue(new SaApiKeyDataLoaderDefaultImpl().getIsRecordIndex());

        SaApiKeyManager.setConfig(new SaApiKeyConfig().setIsRecordIndex(false));
        Assertions.assertFalse(new SaApiKeyDataLoaderDefaultImpl().getIsRecordIndex());
    }

    /** getApiKeyModelFromDatabase 默认应该返回 null */
    @Test
    public void getApiKeyModelFromDatabase_returnsNull() {
        Assertions.assertNull(new SaApiKeyDataLoaderDefaultImpl().getApiKeyModelFromDatabase("apikey", "AK-xxx"));
    }
}
