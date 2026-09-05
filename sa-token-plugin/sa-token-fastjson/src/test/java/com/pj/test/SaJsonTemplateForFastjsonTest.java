package com.pj.test;

import cn.dev33.satoken.json.SaJsonTemplateForFastjson;
import cn.dev33.satoken.plugin.SaTokenPluginForFastjson;
import cn.dev33.satoken.session.SaSessionForFastjsonCustomized;
import com.alibaba.fastjson.JSONObject;
import com.pj.test.json.SaJsonTemplateTestCommon;
import com.pj.test.model.SysUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Fastjson JSON 插件测试 */
public class SaJsonTemplateForFastjsonTest extends SaJsonTemplateTestCommon {

	/** 验证 Fastjson 的基础转换契约 */
	@Test
	void convertsValues() {
		assertTemplate(SaJsonTemplateForFastjson.class, new SaJsonTemplateForFastjson(),
				"{\"age\":18,\"id\":10001,\"name\":\"张三\"}", false, true);
	}

	/** 验证 Fastjson 将空字符串视为无值 */
	@Test
	void convertsEmptyStringsToNull() {
		assertEmptyStringConversions(new SaJsonTemplateForFastjson());
	}

	/** 验证 Fastjson 插件替换模板和 Session 策略 */
	@Test
	void installsJsonTemplateAndSessionStrategy() {
		assertPluginInstall(new SaTokenPluginForFastjson(), SaJsonTemplateForFastjson.class,
				SaSessionForFastjsonCustomized.class);
	}

	/** 验证 Fastjson 定制 Session 覆盖全部 getModel 分支 */
	@Test
	void getsModelsFromBasicNullJsonObjectAndJsonString() {
		SaSessionForFastjsonCustomized session = new SaSessionForFastjsonCustomized("fastjson");
		session.set("count", "12");
		session.set("userObject", JSONObject.parseObject("{\"id\":10001,\"name\":\"张三\",\"age\":18}"));
		session.set("userJson", "{\"id\":10002,\"name\":\"李四\",\"age\":20}");
		Assertions.assertEquals(Integer.valueOf(12), session.getModel("count", Integer.class));
		Assertions.assertNull(session.getModel("missing", SysUser.class));
		Assertions.assertEquals("张三", session.getModel("userObject", SysUser.class).getName());
		Assertions.assertEquals("李四", session.getModel("userJson", SysUser.class).getName());
	}

	/** 验证无参构造函数可以创建 Session 对象 */
	@Test
	void createsSessionWithNoArgConstructor() {
		Assertions.assertNotNull(new SaSessionForFastjsonCustomized());
	}

}
