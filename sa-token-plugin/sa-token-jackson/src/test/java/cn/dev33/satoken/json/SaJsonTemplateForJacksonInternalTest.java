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
package cn.dev33.satoken.json;

import cn.dev33.satoken.exception.SaJsonConvertException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaJsonTemplateForJackson} 包级静态方法与异常包装内部测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaJsonTemplateForJacksonInternalTest {

	/** 序列化失败（getter 抛异常的 bean）时应该包装为 SaJsonConvertException */
	@Test
	void wrapsSerializeException() {
		Assertions.assertThrows(SaJsonConvertException.class,
				() -> new SaJsonTemplateForJackson().objectToJson(new BadBean()));
	}

	/** 测试用 bean：getter 抛异常，触发序列化失败 */
	public static class BadBean {
		public String getName() {
			throw new IllegalStateException("boom");
		}
	}

	/** message 为 null 时 isWhitelistDenied 应该返回 false（用匿名子类模拟无 message 的异常） */
	@Test
	void isWhitelistDenied_nullMessage() {
		InvalidTypeIdException e = new InvalidTypeIdException(null, "tid", (JavaType) null, null) {
			@Override
			public String getMessage() {
				return null;
			}
		};
		Assertions.assertFalse(SaJsonTemplateForJackson.isWhitelistDenied(e));
	}

	/** message 不含 denied resolution 时 isWhitelistDenied 应该返回 false */
	@Test
	void isWhitelistDenied_otherMessage() {
		InvalidTypeIdException e = new InvalidTypeIdException(null, "tid", null, "some other error");
		Assertions.assertFalse(SaJsonTemplateForJackson.isWhitelistDenied(e));
	}

	/** typeId 为 null 时 toSaJsonConvertException 应该走通用包装，而不是误报白名单提示 */
	@Test
	void toSaJsonConvertException_nullTypeId() {
		InvalidTypeIdException source = new InvalidTypeIdException(null, null, null, "missing type id");
		SaJsonConvertException ex = SaJsonTemplateForJackson.toSaJsonConvertException(source);
		Assertions.assertSame(source, ex.getCause());
	}

	/** typeId 存在但非白名单拒绝（普通类型解析失败）时应该走通用包装，而不是误报白名单提示 */
	@Test
	void toSaJsonConvertException_notWhitelistDenied() {
		InvalidTypeIdException source = new InvalidTypeIdException(null, "com.pj.test.model.NonExistent", null,
				"Could not resolve type id 'com.pj.test.model.NonExistent'");
		SaJsonConvertException ex = SaJsonTemplateForJackson.toSaJsonConvertException(source);
		Assertions.assertSame(source, ex.getCause());
		Assertions.assertFalse(ex.getMessage().contains("JSON 全局类型白名单"));
	}

}
