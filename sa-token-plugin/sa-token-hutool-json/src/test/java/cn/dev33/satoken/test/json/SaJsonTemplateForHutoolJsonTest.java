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

import cn.dev33.satoken.json.SaJsonTemplateForHutoolJson;
import cn.dev33.satoken.plugin.SaTokenPluginForHutoolJson;
import cn.dev33.satoken.session.SaSessionForHutoolJsonCustomized;
import cn.dev33.satoken.test.model.SysUser;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Hutool Json JSON 插件测试
 *
 * @author wcanxuan
 * @since 1.46.0
 */
public class SaJsonTemplateForHutoolJsonTest extends SaJsonTemplateTestCommon {

	/** 验证 Hutool Json 的基础转换契约 */
	@Test
	void convertsValues() {
		assertTemplate(SaJsonTemplateForHutoolJson.class, new SaJsonTemplateForHutoolJson(),
				"{\"id\":10001,\"name\":\"张三\",\"age\":18}", false, false);
	}

	/** 验证 Hutool Json 的 Session 常用类型往返（Date 按时间戳存储） */
	@Test
	void allowsCommonSessionTypes() {
		assertCommonSessionTypes(new SaJsonTemplateForHutoolJson(), false);
	}

	/** 验证 Hutool Json 保留 Session Map 包装类型 */
	@Test
	void allowsWrapperTypesInSessionMap() {
		assertWrapperTypesInSessionMap(new SaJsonTemplateForHutoolJson());
	}

	/** 验证 Hutool Json 将空字符串视为无值 */
	@Test
	void convertsEmptyStringsToNull() {
		assertEmptyStringConversions(new SaJsonTemplateForHutoolJson());
	}

	/** 验证 Hutool Json 插件替换模板和 Session 策略 */
	@Test
	void installsJsonTemplateAndSessionStrategy() {
		assertPluginInstall(new SaTokenPluginForHutoolJson(), SaJsonTemplateForHutoolJson.class,
				SaSessionForHutoolJsonCustomized.class);
	}

	/** 验证 Hutool Json 定制 Session 覆盖全部 getModel 分支 */
	@Test
	void getsModelsFromBasicNullJsonObjectAndJsonString() {
		SaSessionForHutoolJsonCustomized session = new SaSessionForHutoolJsonCustomized("hutool-json");
		session.set("count", "12");
		session.set("userObject", JSONUtil.parseObj("{\"id\":10001,\"name\":\"张三\",\"age\":18}"));
		session.set("userJson", "{\"id\":10002,\"name\":\"李四\",\"age\":20}");
		Assertions.assertEquals(Integer.valueOf(12), session.getModel("count", Integer.class));
		Assertions.assertNull(session.getModel("missing", SysUser.class));
		Assertions.assertEquals("张三", session.getModel("userObject", SysUser.class).getName());
		Assertions.assertEquals("李四", session.getModel("userJson", SysUser.class).getName());
	}

	/** 验证无参构造函数可以创建 Session 对象 */
	@Test
	void createsSessionWithNoArgConstructor() {
		Assertions.assertNotNull(new SaSessionForHutoolJsonCustomized());
	}

}
