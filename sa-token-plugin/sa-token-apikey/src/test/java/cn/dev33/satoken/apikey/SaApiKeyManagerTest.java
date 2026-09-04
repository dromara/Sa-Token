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
package cn.dev33.satoken.apikey;

import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaApiKeyManager} 全局组件 lazy init 与读写测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaApiKeyManagerTest {

    private SaApiKeyConfig backupConfig;
    private SaApiKeyDataLoader backupLoader;
    private SaApiKeyTemplate backupTemplate;

    @BeforeEach
    public void backup() {
        backupConfig = SaApiKeyManager.getConfig();
        backupLoader = SaApiKeyManager.getSaApiKeyDataLoader();
        backupTemplate = SaApiKeyManager.getSaApiKeyTemplate();
    }

    @AfterEach
    public void restore() {
        SaApiKeyManager.setConfig(backupConfig);
        SaApiKeyManager.setSaApiKeyDataLoader(backupLoader);
        SaApiKeyManager.setSaApiKeyTemplate(backupTemplate);
    }

    /** getConfig 多次调用应该返回同一个实例 */
    @Test
    public void getConfig_lazyInit() {
        Assertions.assertSame(SaApiKeyManager.getConfig(), SaApiKeyManager.getConfig());
    }

    /** setConfig 写入后 getConfig 应该读到同一个实例 */
    @Test
    public void setConfig_readBack() {
        SaApiKeyConfig config = new SaApiKeyConfig().setPrefix("X-");
        SaApiKeyManager.setConfig(config);
        Assertions.assertSame(config, SaApiKeyManager.getConfig());
    }

    /** getSaApiKeyDataLoader 多次调用应该返回同一个实例 */
    @Test
    public void getSaApiKeyDataLoader_lazyInit() {
        Assertions.assertSame(SaApiKeyManager.getSaApiKeyDataLoader(), SaApiKeyManager.getSaApiKeyDataLoader());
    }

    /** setSaApiKeyDataLoader 写入后应该读到同一个实例 */
    @Test
    public void setSaApiKeyDataLoader_readBack() {
        SaApiKeyDataLoader loader = new cn.dev33.satoken.apikey.loader.SaApiKeyDataLoaderDefaultImpl();
        SaApiKeyManager.setSaApiKeyDataLoader(loader);
        Assertions.assertSame(loader, SaApiKeyManager.getSaApiKeyDataLoader());
    }

    /** getSaApiKeyTemplate 多次调用应该返回同一个实例 */
    @Test
    public void getSaApiKeyTemplate_lazyInit() {
        Assertions.assertSame(SaApiKeyManager.getSaApiKeyTemplate(), SaApiKeyManager.getSaApiKeyTemplate());
    }

    /** setSaApiKeyTemplate 写入后应该读到同一个实例 */
    @Test
    public void setSaApiKeyTemplate_readBack() {
        SaApiKeyTemplate template = new SaApiKeyTemplate("test-ns");
        SaApiKeyManager.setSaApiKeyTemplate(template);
        Assertions.assertSame(template, SaApiKeyManager.getSaApiKeyTemplate());
    }
}
