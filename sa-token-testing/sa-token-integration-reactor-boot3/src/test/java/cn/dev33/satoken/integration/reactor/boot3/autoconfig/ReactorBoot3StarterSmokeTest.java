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
package cn.dev33.satoken.integration.reactor.boot3.autoconfig;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.integration.reactor.boot3.IntegrationReactorBoot3Application;
import cn.dev33.satoken.reactor.filter.SaFirewallCheckFilterForReactor;
import cn.dev33.satoken.reactor.filter.SaTokenContextFilterForReactor;
import cn.dev33.satoken.reactor.filter.SaTokenCorsFilterForReactor;
import cn.dev33.satoken.reactor.model.SaRequestForReactor;
import cn.dev33.satoken.reactor.model.SaResponseForReactor;
import cn.dev33.satoken.reactor.model.SaStorageForReactor;
import cn.dev33.satoken.reactor.spring.SaTokenContextRegister;
import cn.dev33.satoken.strategy.SaStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

/**
 * Boot 3 Reactor Starter 冒烟测试：验证容器启动、自动装配 Filter 与 PathPattern 策略重写。
 */
@SpringBootTest(classes = IntegrationReactorBoot3Application.class)
public class ReactorBoot3StarterSmokeTest {

    @Autowired
    private ApplicationContext applicationContext;

    /** Boot 3 WebFlux 上下文应该能正常启动 */
    @Test
    public void context_shouldStart() {
        Assertions.assertNotNull(applicationContext);
    }

    /** sa-token 配置应该能绑定为 SaTokenConfig Bean */
    @Test
    public void saTokenConfig_shouldBindFromApplicationYml() {
        SaTokenConfig config = applicationContext.getBean(SaTokenConfig.class);
        Assertions.assertEquals("satoken", config.getTokenName());
    }

    /** 自动装配应该注册出上下文注册器与三个 Filter Bean */
    @Test
    public void reactorBeans_shouldBeRegistered() {
        Assertions.assertNotNull(applicationContext.getBean(SaTokenContextRegister.class));
        Assertions.assertNotNull(applicationContext.getBean(SaTokenContextFilterForReactor.class));
        Assertions.assertNotNull(applicationContext.getBean(SaTokenCorsFilterForReactor.class));
        Assertions.assertNotNull(applicationContext.getBean(SaFirewallCheckFilterForReactor.class));
    }

    /** SaStrategy 应该产出 Reactor 版模型，路由匹配器应按 PathPatternParser 识别路径变量 */
    @Test
    public void strategy_shouldProduceReactorModels() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/autoconfig").build());

        Assertions.assertInstanceOf(SaRequestForReactor.class,
                SaStrategy.instance.createSaRequest.apply(exchange.getRequest()));
        Assertions.assertInstanceOf(SaResponseForReactor.class,
                SaStrategy.instance.createSaResponse.apply(exchange.getResponse()));
        Assertions.assertInstanceOf(SaStorageForReactor.class,
                SaStrategy.instance.createSaStorage.apply(exchange));
        Assertions.assertTrue(SaStrategy.instance.routeMatcher.apply("/rt/{id}", "/rt/101"));
        Assertions.assertFalse(SaStrategy.instance.routeMatcher.apply("/rt/{id}", "/acc/isLogin"));
    }

}
