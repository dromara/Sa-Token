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
package cn.dev33.satoken.solon;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.SaTokenContextForReadOnly;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.model.SaTokenContextModelBox;
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
import cn.dev33.satoken.plugin.SaTokenPlugin;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHook;
import cn.dev33.satoken.temp.SaTempTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * {@link SaBeanInject} 各注入方法应该把组件写进全局管理器
 */
@SaTokenTest
public class SaBeanInjectMethodTest {

	/** 各个 setXxx 注入方法应该把自定义组件写进 SaManager / 策略中心 */
	@Test
	public void injectMethods_shouldWriteIntoManagers() {
		SaBeanInject inject = new SaBeanInject(null, new SaTokenConfig(), null);

		SaTokenDaoDefaultImpl dao = new SaTokenDaoDefaultImpl();
		inject.setSaTokenDao(dao);
		Assertions.assertSame(dao, SaManager.getSaTokenDao());

		StpInterfaceDefaultImpl stpInterface = new StpInterfaceDefaultImpl();
		inject.setStpInterface(stpInterface);
		Assertions.assertSame(stpInterface, SaManager.getStpInterface());

		SaTokenContext context = new CustomReadOnlyContext();
		inject.setSaTokenContext(context);
		Assertions.assertSame(context, SaManager.getSaTokenContext());

		SaTokenListenerForSimple listener = new SaTokenListenerForSimple();
		inject.setSaTokenListener(Collections.singletonList(listener));
		Assertions.assertTrue(SaTokenEventCenter.getListenerList().contains(listener));

		SaCheckLoginHandler handler = new SaCheckLoginHandler();
		inject.setSaAnnotationHandler(Collections.singletonList(handler));
		Assertions.assertSame(handler, SaAnnotationStrategy.instance.annotationHandlerMap.get(SaCheckLogin.class));

		SaTempTemplate temp = new SaTempTemplate();
		inject.setSaTempTemplate(temp);
		Assertions.assertSame(temp, SaManager.getSaTempTemplate());

		SaSameTemplate same = new SaSameTemplate();
		inject.setSaIdTemplate(same);
		Assertions.assertSame(same, SaManager.getSaSameTemplate());

		SaHttpBasicTemplate basic = new SaHttpBasicTemplate();
		inject.setSaHttpBasicTemplate(basic);
		Assertions.assertSame(basic, SaHttpBasicUtil.saHttpBasicTemplate);

		SaHttpDigestTemplate digest = new SaHttpDigestTemplate();
		inject.setSaHttpDigestTemplate(digest);
		Assertions.assertSame(digest, SaHttpDigestUtil.saHttpDigestTemplate);

		SaJsonTemplateDefaultImpl json = new SaJsonTemplateDefaultImpl();
		inject.setSaJsonTemplate(json);
		Assertions.assertSame(json, SaManager.getSaJsonTemplate());

		SaHttpTemplateDefaultImpl http = new SaHttpTemplateDefaultImpl();
		inject.setSaHttpTemplate(http);
		Assertions.assertSame(http, SaManager.getSaHttpTemplate());

		SaSerializerTemplateForJson serializer = new SaSerializerTemplateForJson();
		inject.setSaSerializerTemplate(serializer);
		Assertions.assertSame(serializer, SaManager.getSaSerializerTemplate());

		SaTotpTemplate totp = new SaTotpTemplate();
		inject.setSaTotpTemplate(totp);
		Assertions.assertSame(totp, SaManager.getSaTotpTemplate());

		StpLogic stpLogic = new StpLogic("login");
		inject.setStpLogic(stpLogic);
		Assertions.assertSame(stpLogic, StpUtil.getStpLogic());

		SaFirewallCheckHook hook = (req, res, ext) -> {};
		inject.setSaFirewallCheckHooks(Collections.singletonList(hook));
		Assertions.assertTrue(SaFirewallStrategy.instance.checkHooks.contains(hook));

		SaCorsHandleFunction cors = (req, res, sto) -> {};
		inject.setCorsHandle(cors);
		Assertions.assertSame(cors, SaStrategy.instance.corsHandle);

		SaTokenPlugin plugin = new MarkerPlugin();
		inject.setSaTokenPluginList(Collections.singletonList(plugin));
		Assertions.assertSame(plugin, SaTokenPluginHolder.instance.getPlugin(MarkerPlugin.class));
	}

	static class CustomReadOnlyContext implements SaTokenContextForReadOnly {
		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public SaTokenContextModelBox getModelBox() {
			return null;
		}

		@Override
		public SaRequest getRequest() {
			return null;
		}

		@Override
		public SaResponse getResponse() {
			return null;
		}

		@Override
		public SaStorage getStorage() {
			return null;
		}
	}

	static class MarkerPlugin implements SaTokenPlugin {
		@Override
		public void install() {
		}
	}

}
