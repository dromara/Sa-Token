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

import cn.dev33.satoken.util.SaValue2Box;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaValue2Box 双值容器测试
 */
public class SaValue2BoxTest {

	/** 构造函数及 getter 应正确返回两个值及其字符串、Long 形式 */
	@Test
	void constructorAndGetters() {
		SaValue2Box box = new SaValue2Box(123, "abc");
		Assertions.assertEquals(123, box.getValue1());
		Assertions.assertEquals("abc", box.getValue2());
		Assertions.assertEquals("123", box.getValue1AsString());
		Assertions.assertEquals("abc", box.getValue2AsString());
		Assertions.assertEquals(123L, box.getValue1AsLong());
	}

	/** 从逗号分隔字符串解析应正确拆分两个值 */
	@Test
	void parseFromString() {
		SaValue2Box box = new SaValue2Box("100,200");
		Assertions.assertEquals("100", box.getValue1AsString());
		Assertions.assertEquals("200", box.getValue2AsString());
		Assertions.assertEquals(100L, box.getValue1AsLong());
		Assertions.assertEquals(200L, box.getValue2AsLong());
	}

	/** isNotValueState/isSingleValueState/isDoubleValueState 及 isEmpty 判断应正确 */
	@Test
	void valueState() {
		SaValue2Box empty = new SaValue2Box((String) null);
		Assertions.assertTrue(empty.isNotValueState());

		SaValue2Box single = new SaValue2Box("only", null);
		Assertions.assertTrue(single.isSingleValueState());
		Assertions.assertFalse(single.isDoubleValueState());

		SaValue2Box pair = new SaValue2Box(null, "two");
		Assertions.assertTrue(pair.isDoubleValueState());
		Assertions.assertTrue(pair.value1IsEmpty());
		Assertions.assertFalse(pair.value2IsEmpty());
	}

	/** toString 应按值状态格式化为逗号分隔字符串或 null */
	@Test
	void toString_formatsValues() {
		Assertions.assertEquals("1,2", new SaValue2Box(1, 2).toString());
		Assertions.assertNull(new SaValue2Box(null, null).toString());
		Assertions.assertEquals("1", new SaValue2Box(1, null).toString());
		Assertions.assertEquals(",2", new SaValue2Box(null, 2).toString());
	}

	/** getValueAsLong 缺省值应在值为空时返回默认值 */
	@Test
	void getValueAsLongWithDefault() {
		SaValue2Box box = new SaValue2Box("10,20");
		Assertions.assertEquals(10L, box.getValue1AsLong(99L));
		Assertions.assertEquals(20L, box.getValue2AsLong(99L));

		SaValue2Box empty = new SaValue2Box((String) null);
		Assertions.assertEquals(99L, empty.getValue1AsLong(99L));
		Assertions.assertEquals(88L, empty.getValue2AsLong(88L));
	}

}
