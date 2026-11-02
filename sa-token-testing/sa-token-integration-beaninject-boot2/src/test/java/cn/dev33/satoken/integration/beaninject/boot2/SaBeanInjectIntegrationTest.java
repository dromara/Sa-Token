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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestUtil;
import cn.dev33.satoken.integration.beaninject.boot2.Boot2BeanInjectApplication;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomIntegrationSaTokenPlugin;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaCheckLoginHandler;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaCorsHandleFunction;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaFirewallCheckHook;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaHttpBasicTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaHttpDigestTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaHttpTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaJsonTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaLog;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaSameTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaSerializerTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaTempTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaTokenContext;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaTokenDao;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaTokenListener;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaTokenPluginHolder;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomSaTotpTemplate;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomStpInterface;
import cn.dev33.satoken.integration.beaninject.boot2.override.CustomStpLogic;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import cn.dev33.satoken.spring.pathmatch.SaPathMatcherHolder;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.PathMatcher;

/**
 * SaBeanInject 全量注入点集成测试：验证 v2v3v4-common 中每个 @Autowired 注入方法是否生效。
 */
@SpringBootTest(classes = Boot2BeanInjectApplication.class)
public class SaBeanInjectIntegrationTest {

    @Autowired
    private CustomSaLog customSaLog;
    @Autowired
    private SaTokenConfig saTokenConfig;
    @Autowired
    private CustomSaTokenPluginHolder customSaTokenPluginHolder;
    @Autowired
    private CustomSaTokenDao customSaTokenDao;
    @Autowired
    private CustomStpInterface customStpInterface;
    @Autowired
    private CustomSaTokenContext customSaTokenContext;
    @Autowired
    private CustomSaTokenListener customSaTokenListener;
    @Autowired
    private CustomSaCheckLoginHandler customSaCheckLoginHandler;
    @Autowired
    private CustomSaTempTemplate customSaTempTemplate;
    @Autowired
    private CustomSaSameTemplate customSaSameTemplate;
    @Autowired
    private CustomSaHttpBasicTemplate customSaHttpBasicTemplate;
    @Autowired
    private CustomSaHttpDigestTemplate customSaHttpDigestTemplate;
    @Autowired
    private CustomSaJsonTemplate customSaJsonTemplate;
    @Autowired
    private CustomSaHttpTemplate customSaHttpTemplate;
    @Autowired
    private CustomSaSerializerTemplate customSaSerializerTemplate;
    @Autowired
    private CustomSaTotpTemplate customSaTotpTemplate;
    @Autowired
    private CustomStpLogic customStpLogic;
    @Autowired
    private CustomSaFirewallCheckHook customSaFirewallCheckHook;
    @Autowired
    private CustomSaCorsHandleFunction customSaCorsHandleFunction;
    @Autowired
    private CustomIntegrationSaTokenPlugin customIntegrationSaTokenPlugin;
    @Autowired
    @Qualifier("mvcPathMatcher")
    private PathMatcher mvcPathMatcher;

    /** 构造器注入：SaLog 应该写入 SaManager */
    @Test
    public void saLog_shouldInjectIntoSaManager() {
        Assertions.assertSame(customSaLog, SaManager.getLog());
    }

    /** 构造器注入：SaTokenConfig 应该写入 SaManager */
    @Test
    public void saTokenConfig_shouldInjectIntoSaManager() {
        Assertions.assertSame(saTokenConfig, SaManager.getConfig());
    }

    /** 构造器注入：SaTokenPluginHolder 应该替换全局 instance */
    @Test
    public void saTokenPluginHolder_shouldInjectIntoGlobalInstance() {
        Assertions.assertSame(customSaTokenPluginHolder, SaTokenPluginHolder.instance);
    }

    /** SaTokenDao 应该注入到 SaManager */
    @Test
    public void saTokenDao_shouldInjectIntoSaManager() {
        Assertions.assertSame(customSaTokenDao, SaManager.getSaTokenDao());
    }

    /** StpInterface 应该注入到 SaManager */
    @Test
    public void stpInterface_shouldInjectIntoSaManager() {
        Assertions.assertSame(customStpInterface, SaManager.getStpInterface());
    }

    /** SaTokenContext 应该注入到 SaManager */
    @Test
    public void saTokenContext_shouldInjectIntoSaManager() {
        Assertions.assertSame(customSaTokenContext, SaManager.getSaTokenContext());
    }

