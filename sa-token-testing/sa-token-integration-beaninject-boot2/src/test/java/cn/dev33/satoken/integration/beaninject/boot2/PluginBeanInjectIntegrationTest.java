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
package cn.dev33.satoken.integration.beaninject.boot2;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.integration.beaninject.boot2.Boot2BeanInjectApplication;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaApiKeyDataLoader;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaApiKeyTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaOAuth2Dao;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaOAuth2DataConverter;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaOAuth2DataGenerate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaOAuth2DataLoader;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaOAuth2DataResolver;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaOAuth2GrantTypeHandler;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaOAuth2ScopeHandler;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaOAuth2ServerProcessor;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaOAuth2Template;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaSignTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaSsoClientTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaSsoServerTemplate;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.config.SaSignManyConfigWrapper;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 插件 *BeanInject 全量注入点集成测试：OAuth2 / SSO / Sign / ApiKey。
 */
@SpringBootTest(classes = Boot2BeanInjectApplication.class)
public class PluginBeanInjectIntegrationTest {

    @Autowired
    private SaOAuth2ServerConfig saOAuth2ServerConfig;
    @Autowired
    private CustomSaOAuth2Template customSaOAuth2Template;
    @Autowired
    private CustomSaOAuth2ServerProcessor customSaOAuth2ServerProcessor;
    @Autowired
    private CustomSaOAuth2DataLoader customSaOAuth2DataLoader;
    @Autowired
    private CustomSaOAuth2DataResolver customSaOAuth2DataResolver;
    @Autowired
    private CustomSaOAuth2DataConverter customSaOAuth2DataConverter;
    @Autowired
    private CustomSaOAuth2DataGenerate customSaOAuth2DataGenerate;
    @Autowired
    private CustomSaOAuth2Dao customSaOAuth2Dao;
    @Autowired
    private CustomSaOAuth2ScopeHandler customSaOAuth2ScopeHandler;
    @Autowired
    private CustomSaOAuth2GrantTypeHandler customSaOAuth2GrantTypeHandler;

    @Autowired
    private SaSsoServerConfig saSsoServerConfig;
    @Autowired
    private SaSsoClientConfig saSsoClientConfig;
    @Autowired
    private CustomSaSsoServerTemplate customSaSsoServerTemplate;
    @Autowired
    private CustomSaSsoClientTemplate customSaSsoClientTemplate;

    @Autowired
    private SaSignConfig saSignConfig;
    @Autowired
    private SaSignManyConfigWrapper saSignManyConfigWrapper;
    @Autowired
    private CustomSaSignTemplate customSaSignTemplate;

    @Autowired
    private SaApiKeyConfig saApiKeyConfig;
    @Autowired
    private CustomSaApiKeyTemplate customSaApiKeyTemplate;
    @Autowired
    private CustomSaApiKeyDataLoader customSaApiKeyDataLoader;

    /** OAuth2 配置应该注入到 SaOAuth2Manager */
    @Test
    public void saOAuth2ServerConfig_shouldInjectIntoManager() {
        Assertions.assertSame(saOAuth2ServerConfig, SaOAuth2Manager.getServerConfig());
    }

    /** OAuth2 模板应该注入到 SaOAuth2Manager */
    @Test
    public void saOAuth2Template_shouldInjectIntoManager() {
        Assertions.assertSame(customSaOAuth2Template, SaOAuth2Manager.getTemplate());
    }

    /** OAuth2 请求处理器应该注入到 SaOAuth2ServerProcessor.instance */
    @Test
    public void saOAuth2ServerProcessor_shouldInjectIntoGlobalInstance() {
        Assertions.assertSame(customSaOAuth2ServerProcessor, SaOAuth2ServerProcessor.instance);
    }

    /** OAuth2 数据加载器应该注入到 SaOAuth2Manager */
    @Test
    public void saOAuth2DataLoader_shouldInjectIntoManager() {
        Assertions.assertSame(customSaOAuth2DataLoader, SaOAuth2Manager.getDataLoader());
    }

    /** OAuth2 数据解析器应该注入到 SaOAuth2Manager */
    @Test
    public void saOAuth2DataResolver_shouldInjectIntoManager() {
        Assertions.assertSame(customSaOAuth2DataResolver, SaOAuth2Manager.getDataResolver());
    }

