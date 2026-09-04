package com.pj.test;

import cn.dev33.satoken.json.SaJsonTemplateForSnack3;
import cn.dev33.satoken.plugin.SaTokenPluginForSnack3;
import cn.dev33.satoken.session.SaSessionForSnack3Customized;
import com.pj.test.json.SaJsonTemplateTestCommon;
import com.pj.test.model.SysUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

import java.util.HashMap;
import java.util.Map;

/** Snack3 JSON 插件测试 */
public class SaJsonTemplateForSnack3Test extends SaJsonTemplateTestCommon {

	/** 验证 Snack3 的基础转换契约 */
	@Test
	void convertsValues() {
		assertTemplate(SaJsonTemplateForSnack3.class, new SaJsonTemplateForSnack3(),
				"{\"id\":10001,\"name\":\"张三\",\"age\":18}", false, true);
	}

	/** 验证 Snack3 将空字符串视为无值 */
	@Test
	void convertsEmptyStringsToNull() {
		assertEmptyStringConversions(new SaJsonTemplateForSnack3());
	}

	/** 验证 Snack3 插件替换模板和 Session 策略 */
	@Test
	void installsJsonTemplateAndSessionStrategy() {
		assertPluginInstall(new SaTokenPluginForSnack3(), SaJsonTemplateForSnack3.class,
				SaSessionForSnack3Customized.class);
	}

	/** 验证 Snack3 定制 Session 覆盖全部 getModel 分支 */
	@Test
	void getsModelsFromBasicNullNodeStringAndMap() {
		SaSessionForSnack3Customized session = new SaSessionForSnack3Customized("snack3");
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("id", 10003);
		userMap.put("name", "王五");
		userMap.put("age", 22);
		session.set("count", "12");
		session.set("userNode", ONode.load("{\"id\":10001,\"name\":\"张三\",\"age\":18}"));
		session.set("userJson", "{\"id\":10002,\"name\":\"李四\",\"age\":20}");
		session.set("userMap", userMap);
		Assertions.assertEquals(Integer.valueOf(12), session.getModel("count", Integer.class));
		Assertions.assertNull(session.getModel("missing", SysUser.class));
		Assertions.assertEquals("张三", session.getModel("userNode", SysUser.class).getName());
		Assertions.assertEquals("李四", session.getModel("userJson", SysUser.class).getName());
		Assertions.assertEquals("王五", session.getModel("userMap", SysUser.class).getName());
	}

}
