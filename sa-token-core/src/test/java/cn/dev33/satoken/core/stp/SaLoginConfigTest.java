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
package cn.dev33.satoken.core.stp;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

/**
 * SaLoginConfig 快捷构建登录参数测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaLoginConfigTest {

	/** 各 setXxx 静态方法应返回正确配置的 SaLoginParameter */
	@Test
	void setters_buildSaLoginParameter() {
		Assertions.assertEquals("PC", SaLoginConfig.setDevice("PC").getDeviceType());
		Assertions.assertTrue(SaLoginConfig.setIsLastingCookie(true).getIsLastingCookie());
		Assertions.assertEquals(3600L, SaLoginConfig.setTimeout(3600).getTimeout());
		Assertions.assertEquals(60L, SaLoginConfig.setActiveTimeout(60).getActiveTimeout());
		Map<String, Object> extraData = Collections.singletonMap("a", 1);
		Assertions.assertEquals(extraData, SaLoginConfig.setExtraData(extraData).getExtraData());
		Assertions.assertEquals("preset-token", SaLoginConfig.setToken("preset-token").getToken());
		Assertions.assertEquals("v", SaLoginConfig.setExtra("k", "v").getExtra("k"));
		Assertions.assertTrue(SaLoginConfig.setIsWriteHeader(true).getIsWriteHeader());
		Map<String, Object> tag = Collections.singletonMap("tag", "1");
		Assertions.assertEquals(tag, SaLoginConfig.setTokenSignTag(tag).getTerminalExtraData());
		Assertions.assertNotNull(SaLoginConfig.create());
	}

	/** create 应基于全局配置构造 SaLoginParameter */
	@Test
	void create_usesGlobalConfig() {
		SaLoginParameter param = SaLoginConfig.create();
		Assertions.assertNotNull(param);
		Assertions.assertInstanceOf(SaLoginParameter.class, param);
	}

}
