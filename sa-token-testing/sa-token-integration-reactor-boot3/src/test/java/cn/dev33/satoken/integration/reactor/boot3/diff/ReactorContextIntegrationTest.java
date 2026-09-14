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
package cn.dev33.satoken.integration.reactor.boot3.diff;

import cn.dev33.satoken.integration.reactor.boot3.IntegrationReactorBoot3Application;
import cn.dev33.satoken.integration.reactor.boot3.support.ReactorWebTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Boot 3 Reactor 上下文差异测试：验证 request / response / storage / holder 在真实 WebFlux 请求链路中可用。
 */
@SpringBootTest(classes = IntegrationReactorBoot3Application.class)
public class ReactorContextIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient;

    /** 每个用例前基于应用上下文组装 WebTestClient */
    @BeforeEach
    public void setUp() {
        webTestClient = ReactorWebTestSupport.create(applicationContext);
    }

    /** 真实请求应该能读到 Reactor 版 Request 包装的 path、参数、Cookie 和请求头 */
    @Test
    public void requestInfo_shouldExposeReactorRequest() {
        webTestClient.get().uri("http://localhost/reactor/requestInfo?q=abc123")
                .header("X-Test-Header", "header-value")
                .header("X-Requested-With", "XMLHttpRequest")
                .cookie("token", "first")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/reactor/requestInfo")
                .jsonPath("$.method").isEqualTo("GET")
                .jsonPath("$.param").isEqualTo("abc123")
                .jsonPath("$.header").isEqualTo("header-value")
                .jsonPath("$.host").isEqualTo("localhost")
                .jsonPath("$.cookieFirst").isEqualTo("first")
                .jsonPath("$.isAjax").isEqualTo(true)
                .jsonPath("$.sourceNotNull").isEqualTo(true);

        // WebFlux 解析同名 Cookie 时只留第一个，getCookieLastValue 依赖原始 Cookie 头
        webTestClient.get().uri("http://localhost/reactor/requestInfo")
                .header("Cookie", "token=first; token=last")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.cookieLast").isEqualTo("last");
    }

    /** 真实请求应该能通过 Reactor 版 Response 包装写入响应头 */
    @Test
    public void responseInfo_shouldExposeReactorResponse() {
        webTestClient.get().uri("/reactor/responseInfo")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Test-Set", "set-value")
                .expectHeader().valueEquals("X-Test-Add", "add-value")
                .expectBody()
                .jsonPath("$.sourceNotNull").isEqualTo(true);
    }

    /** 真实请求应该能通过 Reactor 版 Storage 包装读写 exchange 作用域 */
    @Test
    public void storageInfo_shouldExposeReactorStorage() {
        webTestClient.get().uri("/reactor/storageInfo")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.value").isEqualTo("stored-value")
                .jsonPath("$.afterDelete").isEmpty()
                .jsonPath("$.sourceNotNull").isEqualTo(true);
    }

    /** 真实请求里调用 SaReactorSyncHolder 函数版应该能拿到 path 和 exchange */
    @Test
    public void contextUtil_shouldExposeSyncHolder() {
        webTestClient.get().uri("/reactor/contextUtil")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isEqualTo("/reactor/contextUtil")
                .jsonPath("$.exchangeNotNull").isEqualTo(true);
    }

}