    /** SaTokenListener 列表应该注册到 SaTokenEventCenter */
    @Test
    public void saTokenListener_shouldRegisterIntoEventCenter() {
        Assertions.assertTrue(SaTokenEventCenter.getListenerList().contains(customSaTokenListener));
    }

    /** SaAnnotationHandler 列表应该注册到 SaAnnotationStrategy */
    @Test
    public void saAnnotationHandler_shouldRegisterIntoAnnotationStrategy() {
        Assertions.assertSame(customSaCheckLoginHandler,
                SaAnnotationStrategy.instance.annotationHandlerMap.get(SaCheckLogin.class));
    }

    /** SaTempTemplate 应该注入到 SaManager */
    @Test
    public void saTempTemplate_shouldInjectIntoSaManager() {
        Assertions.assertSame(customSaTempTemplate, SaManager.getSaTempTemplate());
    }

    /** SaSameTemplate 应该注入到 SaManager */
    @Test
    public void saSameTemplate_shouldInjectIntoSaManager() {
        Assertions.assertSame(customSaSameTemplate, SaManager.getSaSameTemplate());
    }

    /** SaHttpBasicTemplate 应该注入到 SaHttpBasicUtil */
    @Test
    public void saHttpBasicTemplate_shouldInjectIntoUtil() {
        Assertions.assertSame(customSaHttpBasicTemplate, SaHttpBasicUtil.saHttpBasicTemplate);
    }

    /** SaHttpDigestTemplate 应该注入到 SaHttpDigestUtil */
    @Test
    public void saHttpDigestTemplate_shouldInjectIntoUtil() {
        Assertions.assertSame(customSaHttpDigestTemplate, SaHttpDigestUtil.saHttpDigestTemplate);
    }

    /** SaJsonTemplate 应该注入到 SaManager */
    @Test
    public void saJsonTemplate_shouldInjectIntoSaManager() {
        Assertions.assertSame(customSaJsonTemplate, SaManager.getSaJsonTemplate());
    }

    /** SaHttpTemplate 应该注入到 SaManager */
    @Test
    public void saHttpTemplate_shouldInjectIntoSaManager() {
        Assertions.assertSame(customSaHttpTemplate, SaManager.getSaHttpTemplate());
    }

    /** SaSerializerTemplate 应该注入到 SaManager */
    @Test
    public void saSerializerTemplate_shouldInjectIntoSaManager() {
        Assertions.assertSame(customSaSerializerTemplate, SaManager.getSaSerializerTemplate());
    }

    /** SaTotpTemplate 应该注入到 SaManager */
    @Test
    public void saTotpTemplate_shouldInjectIntoSaManager() {
        Assertions.assertSame(customSaTotpTemplate, SaManager.getSaTotpTemplate());
    }

    /** StpLogic 应该注入到 StpUtil */
    @Test
    public void stpLogic_shouldInjectIntoStpUtil() {
        Assertions.assertSame(customStpLogic, StpUtil.getStpLogic());
    }

    /** mvcPathMatcher 应该注入到 SaPathMatcherHolder */
    @Test
    public void pathMatcher_shouldInjectIntoSaPathMatcherHolder() {
        Assertions.assertSame(mvcPathMatcher, SaPathMatcherHolder.getPathMatcher());
    }

    /** SaFirewallCheckHook 列表应该注册到 SaFirewallStrategy */
    @Test
    public void saFirewallCheckHook_shouldRegisterIntoFirewallStrategy() {
        Assertions.assertTrue(SaFirewallStrategy.instance.checkHooks.contains(customSaFirewallCheckHook));
    }

    /** SaCorsHandleFunction 应该注入到 SaStrategy */
    @Test
    public void corsHandleFunction_shouldInjectIntoSaStrategy() {
        Assertions.assertSame(customSaCorsHandleFunction, SaStrategy.instance.corsHandle);
    }

    /** SaTokenPlugin 列表应该安装到 SaTokenPluginHolder */
    @Test
    public void saTokenPlugin_shouldInstallIntoPluginHolder() {
        Assertions.assertSame(customIntegrationSaTokenPlugin,
                SaTokenPluginHolder.instance.getPlugin(CustomIntegrationSaTokenPlugin.class));
    }

}
