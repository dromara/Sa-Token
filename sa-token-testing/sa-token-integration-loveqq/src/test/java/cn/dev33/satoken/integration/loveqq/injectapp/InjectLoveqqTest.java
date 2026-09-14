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
package cn.dev33.satoken.integration.loveqq.injectapp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoaderDefaultImpl;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import cn.dev33.satoken.application.ApplicationInfo;
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
import cn.dev33.satoken.loveqq.boot.support.SaPathMatcherHolder;
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
import com.kfyty.loveqq.framework.boot.K;
import com.kfyty.loveqq.framework.core.autoconfig.ApplicationContext;
import com.kfyty.loveqq.framework.core.support.PatternMatcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 插件自动装配启动后，自定义 Bean 应该经 *BeanInject 写进全局管理器
 */
public class InjectLoveqqTest {

	private static ApplicationContext ctx;

	/** 起注入专用应用，带上 contextPath 用来打路径加载器 */
	@BeforeAll
	public static void start() {
		ctx = K.start(InjectApp.class, "--k.mvc.tomcat.contextPath=app/");
	}

	/** 测完把容器关掉 */
	@AfterAll
	public static void stop() throws Exception {
		if (ctx != null) {
			ctx.close();
		}
	}

	/** 核心组件应该注入到 SaManager / Util / 策略中心 */
	@Test
	public void coreBeans_shouldInjectIntoManagers() {
		Assertions.assertSame(ctx.getBean(SaLogForConsole.class), SaManager.getLog());
		Assertions.assertSame(ctx.getBean(SaTokenPluginHolder.class), SaTokenPluginHolder.instance);
		Assertions.assertNotNull(ctx.getBean(SaTokenDaoDefaultImpl.class));
		Assertions.assertNotNull(SaManager.getSaTokenDao());
		Assertions.assertSame(ctx.getBean(StpInterfaceDefaultImpl.class), SaManager.getStpInterface());
		Assertions.assertTrue(SaTokenEventCenter.getListenerList()
				.contains(ctx.getBean(SaTokenListenerForSimple.class)));
		Assertions.assertSame(ctx.getBean(SaCheckLoginHandler.class),
				SaAnnotationStrategy.instance.annotationHandlerMap.get(SaCheckLogin.class));
		Assertions.assertSame(ctx.getBean(SaTempTemplate.class), SaManager.getSaTempTemplate());
		Assertions.assertSame(ctx.getBean(SaSameTemplate.class), SaManager.getSaSameTemplate());
		Assertions.assertSame(ctx.getBean(SaHttpBasicTemplate.class), SaHttpBasicUtil.saHttpBasicTemplate);
		Assertions.assertSame(ctx.getBean(SaHttpDigestTemplate.class), SaHttpDigestUtil.saHttpDigestTemplate);
		Assertions.assertSame(ctx.getBean(SaJsonTemplateDefaultImpl.class), SaManager.getSaJsonTemplate());
		Assertions.assertSame(ctx.getBean(SaHttpTemplateDefaultImpl.class), SaManager.getSaHttpTemplate());
		Assertions.assertSame(ctx.getBean(SaSerializerTemplateForJson.class),
				SaManager.getSaSerializerTemplate());
		Assertions.assertSame(ctx.getBean(SaTotpTemplate.class), SaManager.getSaTotpTemplate());
		Assertions.assertSame(ctx.getBean(StpLogic.class), StpUtil.getStpLogic());
		Assertions.assertSame(ctx.getBean(PatternMatcher.class), SaPathMatcherHolder.getPathMatcher());
		Assertions.assertTrue(SaFirewallStrategy.instance.checkHooks
				.contains(ctx.getBean(SaFirewallCheckHook.class)));
		Assertions.assertSame(ctx.getBean(SaCorsHandleFunction.class), SaStrategy.instance.corsHandle);
		Assertions.assertNotNull(SaTokenPluginHolder.instance.getPlugin(InjectConfig.MarkerPlugin.class));
	}

	/** SSO / OAuth2 / ApiKey / Sign 自定义 Bean 应该注入到对应 Manager */
	@Test
	public void pluginBeans_shouldInjectIntoManagers() {
		Assertions.assertSame(ctx.getBean(SaSsoServerTemplate.class),
				SaSsoServerProcessor.instance.ssoServerTemplate);
		Assertions.assertSame(ctx.getBean(SaSsoClientTemplate.class),
				SaSsoClientProcessor.instance.ssoClientTemplate);

		Assertions.assertSame(ctx.getBean(SaOAuth2Template.class), SaOAuth2Manager.getTemplate());
		Assertions.assertSame(ctx.getBean(SaOAuth2ServerProcessor.class), SaOAuth2ServerProcessor.instance);
		Assertions.assertSame(ctx.getBean(SaOAuth2DataLoaderDefaultImpl.class),
				SaOAuth2Manager.getDataLoader());
		Assertions.assertSame(ctx.getBean(SaOAuth2DataResolverDefaultImpl.class),
				SaOAuth2Manager.getDataResolver());
		Assertions.assertSame(ctx.getBean(SaOAuth2DataConverterDefaultImpl.class),
				SaOAuth2Manager.getDataConverter());
		Assertions.assertSame(ctx.getBean(SaOAuth2DataGenerateDefaultImpl.class),
				SaOAuth2Manager.getDataGenerate());
		Assertions.assertSame(ctx.getBean(SaOAuth2Dao.class), SaOAuth2Manager.getDao());
		Assertions.assertSame(ctx.getBean(SaOAuth2ScopeHandlerInterface.class),
				SaOAuth2Strategy.instance.scopeHandlerMap.get("loveqq-inject-scope"));
		Assertions.assertSame(ctx.getBean(SaOAuth2GrantTypeHandlerInterface.class),
				SaOAuth2Strategy.instance.grantTypeHandlerMap.get("loveqq_inject_grant"));

		Assertions.assertSame(ctx.getBean(SaApiKeyTemplate.class), SaApiKeyManager.getSaApiKeyTemplate());
		Assertions.assertSame(ctx.getBean(SaApiKeyDataLoaderDefaultImpl.class),
				SaApiKeyManager.getSaApiKeyDataLoader());

		Assertions.assertSame(ctx.getBean(SaSignTemplate.class), SaSignManager.getSaSignTemplate());
	}

	/** 配了 contextPath 时应该把路由前缀写进 ApplicationInfo */
	@Test
	public void contextPath_shouldWriteRoutePrefix() {
		Assertions.assertEquals("/app", ApplicationInfo.routePrefix);
	}

}
