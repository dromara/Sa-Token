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
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import cn.dev33.satoken.util.SaResult;
/**
 * SaResult 结果集测试
 */
public class SaResultTest {
	/** 构造函数、getter/setter 及 getOrDefault 应正确读写字段 */
	@Test
	public void constructorAndGetSet() {
		SaResult res = new SaResult();
		Assertions.assertNull(res.getCode());
		Assertions.assertNull(res.getMsg());
		Assertions.assertNull(res.getData());
		SaResult res2 = new SaResult(200, "ok", "zhangsan");
		Assertions.assertEquals(200, res2.getCode());
		Assertions.assertEquals("ok", res2.getMsg());
		Assertions.assertEquals("zhangsan", res2.getData());
		res.set("age", 18);
		Assertions.assertEquals(18, res.get("age"));
		Assertions.assertEquals("18", res.get("age", String.class));
		Assertions.assertEquals(18, res.getOrDefault("age", 20));
		Assertions.assertEquals(20, res.getOrDefault("age2", 20));
	}
	/** 静态工厂方法 ok/error/code/get 及 toString 应返回预期结果 */
	@Test
	public void staticFactoryMethods() {
		Assertions.assertEquals(200, SaResult.ok().getCode());
		Assertions.assertEquals(500, SaResult.error().getCode());
		Assertions.assertEquals("错误", SaResult.error("错误").getMsg());
		SaResult res = SaResult.code(201);
		Assertions.assertEquals(201, res.getCode());
		SaResult res2 = SaResult.get(200, "ok", "zhangsan");
		Assertions.assertEquals(200, res2.getCode());
		Assertions.assertEquals("ok", res2.getMsg());
		Assertions.assertEquals("zhangsan", res2.getData());
		Assertions.assertEquals("{\"code\": 200, \"msg\": \"ok\", \"data\": \"zhangsan\"}", res2.toString());
		res2.setData(1);
		Assertions.assertEquals("{\"code\": 200, \"msg\": \"ok\", \"data\": 1}", res2.toString());
		Map<String, Object> map = new HashMap<>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		SaResult res4 = new SaResult(map);
		Assertions.assertEquals("value1", res4.get("key1"));
		Assertions.assertEquals("value2", res4.get("key2"));
	}
}
