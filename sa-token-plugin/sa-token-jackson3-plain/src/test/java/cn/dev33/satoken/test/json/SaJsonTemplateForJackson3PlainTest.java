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
package cn.dev33.satoken.test.json;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateForJackson3Plain;
import cn.dev33.satoken.plugin.SaTokenPluginForJackson3Plain;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionForJackson3PlainCustomized;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.model.SysUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Jackson3 无类型信息 JSON 插件测试。 */
public class SaJsonTemplateForJackson3PlainTest extends SaJsonTemplateTestCommon {

	/** 验证对象、Map、null 的基础转换契约，且 JSON 不含类型信息。 */
	@Test
	void convertsPlainValues() {
		SaJsonTemplate template = new SaJsonTemplateForJackson3Plain();
		assertTemplate(SaJsonTemplateForJackson3Plain.class, template,
				"{\"age\":18,\"id\":10001,\"name\":\"张三\",\"role\":null}", false, true);
		Assertions.assertFalse(template.objectToJson(new SysUser(10001, "张三", 18)).contains("@class"));
	}

	/** 验证 SPI 插件会同时注册 JSON 模板和定制 Session 类型。 */
	@Test
	void installsPlainTemplateAndSessionStrategy() {
		assertPluginInstall(new SaTokenPluginForJackson3Plain(), SaJsonTemplateForJackson3Plain.class,
				SaSessionForJackson3PlainCustomized.class);
	}

	/** 验证 META-INF/satoken 中的 SPI 声明可被插件加载器发现。 */
	@Test
	void discoversPluginThroughSpi() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.loaderPlugins();
		Assertions.assertTrue(holder.isInstalledPlugin(SaTokenPluginForJackson3Plain.class));
	}

	/** 验证持久化恢复后的业务对象可通过 getModel(key, Class) 显式还原。 */
	@Test
	void restoresSessionModelUsingRequestedType() {
		new SaTokenPluginForJackson3Plain().install();
		SaSession session = SaStrategy.instance.createSession.apply("plain-session");
		SysUser user = new SysUser(10001, "张三", 18);
		session.set("user", user);
		session.set("name", "Sa-Token");

		String json = SaManager.getSaJsonTemplate().objectToJson(session);
		Assertions.assertFalse(json.contains("@class"));
		SaSession restored = SaManager.getSaJsonTemplate().jsonToObject(json, SaSessionForJackson3PlainCustomized.class);

		Assertions.assertEquals(user.toString(), restored.getModel("user", SysUser.class).toString());
		Assertions.assertEquals("Sa-Token", restored.getModel("name", String.class));
		Assertions.assertNull(restored.getModel("missing", SysUser.class));
	}

}
