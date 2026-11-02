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
package cn.dev33.satoken.integration.beaninject.solon;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoaderDefaultImpl;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.http.SaHttpTemplateDefaultImpl;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicTemplate;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestUtil;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverterDefaultImpl;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerateDefaultImpl;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoaderDefaultImpl;
import cn.dev33.satoken.oauth2.data.resolver.SaOAuth2DataResolverDefaultImpl;
import cn.dev33.satoken.oauth2.granttype.handler.SaOAuth2GrantTypeHandlerInterface;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHook;
import cn.dev33.satoken.temp.SaTempTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonTest;

/**
 * 插件 SPI 启动后，自定义 Bean 应该经 *BeanInject 写进全局管理器
 */
@SolonTest(value = InjectApp.class, enableHttp = false)
public class InjectSolonTest {

	/** 核心组件应该注入到 SaManager / Util / 策略中心 */
	@Test
	public void coreBeans_shouldInjectIntoManagers() {
		Assertions.assertSame(Solon.context().getBean(SaLogForConsole.class), SaManager.getLog());
		Assertions.assertSame(Solon.context().getBean(SaTokenPluginHolder.class), SaTokenPluginHolder.instance);
		Assertions.assertNotNull(Solon.context().getBean(SaTokenDaoDefaultImpl.class));
		Assertions.assertNotNull(SaManager.getSaTokenDao());
		Assertions.assertSame(Solon.context().getBean(StpInterfaceDefaultImpl.class), SaManager.getStpInterface());
		Assertions.assertTrue(SaTokenEventCenter.getListenerList()
				.contains(Solon.context().getBean(SaTokenListenerForSimple.class)));
		Assertions.assertSame(Solon.context().getBean(SaCheckLoginHandler.class),
				SaAnnotationStrategy.instance.annotationHandlerMap.get(SaCheckLogin.class));
		Assertions.assertSame(Solon.context().getBean(SaTempTemplate.class), SaManager.getSaTempTemplate());
		Assertions.assertSame(Solon.context().getBean(SaSameTemplate.class), SaManager.getSaSameTemplate());
		Assertions.assertSame(Solon.context().getBean(SaHttpBasicTemplate.class), SaHttpBasicUtil.saHttpBasicTemplate);
		Assertions.assertSame(Solon.context().getBean(SaHttpDigestTemplate.class), SaHttpDigestUtil.saHttpDigestTemplate);
		Assertions.assertSame(Solon.context().getBean(SaJsonTemplateDefaultImpl.class), SaManager.getSaJsonTemplate());
		Assertions.assertSame(Solon.context().getBean(SaHttpTemplateDefaultImpl.class), SaManager.getSaHttpTemplate());
		Assertions.assertSame(Solon.context().getBean(SaSerializerTemplateForJson.class),
				SaManager.getSaSerializerTemplate());
		Assertions.assertSame(Solon.context().getBean(SaTotpTemplate.class), SaManager.getSaTotpTemplate());
		Assertions.assertSame(Solon.context().getBean(StpLogic.class), StpUtil.getStpLogic());
		Assertions.assertTrue(SaFirewallStrategy.instance.checkHooks
				.contains(Solon.context().getBean(SaFirewallCheckHook.class)));
		Assertions.assertSame(Solon.context().getBean(SaCorsHandleFunction.class), SaStrategy.instance.corsHandle);
		Assertions.assertNotNull(SaTokenPluginHolder.instance.getPlugin(InjectConfig.MarkerPlugin.class));
	}

	/** SSO / OAuth2 / ApiKey / Sign 自定义 Bean 应该注入到对应 Manager */
	@Test
	public void pluginBeans_shouldInjectIntoManagers() {
		Assertions.assertSame(Solon.context().getBean(SaSsoServerTemplate.class),
				SaSsoServerProcessor.instance.ssoServerTemplate);
		Assertions.assertSame(Solon.context().getBean(SaSsoClientTemplate.class),
				SaSsoClientProcessor.instance.ssoClientTemplate);

		Assertions.assertSame(Solon.context().getBean(SaOAuth2Template.class), SaOAuth2Manager.getTemplate());
		Assertions.assertSame(Solon.context().getBean(SaOAuth2ServerProcessor.class), SaOAuth2ServerProcessor.instance);
		Assertions.assertSame(Solon.context().getBean(SaOAuth2DataLoaderDefaultImpl.class),
				SaOAuth2Manager.getDataLoader());
		Assertions.assertSame(Solon.context().getBean(SaOAuth2DataResolverDefaultImpl.class),
				SaOAuth2Manager.getDataResolver());
		Assertions.assertSame(Solon.context().getBean(SaOAuth2DataConverterDefaultImpl.class),
				SaOAuth2Manager.getDataConverter());
		Assertions.assertSame(Solon.context().getBean(SaOAuth2DataGenerateDefaultImpl.class),
				SaOAuth2Manager.getDataGenerate());
		Assertions.assertSame(Solon.context().getBean(SaOAuth2Dao.class), SaOAuth2Manager.getDao());
		Assertions.assertSame(Solon.context().getBean(SaOAuth2ScopeHandlerInterface.class),
				SaOAuth2Strategy.instance.scopeHandlerMap.get("solon-inject-scope"));
		Assertions.assertSame(Solon.context().getBean(SaOAuth2GrantTypeHandlerInterface.class),
				SaOAuth2Strategy.instance.grantTypeHandlerMap.get("solon_inject_grant"));

		Assertions.assertSame(Solon.context().getBean(SaApiKeyTemplate.class), SaApiKeyManager.getSaApiKeyTemplate());
		Assertions.assertSame(Solon.context().getBean(SaApiKeyDataLoaderDefaultImpl.class),
				SaApiKeyManager.getSaApiKeyDataLoader());

		Assertions.assertSame(Solon.context().getBean(SaSignTemplate.class), SaSignManager.getSaSignTemplate());
	}

}
