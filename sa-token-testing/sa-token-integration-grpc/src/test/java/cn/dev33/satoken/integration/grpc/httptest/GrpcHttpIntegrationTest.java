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
package cn.dev33.satoken.integration.grpc.httptest;

import cn.dev33.satoken.integration.grpc.IntegrationGrpcApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * gRPC 真 RPC：本机端口，Same-Token 开着。Provider 业务方法目前会因为上下文被提前清掉而 UNKNOWN。
 */
@SpringBootTest(classes = IntegrationGrpcApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GrpcHttpIntegrationTest {

	@Autowired
	TestRestTemplate rest;

	/** Consumer 登录后再 RPC，Provider 业务方法目前会报上下文尚未初始化 */
	@Test
	public void consumerLogin_currentlyProviderContextMissing() {
		Map<String, Object> body = getRpcUnknown("/consumer-login-then-rpc");
		Assertions.assertEquals(500, ((Number) body.get("code")).intValue());
	}

	/** Provider 登录目前也走不到业务方法，Consumer 拿不到回传 token */
	@Test
	public void providerLogin_currentlyProviderContextMissing() {
		Map<String, Object> body = getRpcUnknown("/provider-login-then-check");
		Assertions.assertEquals(500, ((Number) body.get("code")).intValue());
	}

	/** 没登录直接 RPC，Provider 读会话一样会炸 */
	@Test
	public void noLogin_currentlyProviderContextMissing() {
		Map<String, Object> body = getRpcUnknown("/rpc-without-login");
		Assertions.assertEquals(500, ((Number) body.get("code")).intValue());
	}

	/** Consumer 登录后就算 RPC 炸了，自己这边会话还在 */
	@Test
	public void consumerLogin_staysOnConsumer_evenIfRpcFails() {
		Map<String, Object> body = getOk("/consumer-still-login");
		Assertions.assertEquals(Boolean.TRUE, body.get("consumerLogin"));
		Assertions.assertEquals("10003", String.valueOf(body.get("consumerLoginId")));
		Assertions.assertEquals(Boolean.FALSE, body.get("rpcOk"));
		Assertions.assertEquals("UNKNOWN", body.get("rpcError"));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getOk(String path) {
		ResponseEntity<Map> resp = rest.getForEntity(path, Map.class);
		Assertions.assertEquals(200, resp.getStatusCodeValue());
		Map<String, Object> body = resp.getBody();
		Assertions.assertNotNull(body);
		Assertions.assertEquals(200, ((Number) body.get("code")).intValue(), String.valueOf(body));
		return body;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getRpcUnknown(String path) {
		ResponseEntity<Map> resp = rest.getForEntity(path, Map.class);
		Assertions.assertEquals(200, resp.getStatusCodeValue());
		Map<String, Object> body = resp.getBody();
		Assertions.assertNotNull(body);
		Assertions.assertEquals(500, ((Number) body.get("code")).intValue(), String.valueOf(body));
		Assertions.assertEquals("UNKNOWN", body.get("msg"));
		return body;
	}

}
