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
import cn.dev33.satoken.strategy.SaJsonStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * SaJsonStrategy 剩余覆盖率补充测试
 */
public class SaJsonStrategyExtendedTest {

	private final SaJsonStrategy strategy = SaJsonStrategy.instance;

	@BeforeEach
	void setUp() {
		strategy.resetState();
	}

	@AfterEach
	void tearDown() {
		strategy.resetState();
	}

	/** loadSpiAllowTypeList 应读取测试资源中的 SPI 类型 */
	@Test
	void loadSpiAllowTypeList_readsTestResource() {
		List<Class<?>> spiTypes = strategy.loadSpiAllowTypeList();
		Assertions.assertTrue(spiTypes.contains(SpiAllowType.class));
	}

	/** 合并白名单应包含 SPI 加载的类型 */
	@Test
	void getSaJsonAllowTypeList_includesSpiTypes() {
		List<Class<?>> merged = strategy.getSaJsonAllowTypeList();
		Assertions.assertTrue(merged.contains(SpiAllowType.class));
		Assertions.assertTrue(strategy.isInit());
	}

	/** resetState 后应允许再次 registerAllowType */
	@Test
	void resetState_allowsRegisterAgain() {
		strategy.getSaJsonAllowTypeList();
		Assertions.assertTrue(strategy.isInit());
		strategy.resetState();
		Assertions.assertFalse(strategy.isInit());
		strategy.registerAllowType(SpiAllowType.class);
		Assertions.assertTrue(strategy.getCustomAllowTypeList().contains(SpiAllowType.class));
	}

	/** JDK 白名单应包含 Enum 类型 */
	@Test
	void getJdkAllowTypeList_containsEnumType() {
		List<Class<?>> jdkTypes = strategy.getJdkAllowTypeList();
		Assertions.assertTrue(jdkTypes.contains(Enum.class));
	}

	/** getSaJsonTypeMarkerList 应返回 SaJsonType 标记类 */
	@Test
	void getSaJsonTypeMarkerList_returnsSaJsonTypeMarker() {
		List<Class<?>> markers = strategy.getSaJsonTypeMarkerList();
		Assertions.assertEquals(1, markers.size());
		Assertions.assertEquals(markers, strategy.getSaJsonTypeMarkerList());
	}

	public static class SpiAllowType {
	}

}
