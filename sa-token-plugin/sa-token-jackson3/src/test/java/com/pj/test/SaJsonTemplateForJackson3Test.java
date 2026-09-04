package com.pj.test;

import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateForJackson3;
import cn.dev33.satoken.plugin.SaTokenPluginForJackson3;
import com.pj.test.json.SaJsonTemplateTestCommon;
import cn.dev33.satoken.SaManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Jackson3 JSON 插件测试 */
public class SaJsonTemplateForJackson3Test extends SaJsonTemplateTestCommon {

	/** 重置 JSON 白名单初始化状态 */
	@BeforeEach
	void reset() {
		resetJsonStrategy();
	}

	/** 验证 Jackson3 的基础转换契约 */
	@Test
	void convertsValues() {
		SaJsonTemplate template = new SaJsonTemplateForJackson3();
		assertTemplate(SaJsonTemplateForJackson3.class, template,
				"{\"@class\":\"com.pj.test.model.SysUser\",\"age\":18,\"id\":10001,\"name\":\"张三\",\"role\":null}", true, true);
	}

	/** 验证 Jackson3 保留 Session 常用类型 */
	@Test
	void allowsCommonSessionTypes() {
		assertCommonSessionTypes(new SaJsonTemplateForJackson3(), true);
	}

	/** 验证 Jackson3 保留 Session Map 包装类型 */
	@Test
	void allowsWrapperTypesInSessionMap() {
		assertWrapperTypesInSessionMap(new SaJsonTemplateForJackson3());
	}

	/** 验证 Jackson3 拒绝未白名单类型 */
	@Test
	void blocksUnknownAllowType() {
		assertBlocksUnknownAllowType(new SaJsonTemplateForJackson3(), "{\"@class\":\"java.lang.ProcessBuilder\",\"command\":[\"calc.exe\"]}");
	}

	/** 验证 Jackson3 保留缺失类型错误 */
	@Test
	void classNotFoundKeepsOriginalMessage() {
		assertClassNotFoundMessage(new SaJsonTemplateForJackson3(),
				"{\"@class\":\"com.pj.test.model.NonExistentJsonType\",\"mode\":3}", "no such class found");
	}

	/** 验证 Jackson3 初始化后锁定白名单 */
	@Test
	void strategyCannotBeChangedAfterInitialization() {
		assertStrategyCannotBeChangedAfterInitialization(new SaJsonTemplateForJackson3());
	}

	/** 验证 Jackson3 将空字符串视为无值 */
	@Test
	void convertsEmptyStringsToNull() {
		assertEmptyStringConversions(new SaJsonTemplateForJackson3());
	}

	/** 验证 Jackson3 包装非法对象和 Map JSON 异常 */
	@Test
	void wrapsInvalidJsonExceptions() {
		SaJsonTemplate template = new SaJsonTemplateForJackson3();
		Assertions.assertThrows(RuntimeException.class, () -> template.jsonToObject("{", Object.class));
		Assertions.assertThrows(RuntimeException.class, () -> template.jsonToMap("{"));
	}

	/** 验证 Jackson3 插件仅在默认模板时安装 */
	@Test
	void installsOnlyDefaultJsonTemplate() {
		new SaTokenPluginForJackson3().install();
		Assertions.assertEquals(SaJsonTemplateForJackson3.class, SaManager.getSaJsonTemplate().getClass());
		SaJsonTemplate customTemplate = new SaJsonTemplateForJackson3();
		SaManager.setSaJsonTemplate(customTemplate);
		new SaTokenPluginForJackson3().install();
		Assertions.assertSame(customTemplate, SaManager.getSaJsonTemplate());
	}

}
