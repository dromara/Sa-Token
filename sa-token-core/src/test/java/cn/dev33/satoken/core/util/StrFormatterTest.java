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
package cn.dev33.satoken.core.util;

import cn.dev33.satoken.util.StrFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * StrFormatter 字符串格式化测试
 */
public class StrFormatterTest {

	/** format 应正确替换 {} 占位符 */
	@Test
	void format_replacesPlaceholders() {
		Assertions.assertEquals("this is a for b", StrFormatter.format("this is {} for {}", "a", "b"));
		Assertions.assertEquals("hello world", StrFormatter.format("hello {}", "world"));
	}

	/** format 应支持转义占位符 \\{} 与 \\\\{} */
	@Test
	void format_escapePlaceholder() {
		Assertions.assertEquals("this is {} for a", StrFormatter.format("this is \\{} for {}", "a", "b"));
		Assertions.assertEquals("this is \\a for b", StrFormatter.format("this is \\\\{} for {}", "a", "b"));
	}

	/** formatWith 应使用自定义占位符进行格式化 */
	@Test
	void formatWith_customPlaceholder() {
		Assertions.assertEquals("this is a for b",
				StrFormatter.formatWith("this is {} for {}", "{}", "a", "b"));
	}

	/** format 在无参数、null 或空模板时应返回预期结果 */
	@Test
	void format_emptyPatternOrArgs() {
		Assertions.assertEquals("plain text", StrFormatter.format("plain text"));
		Assertions.assertNull(StrFormatter.format(null, "a"));
		Assertions.assertEquals("", StrFormatter.format("", "a"));
	}

}

