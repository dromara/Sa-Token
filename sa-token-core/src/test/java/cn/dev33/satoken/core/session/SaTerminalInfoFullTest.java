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
package cn.dev33.satoken.core.session;

import cn.dev33.satoken.session.SaTerminalInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SaTerminalInfo getter/setter 与扩展数据测试
 */
public class SaTerminalInfoFullTest {

	/** 构造函数与链式 setter 应正确读写各字段及扩展数据 */
	@Test
	void constructorAndFluentSetters() {
		Map<String, Object> extra = new LinkedHashMap<>();
		extra.put("k", "v");
		SaTerminalInfo terminal = new SaTerminalInfo(2, "token-x", "APP", extra);

		Assertions.assertEquals(2, terminal.getIndex());
		Assertions.assertEquals("token-x", terminal.getTokenValue());
		Assertions.assertEquals("APP", terminal.getDeviceType());
		Assertions.assertEquals("v", terminal.getExtra("k"));
		Assertions.assertTrue(terminal.haveExtraData());
		Assertions.assertTrue(terminal.getCreateTime() > 0);

		terminal.setIndex(3)
				.setTokenValue("token-y")
				.setDeviceType("PC")
				.setDeviceId("device-99")
				.setCreateTime(123456789L)
				.setExtraData(null);
		Assertions.assertEquals(3, terminal.getIndex());
		Assertions.assertEquals("token-y", terminal.getTokenValue());
		Assertions.assertEquals("PC", terminal.getDeviceType());
		Assertions.assertEquals("device-99", terminal.getDeviceId());
		Assertions.assertEquals(123456789L, terminal.getCreateTime());
		Assertions.assertFalse(terminal.haveExtraData());
		Assertions.assertNull(terminal.getExtra("k"));
	}

	/** setExtra 在无扩展数据时应懒创建 Map 并写入键值 */
	@Test
	void setExtra_lazyCreatesMap() {
		SaTerminalInfo terminal = new SaTerminalInfo();
		terminal.setExtra("role", "admin");
		Assertions.assertEquals("admin", terminal.getExtra("role"));
		Assertions.assertTrue(terminal.haveExtraData());
		Assertions.assertNotNull(terminal.getExtraData());
		Assertions.assertTrue(terminal.toString().contains("deviceId"));
	}

}
