package com.pj.test;

import cn.dev33.satoken.json.SaJsonTemplateForForyJson;
import cn.dev33.satoken.plugin.SaTokenPluginForForyJson;
import cn.dev33.satoken.session.SaSessionForForyJsonCustomized;
import com.pj.test.json.SaJsonTemplateTestCommon;
import com.pj.test.model.SysUser;
import org.apache.fory.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Apache Fory JSON 插件测试 */
public class SaJsonTemplateForForyJsonTest extends SaJsonTemplateTestCommon {

	/** 验证 Fory JSON 的基础转换契约 */
	@Test
	void convertsValues() {
		assertTemplate(SaJsonTemplateForForyJson.class, new SaJsonTemplateForForyJson(), null, false, false);
	}

	/** 验证 Fory JSON 兼容 Session 常用类型 */
	@Test
	void allowsCommonSessionTypes() {
		assertCommonSessionTypes(new SaJsonTemplateForForyJson(), false);
	}

	/** 验证 Fory JSON 将空字符串视为无值 */
	@Test
	void convertsEmptyStringsToNull() {
		assertEmptyStringConversions(new SaJsonTemplateForForyJson());
	}

	/** 验证 Fory JSON 插件替换模板和 Session 策略 */
	@Test
	void installsJsonTemplateAndSessionStrategy() {
		assertPluginInstall(new SaTokenPluginForForyJson(), SaJsonTemplateForForyJson.class,
				SaSessionForForyJsonCustomized.class);
	}

	/** 验证 Fory JSON 定制 Session 覆盖全部 getModel 分支 */
	@Test
	void getsModelsFromBasicNullJsonObjectAndJsonString() {
		SaSessionForForyJsonCustomized session = new SaSessionForForyJsonCustomized("fory");
		JsonObject userObject = new JsonObject();
		userObject.put("id", 10001);
		userObject.put("name", "张三");
		userObject.put("age", 18);
		session.set("count", "12");
		session.set("userObject", userObject);
		session.set("userJson", "{\"id\":10002,\"name\":\"李四\",\"age\":20}");
		Assertions.assertEquals(Integer.valueOf(12), session.getModel("count", Integer.class));
		Assertions.assertNull(session.getModel("missing", SysUser.class));
		Assertions.assertEquals("张三", session.getModel("userObject", SysUser.class).getName());
		Assertions.assertEquals("李四", session.getModel("userJson", SysUser.class).getName());
	}

}
