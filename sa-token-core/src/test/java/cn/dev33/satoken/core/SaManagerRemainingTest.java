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
package cn.dev33.satoken.core;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContextForThreadLocal;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.http.SaHttpTemplateDefaultImpl;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.temp.SaTempTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SaManager 剩余分支覆盖测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaManagerRemainingTest {

	/** config 为 null 时 getConfig 应懒加载默认配置 */
	@Test
	void getConfig_lazyInitWhenNull() {
		SaManager.config = null;
		SaTokenConfig config = SaManager.getConfig();
		Assertions.assertNotNull(config);
	}

	/** SaStrategy 覆盖 getSaTokenConfig 时应返回自定义配置 */
	@Test
	void getConfig_viaStrategyOverride() {
		SaTokenConfig custom = new SaTokenConfig();
		custom.setTokenName("strategy-config");
		SaStrategy.instance.getSaTokenConfig = () -> custom;
		Assertions.assertSame(custom, SaManager.getConfig());
		Assertions.assertEquals("strategy-config", SaManager.getConfig().getTokenName());
	}

	/** setConfig 开启 isPrint 时应正常写入配置 */
	@Test
	void setConfig_withPrintBanner() {
		SaTokenConfig config = new SaTokenConfig();
		config.setIsPrint(true);
		config.setIsLog(false);
		SaManager.setConfig(config);
		Assertions.assertSame(config, SaManager.getConfig());
	}

	/** 替换 SaTokenDao 时应调用旧实例的 destroy */
	@Test
	void setSaTokenDao_callsDestroyOnPrevious() {
		AtomicBoolean destroyed = new AtomicBoolean(false);
		SaTokenDao oldDao = new SaTokenDaoDefaultImpl() {
			@Override
			public void destroy() {
				destroyed.set(true);
			}
		};
		SaManager.setSaTokenDao(oldDao);
		SaManager.setSaTokenDao(new SaTokenDaoDefaultImpl());
		Assertions.assertTrue(destroyed.get());
	}

	/** 各组件为 null 时 getter 应懒加载默认实现 */
	@Test
	void lazyInitGetters() {
		SaManager.setSaTokenDao(null);
		Assertions.assertNotNull(SaManager.getSaTokenDao());

		SaManager.setStpInterface(null);
		Assertions.assertNotNull(SaManager.getStpInterface());
		Assertions.assertTrue(SaManager.getStpInterface() instanceof StpInterfaceDefaultImpl);

		SaManager.setSaTokenContext(null);
		Assertions.assertNotNull(SaManager.getSaTokenContext());
		Assertions.assertTrue(SaManager.getSaTokenContext() instanceof SaTokenContextForThreadLocal);

		SaManager.setSaTempTemplate(null);
		Assertions.assertNotNull(SaManager.getSaTempTemplate());
		Assertions.assertTrue(SaManager.getSaTempTemplate() instanceof SaTempTemplate);

		SaManager.setSaJsonTemplate(null);
		Assertions.assertNotNull(SaManager.getSaJsonTemplate());
		Assertions.assertTrue(SaManager.getSaJsonTemplate() instanceof SaJsonTemplateDefaultImpl);

		SaManager.setSaHttpTemplate(null);
		Assertions.assertNotNull(SaManager.getSaHttpTemplate());
		Assertions.assertTrue(SaManager.getSaHttpTemplate() instanceof SaHttpTemplateDefaultImpl);

		SaManager.setSaSerializerTemplate(null);
		Assertions.assertNotNull(SaManager.getSaSerializerTemplate());
		Assertions.assertTrue(SaManager.getSaSerializerTemplate() instanceof SaSerializerTemplateForJson);

		SaManager.setSaSameTemplate(null);
		Assertions.assertNotNull(SaManager.getSaSameTemplate());
		Assertions.assertTrue(SaManager.getSaSameTemplate() instanceof SaSameTemplate);

		SaManager.setSaTotpTemplate(null);
		Assertions.assertNotNull(SaManager.getSaTotpTemplate());
		Assertions.assertTrue(SaManager.getSaTotpTemplate() instanceof SaTotpTemplate);
	}

}
