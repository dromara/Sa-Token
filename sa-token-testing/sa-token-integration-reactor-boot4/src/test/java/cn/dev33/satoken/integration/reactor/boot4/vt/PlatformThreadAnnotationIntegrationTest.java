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
package cn.dev33.satoken.integration.reactor.boot4.vt;

import java.time.Duration;
import java.util.Map;

import cn.dev33.satoken.integration.reactor.boot4.IntegrationReactorBoot4Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 平台线程对照测试：不开启虚拟线程时，注解鉴权端点的行为基线。
 */
@SpringBootTest(classes = IntegrationReactorBoot4Application.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlatformThreadAnnotationIntegrationTest {

	@LocalServerPort
	private int port;

	private WebTestClient webTestClient;

	private String token;

	/** 每个用例前组装指向真实 Netty 端口的 client，并登录一次拿 token */
	@BeforeEach
	public void setUp() {
		webTestClient = WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.responseTimeout(Duration.ofSeconds(10))
				.build();
		Map<?, ?> body = webTestClient.get().uri("/vt/login")
				.exchange()
				.expectStatus().isOk()
				.expectBody(Map.class)
				.returnResult()
				.getResponseBody();
		assertNotNull(body);
		token = (String) body.get("token");
		assertNotNull(token);
	}

	/** 平台线程模式下，注解鉴权端点的行为基线 */
	@Test
	public void annotatedEndpoint_baselineOnPlatformThreads() {
		webTestClient.get().uri("/vt/annotated")
				.cookie("satoken", token)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.loginId").isEqualTo("10001")
				.jsonPath("$.thread").exists();
	}

}
