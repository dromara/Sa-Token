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
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.http.SaHttpTemplate;
import cn.dev33.satoken.http.SaHttpTemplateDefaultImpl;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import cn.dev33.satoken.serializer.SaSerializerTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.temp.SaTempTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SaManager 全局组件管理测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaManagerTest {

	/** 设置与获取 SaTokenConfig 应读写同一配置 */
	@Test
	void setAndGetConfig() {
		SaTokenConfig config = new SaTokenConfig();
		config.setTokenName("unit-test-token");
		config.setIsPrint(false);
		SaManager.setConfig(config);
		Assertions.assertEquals("unit-test-token", SaManager.getConfig().getTokenName());
	}

	/** 设置与获取 SaTokenDao 应读写同一实例 */
	@Test
	void setAndGetSaTokenDao() {
		SaTokenDao dao = new SaTokenDaoDefaultImpl();
		SaManager.setSaTokenDao(dao);
		Assertions.assertSame(dao, SaManager.getSaTokenDao());
	}

	/** 设置与获取 StpInterface 后应能正常调用 */
	@Test
	void setAndGetStpInterface() {
		StpInterface stpInterface = new StpInterfaceDefaultImpl() {
			@Override
			public java.util.List<String> getPermissionList(Object loginId, String loginType) {
				return Collections.singletonList("test");
			}
		};
		SaManager.setStpInterface(stpInterface);
		Assertions.assertSame(stpInterface, SaManager.getStpInterface());
		Assertions.assertEquals("test", SaManager.getStpInterface().getPermissionList(1, "login").get(0));
	}

	/** 各 Template 与 Log 组件的 set/get 应读写同一实例 */
	@Test
	void setAndGetTemplates() {
		SaJsonTemplate jsonTemplate = new SaJsonTemplateDefaultImpl();
		SaHttpTemplate httpTemplate = new SaHttpTemplateDefaultImpl();
		SaSerializerTemplate serializerTemplate = new SaSerializerTemplateForJson();
		SaTempTemplate tempTemplate = new SaTempTemplate();
		SaSameTemplate sameTemplate = new SaSameTemplate();
		SaLog log = new SaLogForConsole();

		SaManager.setSaJsonTemplate(jsonTemplate);
		SaManager.setSaHttpTemplate(httpTemplate);
		SaManager.setSaSerializerTemplate(serializerTemplate);
		SaManager.setSaTempTemplate(tempTemplate);
		SaManager.setSaSameTemplate(sameTemplate);
		SaManager.setLog(log);

		Assertions.assertSame(jsonTemplate, SaManager.getSaJsonTemplate());
		Assertions.assertSame(httpTemplate, SaManager.getSaHttpTemplate());
		Assertions.assertSame(serializerTemplate, SaManager.getSaSerializerTemplate());
		Assertions.assertSame(tempTemplate, SaManager.getSaTempTemplate());
		Assertions.assertSame(sameTemplate, SaManager.getSaSameTemplate());
		Assertions.assertSame(log, SaManager.getLog());
	}

	/** putStpLogic 后应能按 loginType 获取 StpLogic */
	@Test
	void putStpLogicAndGetStpLogic() {
		StpLogic adminLogic = new StpLogic("admin");
		SaManager.putStpLogic(adminLogic);
		Assertions.assertSame(adminLogic, SaManager.getStpLogic("admin", false));
		Assertions.assertSame(StpUtil.stpLogic, SaManager.getStpLogic("", false));
	}

	/** 默认构造函数应可正常创建实例 */
	@Test
	void defaultConstructor() {
		Assertions.assertDoesNotThrow(SaManager::new);
	}

	/** removeStpLogic 后应无法再获取对应 StpLogic */
	@Test
	void removeStpLogic() {
		StpLogic logic = new StpLogic("temp");
		SaManager.putStpLogic(logic);
		Assertions.assertSame(logic, SaManager.getStpLogic("temp", false));
		SaManager.removeStpLogic("temp");
		Assertions.assertThrows(SaTokenException.class, () -> SaManager.getStpLogic("temp", false));
	}

	/** 缺失的 loginType 在宽松模式下应自动创建 StpLogic */
	@Test
	void getStpLogic_autoCreateWhenMissing() {
		StpLogic logic = SaManager.getStpLogic("auto-create");
		Assertions.assertNotNull(logic);
		Assertions.assertEquals("auto-create", logic.getLoginType());
		SaManager.removeStpLogic("auto-create");
	}

	/** loginType 为 null 或空字符串时应返回默认 StpLogic */
	@Test
	void getStpLogic_emptyLoginTypeReturnsDefault() {
		Assertions.assertSame(StpUtil.stpLogic, SaManager.getStpLogic(null));
		Assertions.assertSame(StpUtil.stpLogic, SaManager.getStpLogic(""));
	}

	/** 严格模式下获取不存在的 StpLogic 应抛出 CODE_10002 异常 */
	@Test
	void getStpLogicStrictModeThrows() {
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> SaManager.getStpLogic("missing-type", false));
		Assertions.assertEquals(SaErrorCode.CODE_10002, ex.getCode());
	}

	/** setSaTotpTemplate / getSaTotpTemplate 应读写同一实例 */
	@Test
	void setAndGetSaTotpTemplate() {
		SaTotpTemplate template = new SaTotpTemplate();
		SaManager.setSaTotpTemplate(template);
		Assertions.assertSame(template, SaManager.getSaTotpTemplate());
	}

	/** setSaTokenContext 后应能获取同一上下文实现 */
	@Test
	void setSaTokenContext() {
		SaTokenContextForThreadLocal context = new SaTokenContextForThreadLocal();
		SaManager.setSaTokenContext(context);
		Assertions.assertSame(context, SaManager.getSaTokenContext());
	}

	/** 开启日志且未指定 isColorLog 时，setConfig 应自动推断彩色日志开关 */
	@Test
	void setConfig_autoColorLogWhenEnabled() {
		SaTokenConfig config = new SaTokenConfig();
		config.setIsPrint(false);
		config.setIsLog(true);
		config.setIsColorLog(null);
		SaManager.setConfig(config);
		Assertions.assertNotNull(SaManager.getConfig().getIsColorLog());
	}

	/** 关闭日志时，setConfig 不应自动写入彩色日志配置 */
	@Test
	void setConfig_doesNotInferColorLogWhenLoggingDisabled() {
		SaTokenConfig config = new SaTokenConfig();
		config.setIsPrint(false);
		config.setIsLog(false);
		config.setIsColorLog(null);

		SaManager.setConfig(config);

		Assertions.assertNull(SaManager.getConfig().getIsColorLog());
	}

	/** 空配置应被接受，并在后续读取时恢复默认配置 */
	@Test
	void setConfig_acceptsNullAndRestoresDefaultsOnRead() {
		SaManager.setConfig(null);

		Assertions.assertEquals("satoken", SaManager.getConfig().getTokenName());
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

	/** SaStrategy 覆盖 getSaTokenConfig 时应返回自定义配置 */
	@Test
	void getConfig_viaStrategyOverride() {
		SaTokenConfig custom = new SaTokenConfig();
		custom.setTokenName("strategy-config");
		SaStrategy.instance.getSaTokenConfig = () -> custom;
		Assertions.assertSame(custom, SaManager.getConfig());
		Assertions.assertEquals("strategy-config", SaManager.getConfig().getTokenName());
	}

	/** config 为 null 时直接调 getConfig 应懒加载默认配置 */
	@Test
	void getConfig_lazyInitWhenNull() {
		SaManager.config = null;
		SaTokenConfig config = SaManager.getConfig();
		Assertions.assertNotNull(config);
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
