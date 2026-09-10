package com.pj.test.json;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.plugin.SaTokenPlugin;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaJsonStrategy;
import cn.dev33.satoken.strategy.SaStrategy;
import com.pj.test.model.SysUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SaJsonTemplate 各插件实现共享的测试支持。
 *
 * @author click33
 */
public abstract class SaJsonTemplateTestCommon {

	/** 重置全局 JSON 策略，供需要类型白名单的插件测试初始化使用 */
	protected void resetJsonStrategy() {
		SaJsonStrategy.instance.resetState();
	}

	/** 每个插件测试结束后清理全局 JSON 策略状态 */
	@AfterEach
	protected void resetJsonStrategyAfterEach() {
		SaManager.setSaJsonTemplate(new SaJsonTemplateDefaultImpl());
		SaStrategy.instance.createSession = SaSession::new;
		SaStrategy.instance.sessionClassType = SaSession.class;
		resetJsonStrategy();
	}

	/** 验证模板注册、对象往返、空值和 Map 转换的基础契约 */
	protected void assertTemplate(Class<? extends SaJsonTemplate> expectedType, SaJsonTemplate template,
			String expectedUserJson, boolean checkUntypedConversion, boolean checkMapKeyOrder) {
		SaManager.setSaJsonTemplate(template);
		Assertions.assertEquals(expectedType, SaManager.getSaJsonTemplate().getClass());
		assertUserRoundTrip(template, expectedUserJson, checkUntypedConversion);
		assertNullConversions(template);
		assertMapRoundTrip(template, checkMapKeyOrder);
	}

	/** 验证用户对象按指定的类型信息策略完成序列化和反序列化 */
	protected void assertUserRoundTrip(SaJsonTemplate template, String expectedUserJson, boolean checkUntypedConversion) {
		SysUser user = new SysUser(10001, "张三", 18);
		String json = template.objectToJson(user);
		if(expectedUserJson != null) {
			Assertions.assertEquals(expectedUserJson, json);
		}
		Assertions.assertEquals(user.toString(), template.jsonToObject(json, SysUser.class).toString());
		if(checkUntypedConversion) {
			Assertions.assertEquals(user.toString(), ((SysUser) template.jsonToObject(json)).toString());
		}
	}

	/** 验证四个 JSON 转换入口对 null 的统一处理 */
	protected void assertNullConversions(SaJsonTemplate template) {
		Assertions.assertNull(template.objectToJson(null));
		Assertions.assertNull(template.jsonToObject(null, SysUser.class));
		Assertions.assertNull(template.jsonToObject(null));
		Assertions.assertNull(template.jsonToMap(null));
	}

	/** 验证空字符串在所有 JSON 入口中按空值处理 */
	protected void assertEmptyStringConversions(SaJsonTemplate template) {
		Assertions.assertNull(template.objectToJson(""));
		Assertions.assertNull(template.jsonToObject("", SysUser.class));
		Assertions.assertNull(template.jsonToObject(""));
		Assertions.assertNull(template.jsonToMap(""));
	}

