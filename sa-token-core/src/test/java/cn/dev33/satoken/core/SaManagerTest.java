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
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.http.SaHttpTemplate;
import cn.dev33.satoken.http.SaHttpTemplateDefaultImpl;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.serializer.SaSerializerTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

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

}
