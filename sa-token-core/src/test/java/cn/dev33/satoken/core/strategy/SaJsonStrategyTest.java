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
package cn.dev33.satoken.core.strategy;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.json.SaJsonType;
import cn.dev33.satoken.strategy.SaJsonStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * SaJsonStrategy 多态 JSON 类型白名单策略测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaJsonStrategyTest {

	private final SaJsonStrategy strategy = SaJsonStrategy.instance;

	@BeforeEach
	void setUp() {
		strategy.resetState();
	}

	@AfterEach
	void tearDown() {
		strategy.resetState();
	}

	/** 初始化前 registerAllowType 应加入自定义白名单但不触发 init */
	@Test
	void registerAllowType_beforeInit() {
		Assertions.assertFalse(strategy.isInit());
		strategy.registerAllowType(LinkedList.class);
		Assertions.assertFalse(strategy.isInit());
		Assertions.assertTrue(strategy.getCustomAllowTypeList().contains(LinkedList.class));
	}

	/** 初始化后 registerAllowType 应抛出 SaTokenException */
	@Test
	void registerAllowType_afterInit_throws() {
		strategy.registerAllowType(LinkedList.class);
		strategy.getSaJsonAllowTypeList();
		Assertions.assertTrue(strategy.isInit());
		Assertions.assertThrows(SaTokenException.class, () -> strategy.registerAllowType(Map.class));
	}

	/** JDK 白名单应包含 Map、Date 等常用类型 */
	@Test
	void getJdkAllowTypeList_containsCommonTypes() {
		List<Class<?>> jdkTypes = strategy.getJdkAllowTypeList();
		Assertions.assertTrue(jdkTypes.contains(Map.class));
		Assertions.assertTrue(jdkTypes.contains(java.util.Date.class));
		Assertions.assertTrue(jdkTypes.contains(java.time.LocalDateTime.class));
		Assertions.assertTrue(jdkTypes.contains(java.math.BigDecimal.class));
	}

	/** getSaJsonAllowTypeList 应合并 JDK、SPI 与自定义类型 */
	@Test
	void getSaJsonAllowTypeList_mergesTypes() {
		strategy.registerAllowType(LinkedList.class);
		List<Class<?>> merged = strategy.getSaJsonAllowTypeList();

		Assertions.assertTrue(strategy.isInit());
		Assertions.assertTrue(merged.contains(Map.class));
		Assertions.assertTrue(merged.contains(SaJsonType.class));
		Assertions.assertTrue(merged.contains(LinkedList.class));
		Assertions.assertNotSame(merged, strategy.getSaJsonAllowTypeList());
	}

	/** registerAllowType 传入 null 应抛出 SaTokenException */
	@Test
	void registerAllowType_nullType_throws() {
		Assertions.assertThrows(SaTokenException.class, () -> strategy.registerAllowType(null));
	}

	/** 注册 Object.class 时应打印安全警告并仍加入白名单 */
	@Test
	void registerAllowType_objectClass_printsWarning() {
		PrintStream originalErr = System.err;
		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(errContent));
		try {
			strategy.registerAllowType(Object.class);
			String output = errContent.toString();
			Assertions.assertTrue(output.contains("[Sa-Token 安全警告]"));
			Assertions.assertTrue(output.contains("registerAllowType(Object.class)"));
			Assertions.assertTrue(strategy.getCustomAllowTypeList().contains(Object.class));
		} finally {
			System.setErr(originalErr);
		}
	}

}