	/** 验证 Map 往返；需要时同时校验实现定义的字段顺序 */
	protected void assertMapRoundTrip(SaJsonTemplate template, boolean checkMapKeyOrder) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", 10001);
		map.put("name", "张三");
		map.put("age", 18);
		String json = template.objectToJson(map);
		if(checkMapKeyOrder) {
			Assertions.assertEquals("{\"name\":\"张三\",\"id\":10001,\"age\":18}", json);
		}
		Assertions.assertEquals(map.toString(), template.jsonToMap(json).toString());
	}

	/** 验证 Session 中常见 JDK 类型的往返，严格模式要求保持原始类型 */
	protected void assertCommonSessionTypes(SaJsonTemplate template, boolean strictTypes) {
		SaManager.setSaJsonTemplate(template);
		Date time = new Date(1234567890000L);
		BigDecimal amount = new BigDecimal("99.50");
		LocalDateTime loginTime = LocalDateTime.of(2026, 8, 15, 12, 30);
		SaSession session = new SaSession("test-session");
		session.set("time", time);
		session.set("amount", amount);
		session.set("loginTime", loginTime);
		SaSession session2 = template.jsonToObject(template.objectToJson(session), SaSession.class);
		if(strictTypes) {
			Assertions.assertEquals(time, session2.get("time"));
			Assertions.assertEquals(amount, session2.get("amount"));
			Assertions.assertEquals(loginTime, session2.get("loginTime"));
			return;
		}
		Object timeValue = session2.get("time");
		if(timeValue instanceof Date) {
			Assertions.assertEquals(time, timeValue);
		} else {
			Assertions.assertEquals(time.getTime(), ((Number) timeValue).longValue());
		}
		Assertions.assertEquals(0, amount.compareTo(new BigDecimal(session2.get("amount").toString())));
		Object loginTimeValue = session2.get("loginTime");
		if(loginTimeValue instanceof LocalDateTime) {
			Assertions.assertEquals(loginTime, loginTimeValue);
		} else {
			Assertions.assertNotNull(loginTimeValue);
		}
	}

	/** 验证 Session Map 中的 Long 等包装类型可按原类型读回 */
	protected void assertWrapperTypesInSessionMap(SaJsonTemplate template) {
		SaManager.setSaJsonTemplate(template);
		Map<String, Long> tokenIndexMap = new java.util.LinkedHashMap<>();
		tokenIndexMap.put("access-token-value", 1786860583046L);
		SaSession session = new SaSession("oauth2-raw-session");
		session.set("__HD_ACCESS_TOKEN_MAP", tokenIndexMap);
		SaSession session2 = template.jsonToObject(template.objectToJson(session), SaSession.class);
		@SuppressWarnings("unchecked")
		Map<String, Long> map = (Map<String, Long>) session2.get("__HD_ACCESS_TOKEN_MAP");
		Assertions.assertEquals(1786860583046L, map.get("access-token-value"));
	}

	/** 验证未进入白名单的危险类型会被拒绝 */
	protected void assertBlocksUnknownAllowType(SaJsonTemplate template, String json) {
		SaManager.setSaJsonTemplate(template);
		SaJsonConvertException ex = Assertions.assertThrows(SaJsonConvertException.class, () -> template.jsonToObject(json));
		Assertions.assertEquals("无法反序列化的类型：java.lang.ProcessBuilder，请先将其注册到 JSON 全局类型白名单", ex.getMessage());
	}

	/** 验证不存在的类型保留库原始异常信息而非误报白名单 */
	protected void assertClassNotFoundMessage(SaJsonTemplate template, String json, String expectedMessage) {
		SaManager.setSaJsonTemplate(template);
		SaJsonConvertException ex = Assertions.assertThrows(SaJsonConvertException.class, () -> template.jsonToObject(json));
		Assertions.assertTrue(ex.getMessage().contains(expectedMessage));
		Assertions.assertFalse(ex.getMessage().contains("JSON 全局类型白名单"));
	}

	/** 验证 JSON 策略初始化后禁止继续变更类型白名单 */
	protected void assertStrategyCannotBeChangedAfterInitialization(SaJsonTemplate template) {
		SaManager.setSaJsonTemplate(template);
		Assertions.assertThrows(SaTokenException.class, () -> SaJsonStrategy.instance.registerAllowType(String.class));
	}

	/** 验证安装插件后替换 JSON 模板和 Session 创建策略 */
	protected void assertPluginInstall(SaTokenPlugin plugin, Class<? extends SaJsonTemplate> templateType,
			Class<? extends SaSession> sessionType) {
		plugin.install();
		Assertions.assertEquals(templateType, SaManager.getSaJsonTemplate().getClass());
		Assertions.assertEquals(sessionType, SaStrategy.instance.sessionClassType);
		Assertions.assertEquals(sessionType, SaStrategy.instance.createSession.apply("plugin-session").getClass());
	}

}
