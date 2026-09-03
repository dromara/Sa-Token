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
package cn.dev33.satoken.plugin;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SSO Client {@code ssoLogout}：向认证中心推送 signout，并转换 centerId
 */
public class SaSsoClientTemplateSsoLogoutTest {

	private SaSsoClientConfig backupClientConfig;
	private SaJsonTemplate backupJsonTemplate;
	private SaSsoClientTemplate template;

	@BeforeEach
	public void setup() {
		backupClientConfig = SaSsoManager.getClientConfig();
		backupJsonTemplate = SaManager.getSaJsonTemplate();
		SaSsoManager.setClientConfig(new SaSsoClientConfig()
				.setClient("sso-client3")
				.setServerUrl("http://sso-server.com")
				.setSecretKey("test-secret"));
		SaManager.setSaJsonTemplate(new StubSaJsonTemplate());
		template = new SaSsoClientTemplate();
	}

	@AfterEach
	public void restore() {
		SaSsoManager.setClientConfig(backupClientConfig);
		SaManager.setSaJsonTemplate(backupJsonTemplate);
	}

	@Test
	public void pushSignout_withLocalLoginId() {
		AtomicReference<String> pushedUrl = new AtomicReference<>();
		template.strategy.sendRequest = url -> {
			pushedUrl.set(url);
			return "{\"code\":200,\"msg\":\"ok\"}";
		};

		template.ssoLogout(10001);

		Assertions.assertTrue(pushedUrl.get().contains("msgType=" + SaSsoConsts.MESSAGE_SIGNOUT));
		Assertions.assertTrue(pushedUrl.get().contains("loginId=10001"));
		Assertions.assertTrue(pushedUrl.get().contains("client=sso-client3"));
	}

	@Test
	public void convertLocalLoginIdToCenterId_beforePush() {
		AtomicReference<String> pushedUrl = new AtomicReference<>();
		template.strategy.convertLoginIdToCenterId = loginId -> loginId.toString().substring(3);
		template.strategy.sendRequest = url -> {
			pushedUrl.set(url);
			return "{\"code\":200,\"msg\":\"ok\"}";
		};

		template.ssoLogout("Stu10002");

		Assertions.assertTrue(pushedUrl.get().contains("loginId=10002"));
		Assertions.assertFalse(pushedUrl.get().contains("loginId=Stu10002"));
	}

	@Test
	public void throw30006_whenServerReject() {
		template.strategy.sendRequest = url -> "{\"code\":500,\"msg\":\"注销失败\"}";

		SaSsoException e = Assertions.assertThrows(SaSsoException.class, () -> template.ssoLogout(10001));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30006, e.getCode());
		Assertions.assertEquals("注销失败", e.getMessage());
	}

	/** 只解析本测试用到的 {"code":n,"msg":"..."}，避免依赖 Spring 注入的 JSON 实现 */
	private static class StubSaJsonTemplate implements SaJsonTemplate {
		@Override
		public String objectToJson(Object obj) {
			return null;
		}
		@Override
		public <T> T jsonToObject(String jsonStr, Class<T> type) {
			return null;
		}
		@Override
		public Map<String, Object> jsonToMap(String jsonStr) {
			Map<String, Object> map = new LinkedHashMap<>();
			if(jsonStr.contains("\"code\":200")) {
				map.put("code", 200);
			} else {
				map.put("code", 500);
			}
			int start = jsonStr.indexOf("\"msg\":\"") + 7;
			map.put("msg", jsonStr.substring(start, jsonStr.indexOf('"', start)));
			return map;
		}
	}

}