    /** OAuth2 数据转换器应该注入到 SaOAuth2Manager */
    @Test
    public void saOAuth2DataConverter_shouldInjectIntoManager() {
        Assertions.assertSame(customSaOAuth2DataConverter, SaOAuth2Manager.getDataConverter());
    }

    /** OAuth2 数据构建器应该注入到 SaOAuth2Manager */
    @Test
    public void saOAuth2DataGenerate_shouldInjectIntoManager() {
        Assertions.assertSame(customSaOAuth2DataGenerate, SaOAuth2Manager.getDataGenerate());
    }

    /** OAuth2 Dao 应该注入到 SaOAuth2Manager */
    @Test
    public void saOAuth2Dao_shouldInjectIntoManager() {
        Assertions.assertSame(customSaOAuth2Dao, SaOAuth2Manager.getDao());
    }

    /** OAuth2 Scope 处理器应该注册到 SaOAuth2Strategy */
    @Test
    public void saOAuth2ScopeHandler_shouldRegisterIntoStrategy() {
        Assertions.assertSame(customSaOAuth2ScopeHandler,
                SaOAuth2Strategy.instance.scopeHandlerMap.get(CustomSaOAuth2ScopeHandler.SCOPE));
    }

    /** OAuth2 GrantType 处理器应该注册到 SaOAuth2Strategy */
    @Test
    public void saOAuth2GrantTypeHandler_shouldRegisterIntoStrategy() {
        Assertions.assertSame(customSaOAuth2GrantTypeHandler,
                SaOAuth2Strategy.instance.grantTypeHandlerMap.get(CustomSaOAuth2GrantTypeHandler.GRANT_TYPE));
    }

    /** SSO Server 配置应该注入到 SaSsoManager */
    @Test
    public void saSsoServerConfig_shouldInjectIntoManager() {
        Assertions.assertSame(saSsoServerConfig, SaSsoManager.getServerConfig());
    }

    /** SSO Client 配置应该注入到 SaSsoManager */
    @Test
    public void saSsoClientConfig_shouldInjectIntoManager() {
        Assertions.assertSame(saSsoClientConfig, SaSsoManager.getClientConfig());
    }

    /** SSO Server 模板应该注入到 SaSsoServerProcessor */
    @Test
    public void saSsoServerTemplate_shouldInjectIntoProcessor() {
        Assertions.assertSame(customSaSsoServerTemplate, SaSsoServerProcessor.instance.ssoServerTemplate);
    }

    /** SSO Client 模板应该注入到 SaSsoClientProcessor */
    @Test
    public void saSsoClientTemplate_shouldInjectIntoProcessor() {
        Assertions.assertSame(customSaSsoClientTemplate, SaSsoClientProcessor.instance.ssoClientTemplate);
    }

    /** Sign 配置应该注入到 SaSignManager */
    @Test
    public void saSignConfig_shouldInjectIntoManager() {
        Assertions.assertSame(saSignConfig, SaSignManager.getConfig());
    }

    /** SignMany 配置应该注入到 SaSignManager */
    @Test
    public void saSignManyConfig_shouldInjectIntoManager() {
        Assertions.assertSame(saSignManyConfigWrapper.getSignMany(), SaSignManager.getSignMany());
    }

    /** Sign 模板应该注入到 SaSignManager */
    @Test
    public void saSignTemplate_shouldInjectIntoManager() {
        Assertions.assertSame(customSaSignTemplate, SaSignManager.getSaSignTemplate());
    }

    /** ApiKey 配置应该注入到 SaApiKeyManager */
    @Test
    public void saApiKeyConfig_shouldInjectIntoManager() {
        Assertions.assertSame(saApiKeyConfig, SaApiKeyManager.getConfig());
    }

    /** ApiKey 模板应该注入到 SaApiKeyManager */
    @Test
    public void saApiKeyTemplate_shouldInjectIntoManager() {
        Assertions.assertSame(customSaApiKeyTemplate, SaApiKeyManager.getSaApiKeyTemplate());
    }

    /** ApiKey 数据加载器应该注入到 SaApiKeyManager */
    @Test
    public void saApiKeyDataLoader_shouldInjectIntoManager() {
        Assertions.assertSame(customSaApiKeyDataLoader, SaApiKeyManager.getSaApiKeyDataLoader());
    }

}
