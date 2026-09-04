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
package cn.dev33.satoken.core.application;

import cn.dev33.satoken.application.SaApplication;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SaGetValueInterface 默认方法测试（经 SaApplication）
 */
@SaTokenTest
public class SaApplicationGetValueInterfaceTest {

	@AfterEach
	void cleanup() {
		SaHolder.getApplication().clear();
	}

	/** getList/getSet/getMap 与 has 默认方法应正常工作 */
	@Test
	void getListSetMapAndHas() {
		SaApplication app = SaHolder.getApplication();
		app.set("tags", Arrays.asList("a", "b"));
		app.set("roles", new LinkedHashSet<>(Arrays.asList("admin", "user")));
		Map<String, Object> scoreMap = new HashMap<>();
		scoreMap.put("math", 90);
		scoreMap.put("eng", 85);
		app.set("scores", scoreMap);

		List<String> tags = app.getList("tags", String.class);
		Assertions.assertEquals(Arrays.asList("a", "b"), tags);

		Set<String> roles = app.getSet("roles", String.class);
		Assertions.assertEquals(new LinkedHashSet<>(Arrays.asList("admin", "user")), roles);

		Map<String, Integer> scores = app.getMap("scores", String.class, Integer.class);
		Assertions.assertEquals(90, scores.get("math"));
		Assertions.assertEquals(85, scores.get("eng"));

		Assertions.assertTrue(app.has("tags"));
		Assertions.assertFalse(app.has("missing"));
		Assertions.assertTrue(app.getList("empty-list", String.class).isEmpty());
		Assertions.assertTrue(app.getSet("empty-set", String.class).isEmpty());
		Assertions.assertTrue(app.getMap("empty-map", String.class, Integer.class).isEmpty());
	}

	/** 类型不匹配时 getList/getSet/getMap 应抛出异常 */
	@Test
	void getListSetMap_wrongTypeThrows() {
		SaApplication app = SaHolder.getApplication();
		app.set("notCollection", "plain-string");
		app.set("notMap", Arrays.asList("a"));

		Assertions.assertThrows(SaTokenException.class, () -> app.getList("notCollection", String.class));
		Assertions.assertThrows(SaTokenException.class, () -> app.getSet("notCollection", String.class));
		Assertions.assertThrows(SaTokenException.class, () -> app.getMap("notMap", String.class, Integer.class));
	}

}
