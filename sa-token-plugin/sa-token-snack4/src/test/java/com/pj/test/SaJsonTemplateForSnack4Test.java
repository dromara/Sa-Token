package com.pj.test;

import cn.dev33.satoken.json.SaJsonTemplateForSnack4;
import cn.dev33.satoken.plugin.SaTokenPluginForSnack4;
import cn.dev33.satoken.session.SaSessionForSnack4Customized;
import com.pj.test.json.SaJsonTemplateTestCommon;
import com.pj.test.model.SysUser;
import cn.dev33.satoken.strategy.SaJsonStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.HashMap;
import java.util.Map;

/** Snack4 JSON 插件测试 */
public class SaJsonTemplateForSnack4Test extends SaJsonTemplateTestCommon {

	/** 重置 JSON 白名单初始化状态 */
	@BeforeEach
	void reset() {
		resetJsonStrategy();
	}

	/** 验证 Snack4 的基础转换契约 */
	@Test
	void convertsValues() {
		assertTemplate(SaJsonTemplateForSnack4.class, new SaJsonTemplateForSnack4(),
				"{\"id\":10001,\"name\":\"张三\",\"age\":18}", false, true);
		Assertions.assertTrue(SaJsonStrategy.instance.isInit());
	}

	/** 验证 Snack4 兼容 Session 常用类型 */
	@Test
	void allowsCommonSessionTypes() {
		assertCommonSessionTypes(new SaJsonTemplateForSnack4(), false);
	}

	/** 验证 Snack4 保留 Session Map 包装类型 */
	@Test
	void allowsWrapperTypesInSessionMap() {
		assertWrapperTypesInSessionMap(new SaJsonTemplateForSnack4());
	}

	/** 验证 Snack4 拒绝未白名单类型 */
	@Test
	void blocksUnknownAllowType() {
		assertBlocksUnknownAllowType(new SaJsonTemplateForSnack4(), "{\"@type\":\"java.lang.ProcessBuilder\",\"command\":[\"calc.exe\"]}");
	}

	/** 验证 Snack4 保留缺失类型错误 */
	@Test
	void classNotFoundKeepsOriginalMessage() {
		assertClassNotFoundMessage(new SaJsonTemplateForSnack4(),
				"{\"@type\":\"com.pj.test.model.NonExistentJsonType\",\"mode\":3}",
				"Blocked type, class: com.pj.test.model.NonExistentJsonType");
	}

	/** 验证 Snack4 初始化后锁定白名单 */
	@Test
	void strategyCannotBeChangedAfterInitialization() {
		assertStrategyCannotBeChangedAfterInitialization(new SaJsonTemplateForSnack4());
	}

	/** 验证 Snack4 将空字符串视为无值 */
	@Test
	void convertsEmptyStringsToNull() {
		assertEmptyStringConversions(new SaJsonTemplateForSnack4());
	}

	/** 验证 Snack4 包装非法对象和 Map JSON 异常 */
	@Test
	void wrapsInvalidJsonExceptions() {
		SaJsonTemplateForSnack4 template = new SaJsonTemplateForSnack4();
		Assertions.assertThrows(RuntimeException.class, () -> template.jsonToObject("]", Object.class));
		Assertions.assertThrows(RuntimeException.class, () -> template.jsonToMap("]"));
	}

	/** 验证 Snack4 插件替换模板和 Session 策略 */
	@Test
	void installsJsonTemplateAndSessionStrategy() {
		assertPluginInstall(new SaTokenPluginForSnack4(), SaJsonTemplateForSnack4.class,
				SaSessionForSnack4Customized.class);
	}

	/** 验证 Snack4 定制 Session 覆盖全部 getModel 分支 */
	@Test
	void getsModelsFromBasicNullNodeStringAndMap() {
		SaSessionForSnack4Customized session = new SaSessionForSnack4Customized("snack4");
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("id", 10003);
		userMap.put("name", "王五");
		userMap.put("age", 22);
		session.set("count", "12");
		session.set("userNode", ONode.ofJson("{\"id\":10001,\"name\":\"张三\",\"age\":18}"));
		session.set("userJson", "{\"id\":10002,\"name\":\"李四\",\"age\":20}");
		session.set("userMap", userMap);
		Assertions.assertEquals(Integer.valueOf(12), session.getModel("count", Integer.class));
		Assertions.assertNull(session.getModel("missing", SysUser.class));
		Assertions.assertEquals("张三", session.getModel("userNode", SysUser.class).getName());
		Assertions.assertEquals("李四", session.getModel("userJson", SysUser.class).getName());
		Assertions.assertEquals("王五", session.getModel("userMap", SysUser.class).getName());
	}

	/** 验证无参构造函数可以创建 Session 对象 */
	@Test
	void createsSessionWithNoArgConstructor() {
		Assertions.assertNotNull(new SaSessionForSnack4Customized());
	}

	/**
	 * 验证 @type 与白名单类名完全一致时直接放行反序列化
	 * <p>注意：这里不能用 SysUser（它实现了 SaJsonType，会在更早的白名单条目上经"可赋值"路径放行，
	 * 轮不到精确匹配分支），所以用一个不实现任何白名单接口的普通 POJO
	 */
	@Test
	void allowsExactWhitelistTypeName() {
		resetJsonStrategy();
		SaJsonStrategy.instance.registerAllowType(PlainPoint.class);
		SaJsonTemplateForSnack4 template = new SaJsonTemplateForSnack4();
		Object obj = template.jsonToObject("{\"@type\":\"com.pj.test.SaJsonTemplateForSnack4Test$PlainPoint\",\"x\":10,\"y\":20}");
		Assertions.assertTrue(obj instanceof PlainPoint);
		Assertions.assertEquals("com.pj.test.SaJsonTemplateForSnack4Test$PlainPoint", obj.getClass().getName());
	}

	/** 测试用简单 POJO：不实现 SaJsonType，也不在其它白名单类型的可赋值范围内 */
	public static class PlainPoint {
		public int x;
		public int y;
	}

}
