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
package cn.dev33.satoken.integration.reactor.boot2.autoconfig;

import cn.dev33.satoken.reactor.filter.SaFirewallCheckFilterForReactor;
import cn.dev33.satoken.reactor.filter.SaTokenContextFilterForReactor;
import cn.dev33.satoken.reactor.filter.SaTokenCorsFilterForReactor;
import cn.dev33.satoken.reactor.model.SaRequestForReactor;
import cn.dev33.satoken.reactor.model.SaResponseForReactor;
import cn.dev33.satoken.reactor.model.SaStorageForReactor;
import cn.dev33.satoken.strategy.SaStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

/**
 * 自动装配集成测试：starter 应该注册出三个 Reactor Filter 并重写 SaStrategy。
 */
@SpringBootTest
public class AutoConfigurationIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    /** 自动装配应该注册出上下文 / CORS / 防火墙三个 Reactor Filter Bean */
    @Test
    public void autoConfig_registersReactorFilters() {
        Assertions.assertNotNull(applicationContext.getBean(SaTokenContextFilterForReactor.class));
        Assertions.assertNotNull(applicationContext.getBean(SaTokenCorsFilterForReactor.class));
        Assertions.assertNotNull(applicationContext.getBean(SaFirewallCheckFilterForReactor.class));
    }

    /** SaStrategy 应该产出 Reactor 版请求 / 响应 / 存储模型，路由匹配器可用 */
    @Test
    public void strategyProducesReactorModels() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/autoconfig").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Assertions.assertInstanceOf(SaRequestForReactor.class,
                SaStrategy.instance.createSaRequest.apply(exchange.getRequest()));
        Assertions.assertInstanceOf(SaResponseForReactor.class,
                SaStrategy.instance.createSaResponse.apply(exchange.getResponse()));
        Assertions.assertInstanceOf(SaStorageForReactor.class,
                SaStrategy.instance.createSaStorage.apply(exchange));
        Assertions.assertTrue(SaStrategy.instance.routeMatcher.apply("/**", "/any/path"));
    }

}
