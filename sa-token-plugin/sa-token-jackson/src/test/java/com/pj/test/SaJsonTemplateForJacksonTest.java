package com.pj.test;

import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateForJackson;
import cn.dev33.satoken.plugin.SaTokenPluginForJackson;
import com.pj.test.json.SaJsonTemplateTestCommon;
import cn.dev33.satoken.SaManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Jackson JSON 插件测试 */
public class SaJsonTemplateForJacksonTest extends SaJsonTemplateTestCommon {

	/** 重置 JSON 白名单初始化状态 */
	@BeforeEach
	void reset() {
		resetJsonStrategy();
	}

	/** 验证 Jackson 的基础转换契约 */
	@Test
	void convertsValues() {
		SaJsonTemplate template = new SaJsonTemplateForJackson();
		assertTemplate(SaJsonTemplateForJackson.class, template,
				"{\"@class\":\"com.pj.test.model.SysUser\",\"id\":10001,\"name\":\"张三\",\"age\":18,\"role\":null}", true, true);
	}

	/** 验证 Jackson 保留 Session 常用类型 */
	@Test
	void allowsCommonSessionTypes() {
		assertCommonSessionTypes(new SaJsonTemplateForJackson(), true);
	}

	/** 验证 Jackson 保留 Session Map 包装类型 */
	@Test
	void allowsWrapperTypesInSessionMap() {
		assertWrapperTypesInSessionMap(new SaJsonTemplateForJackson());
	}

	/** 验证 Jackson 拒绝未白名单类型 */
	@Test
	void blocksUnknownAllowType() {
		assertBlocksUnknownAllowType(new SaJsonTemplateForJackson(), "{\"@class\":\"java.lang.ProcessBuilder\",\"command\":[\"calc.exe\"]}");
	}

	/** 验证 Jackson 保留缺失类型错误 */
	@Test
	void classNotFoundKeepsOriginalMessage() {
		assertClassNotFoundMessage(new SaJsonTemplateForJackson(),
				"{\"@class\":\"com.pj.test.model.NonExistentJsonType\",\"mode\":3}", "no such class found");
	}

	/** 验证 Jackson 初始化后锁定白名单 */
	@Test
	void strategyCannotBeChangedAfterInitialization() {
		assertStrategyCannotBeChangedAfterInitialization(new SaJsonTemplateForJackson());
	}

	/** 验证 Jackson 将空字符串视为无值 */
	@Test
	void convertsEmptyStringsToNull() {
		assertEmptyStringConversions(new SaJsonTemplateForJackson());
	}

	/** 验证 Jackson 包装非法对象和 Map JSON 异常 */
	@Test
	void wrapsInvalidJsonExceptions() {
		SaJsonTemplate template = new SaJsonTemplateForJackson();
		Assertions.assertThrows(RuntimeException.class, () -> template.jsonToObject("{", Object.class));
		Assertions.assertThrows(RuntimeException.class, () -> template.jsonToMap("{"));
	}

	/** 验证 Jackson 插件仅在默认模板时安装 */
	@Test
	void installsOnlyDefaultJsonTemplate() {
		new SaTokenPluginForJackson().install();
		Assertions.assertEquals(SaJsonTemplateForJackson.class, SaManager.getSaJsonTemplate().getClass());
		SaJsonTemplate customTemplate = new SaJsonTemplateForJackson();
		SaManager.setSaJsonTemplate(customTemplate);
		new SaTokenPluginForJackson().install();
		Assertions.assertSame(customTemplate, SaManager.getSaJsonTemplate());
	}

}
