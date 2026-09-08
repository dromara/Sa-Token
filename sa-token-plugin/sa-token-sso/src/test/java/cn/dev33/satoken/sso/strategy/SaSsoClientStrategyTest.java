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
package cn.dev33.satoken.sso.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.http.SaHttpTemplate;
import cn.dev33.satoken.sso.support.StubSaJsonTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * Client 策略默认 lambda：身份转换、ticketResultHandle、发请求
 */
@SaTokenTest
public class SaSsoClientStrategyTest {

	/** 默认 convert 应该原样返回 */
	@Test
	public void convert_defaultIdentity() {
		SaSsoClientStrategy s = new SaSsoClientStrategy();
		Assertions.assertEquals(10001, s.convertCenterIdToLoginId.run(10001));
		Assertions.assertEquals("u", s.convertLoginIdToCenterId.run("u"));
	}

	/** ticketResultHandle 默认就是 null */
	@Test
	public void ticketResultHandle_defaultNull() {
		Assertions.assertNull(new SaSsoClientStrategy().ticketResultHandle);
	}

	/** 默认 sendRequest 走默认 HttpTemplate 时应该抛未实现 */
	@Test
	public void sendRequest_defaultThrowsNotImpl() {
		SaManager.setSaHttpTemplate(new cn.dev33.satoken.http.SaHttpTemplateDefaultImpl());
		Assertions.assertThrows(NotImplException.class,
				() -> new SaSsoClientStrategy().sendRequest.apply("http://x"));
	}

	/** requestAsSaResult 在 stub 了 json 和 http 后应该解析出 code */
	@Test
	public void requestAsSaResult_parsesStubJson() {
		SaManager.setSaJsonTemplate(new StubSaJsonTemplate());
		SaManager.setSaHttpTemplate(new SaHttpTemplate() {
			@Override
			public String get(String url) {
				return "{\"code\":500,\"msg\":\"fail\"}";
			}
			@Override
			public String postByFormData(String url, Map<String, Object> params) {
				return "";
			}
		});
		SaResult r = new SaSsoClientStrategy().requestAsSaResult("http://x");
		Assertions.assertEquals(500, r.getCode());
		Assertions.assertEquals("fail", r.getMsg());
	}

}
