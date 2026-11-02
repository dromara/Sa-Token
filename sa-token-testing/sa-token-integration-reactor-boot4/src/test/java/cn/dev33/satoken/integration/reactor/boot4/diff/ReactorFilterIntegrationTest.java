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
package cn.dev33.satoken.integration.reactor.boot4.diff;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.integration.reactor.boot4.IntegrationReactorBoot4Application;
import cn.dev33.satoken.integration.reactor.boot4.support.ReactorWebTestSupport;
import cn.dev33.satoken.json.SaJsonTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

/**
 * Boot 4 SaReactorFilter 集成测试：验证生产写法注册的全局鉴权过滤器在真实请求链路中的 include/exclude 与钩子行为。
 */
@SpringBootTest(classes = IntegrationReactorBoot4Application.class)
public class ReactorFilterIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient;

    /** 每个用例前基于应用上下文组装 WebTestClient */
    @BeforeEach
    public void setUp() {
        webTestClient = ReactorWebTestSupport.create(applicationContext);
    }

    /** 未登录访问拦截路由时应该被 SaReactorFilter 拦截并返回 401 业务码 */
    @Test
    public void authRoute_shouldRejectWhenNotLogin() {
        webTestClient.get().uri("/filter/protected")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(401);
    }

    /** 放行路由应该不触发认证函数，但前置函数仍会执行 */
    @Test
    public void excludeRoute_shouldBypassAuthButRunBeforeAuth() {
        webTestClient.get().uri("/filter/open/free")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Before-Auth", "yes");
    }

    /** 非拦截路由的前置函数也会执行 */
    @Test
    public void otherRoute_shouldStillRunBeforeAuth() {
        webTestClient.get().uri("/reactor/requestInfo")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Before-Auth", "yes");
    }

    /** 登录后访问拦截路由应该放行 */
    @Test
    public void authRoute_shouldPassAfterLogin() {
        String body = webTestClient.get().uri("/filter/open/doLogin")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        SaJsonTemplate jsonTemplate = SaManager.getSaJsonTemplate();
        Map<String, Object> map = jsonTemplate.jsonToMap(body);
        String token = String.valueOf(map.get("token"));

        webTestClient.get().uri("/filter/protected")
                .header("satoken", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200);
    }

}
