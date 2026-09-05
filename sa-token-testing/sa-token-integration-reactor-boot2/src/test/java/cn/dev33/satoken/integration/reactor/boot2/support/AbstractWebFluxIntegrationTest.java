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
package cn.dev33.satoken.integration.reactor.boot2.support;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.integration.reactor.boot2.IntegrationReactorBoot2Application;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

/**
 * 基于 WebTestClient 的 WebFlux 集成测试基类：统一 Spring 上下文与请求工具。
 */
@SpringBootTest(classes = IntegrationReactorBoot2Application.class)
public abstract class AbstractWebFluxIntegrationTest {

    @Autowired
    protected ApplicationContext applicationContext;

    protected WebTestClient webTestClient;

    /** 每个用例前基于应用上下文构建 WebTestClient（不依赖 Boot 版本相关的测试注解） */
    @BeforeEach
    public void setUpWebTestClient() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    /** 发送 GET 请求并返回响应体原文 */
    protected String getBody(String uri) {
        return getBodyWithToken(uri, null);
    }

    /** 发送携带 satoken 请求头的 GET 请求并返回响应体原文 */
    protected String getBodyWithToken(String uri, String token) {
        WebTestClient.RequestHeadersSpec<?> spec = webTestClient.get().uri(uri);
        if (token != null) {
            spec = spec.header("satoken", token);
        }
        return spec.exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();
    }

    /** 发送 GET 请求并把 SaResult JSON 响应解析为 Map */
    protected Map<String, Object> getAsMap(String uri) {
        return SaManager.getSaJsonTemplate().jsonToMap(getBody(uri));
    }

    /** 发送携带 satoken 请求头的 GET 请求并把 SaResult JSON 响应解析为 Map */
    protected Map<String, Object> getAsMapWithToken(String uri, String token) {
        return SaManager.getSaJsonTemplate().jsonToMap(getBodyWithToken(uri, token));
    }

}
