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
package cn.dev33.satoken.core.context.model;

import cn.dev33.satoken.context.model.SaCookie;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SaCookie 全字段与边界测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaCookieExtendedTest {

	/** 默认构造与链式 setter 应正确设置各字段 */
	@Test
	void defaultConstructorAndChainSetters() {
		SaCookie cookie = new SaCookie();
		Assertions.assertNull(cookie.getName());
		Assertions.assertNull(cookie.getValue());
		Assertions.assertEquals(-1, cookie.getMaxAge());
		Assertions.assertEquals(false, cookie.getSecure());
		Assertions.assertEquals(false, cookie.getHttpOnly());
		Assertions.assertNotNull(cookie.getExtraAttrs());

		SaCookie chained = cookie
				.setName("n")
				.setValue("v")
				.setMaxAge(3600)
				.setDomain("example.com")
				.setPath("/app")
				.setSecure(true)
				.setHttpOnly(true)
				.setSameSite("Strict");
		Assertions.assertSame(cookie, chained);
		Assertions.assertEquals("n", cookie.getName());
		Assertions.assertEquals("v", cookie.getValue());
		Assertions.assertEquals(3600, cookie.getMaxAge());
		Assertions.assertEquals("example.com", cookie.getDomain());
		Assertions.assertEquals("/app", cookie.getPath());
		Assertions.assertEquals(true, cookie.getSecure());
		Assertions.assertEquals(true, cookie.getHttpOnly());
		Assertions.assertEquals("Strict", cookie.getSameSite());
	}

	/** extraAttrs 的增删改应正常工作 */
	@Test
	void extraAttrsOperations() {
		SaCookie cookie = new SaCookie("k", "v");
		cookie.addExtraAttr("Partitioned");
		cookie.addExtraAttr("attr", "val");
		Assertions.assertTrue(cookie.getExtraAttrs().containsKey("Partitioned"));
		Assertions.assertEquals("val", cookie.getExtraAttrs().get("attr"));

		Map<String, String> custom = new LinkedHashMap<>();
		custom.put("a", "1");
		cookie.setExtraAttrs(custom);
		Assertions.assertSame(custom, cookie.getExtraAttrs());

		cookie.removeExtraAttr("a");
		Assertions.assertFalse(cookie.getExtraAttrs().containsKey("a"));
	}

	/** builder 应设置默认 Path 为 / */
	@Test
	void builderSetsDefaultPath() {
		SaCookie cookie = new SaCookie("k", "v");
		cookie.builder();
		Assertions.assertEquals("/", cookie.getPath());
	}

	/** toHeaderValue 应包含 MaxAge 与扩展属性 */
	@Test
	void toHeaderValue_withMaxAgeAndExtraAttrs() {
		SaCookie cookie = new SaCookie("token", "abc")
				.setMaxAge(100)
				.setDomain("localhost")
				.setSecure(true)
				.setHttpOnly(true)
				.setSameSite("Lax")
				.addExtraAttr("Partitioned");

		String header = cookie.toHeaderValue();
		Assertions.assertTrue(header.startsWith("token=abc; Max-Age=100; Expires="));
		Assertions.assertTrue(header.contains("; Domain=localhost"));
		Assertions.assertTrue(header.contains("; Path=/"));
		Assertions.assertTrue(header.contains("; Secure"));
		Assertions.assertTrue(header.contains("; HttpOnly"));
		Assertions.assertTrue(header.contains("; SameSite=Lax"));
		Assertions.assertTrue(header.contains("; Partitioned"));
	}

	/** MaxAge 为 0 时 Expires 应为 Epoch 时间 */
	@Test
	void toHeaderValue_maxAgeZeroUsesEpochExpires() {
		SaCookie cookie = new SaCookie("token", "abc").setMaxAge(0);
		String header = cookie.toHeaderValue();
		Assertions.assertTrue(header.contains("; Max-Age=0"));
		Assertions.assertTrue(header.contains("; Expires="));
		Assertions.assertTrue(header.contains("1970"));
	}

	/** 非法 name 或 value 时 toHeaderValue 应抛出异常 */
	@Test
	void toHeaderValue_invalidNameOrValue() {
		SaTokenException emptyName = Assertions.assertThrows(SaTokenException.class,
				() -> new SaCookie("", "v").toHeaderValue());
		Assertions.assertEquals(SaErrorCode.CODE_12002, emptyName.getCode());

		SaTokenException invalidValue = Assertions.assertThrows(SaTokenException.class,
				() -> new SaCookie("k", "a;b").toHeaderValue());
		Assertions.assertEquals(SaErrorCode.CODE_12003, invalidValue.getCode());
	}

	/** HEADER_NAME 常量应为 Set-Cookie */
	@Test
	void headerNameConstant() {
		Assertions.assertEquals("Set-Cookie", SaCookie.HEADER_NAME);
	}

}
