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
package cn.dev33.satoken.integration.dubbo.httptest;

import cn.dev33.satoken.integration.dubbo.IntegrationDubboApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Dubbo 2.x 真 RPC：本机 dubbo://，会话下传 / 回传，Same-Token 开着。
 */
@SpringBootTest(classes = IntegrationDubboApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DubboHttpIntegrationTest {

	@Autowired
	TestRestTemplate rest;

	/** Consumer 登录后再 RPC，Provider 应该已经是登录态 */
	@Test
	public void consumerLogin_reachesProvider() {
		Map<?, ?> body = getOk("/consumer-login-then-rpc");
		Map<?, ?> data = (Map<?, ?>) body.get("data");
		Assertions.assertEquals(Boolean.TRUE, data.get("login"));
		Assertions.assertEquals("10001", String.valueOf(data.get("loginId")));
		Assertions.assertNotNull(data.get("token"));
	}

	/** Provider 登录后，Consumer 应该能拿到回传 token */
	@Test
	public void providerLogin_writesBackToConsumer() {
		Map<?, ?> body = getOk("/provider-login-then-check");
		Assertions.assertEquals(Boolean.TRUE, body.get("consumerLogin"));
		Assertions.assertEquals("10002", String.valueOf(body.get("consumerLoginId")));
		Assertions.assertNotNull(body.get("consumerToken"));
	}

	/** 没登录直接 RPC，Provider 不应该是登录态 */
	@Test
	public void noLogin_providerStillAnonymous() {
		Map<?, ?> body = getOk("/rpc-without-login");
		Map<?, ?> data = (Map<?, ?>) body.get("data");
		Assertions.assertEquals(Boolean.FALSE, data.get("login"));
		Assertions.assertNull(data.get("loginId"));
	}

	/** Consumer 登录后 RPC，自己这边会话还在 */
	@Test
	public void consumerLogin_staysOnConsumer() {
		Map<?, ?> body = getOk("/consumer-still-login");
		Assertions.assertEquals(Boolean.TRUE, body.get("consumerLogin"));
		Assertions.assertEquals("10003", String.valueOf(body.get("consumerLoginId")));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getOk(String path) {
		ResponseEntity<Map> resp = rest.getForEntity(path, Map.class);
		Assertions.assertEquals(200, resp.getStatusCodeValue());
		Map<String, Object> body = resp.getBody();
		Assertions.assertNotNull(body);
		Assertions.assertEquals(200, ((Number) body.get("code")).intValue());
		return body;
	}

}
