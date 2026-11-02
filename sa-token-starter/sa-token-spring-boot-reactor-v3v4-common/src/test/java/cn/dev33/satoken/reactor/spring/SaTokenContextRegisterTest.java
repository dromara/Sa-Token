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
package cn.dev33.satoken.reactor.spring;

import cn.dev33.satoken.reactor.filter.SaFirewallCheckFilterForReactor;
import cn.dev33.satoken.reactor.filter.SaTokenContextFilterForReactor;
import cn.dev33.satoken.reactor.filter.SaTokenCorsFilterForReactor;
import cn.dev33.satoken.reactor.model.SaRequestForReactor;
import cn.dev33.satoken.reactor.model.SaResponseForReactor;
import cn.dev33.satoken.reactor.model.SaStorageForReactor;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.testsupport.ReactorTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

/**
 * {@link SaTokenContextRegister} Bean 注册与 SaStrategy 初始化测试
 */
public class SaTokenContextRegisterTest {

    /** 构造后应该能注册出三个 Filter Bean，且 Reactor 相关策略被重写 */
    @Test
    public void registerBeans() {
        SaTokenContextRegister register = new SaTokenContextRegister();
        Assertions.assertNotNull(register.saTokenContextFilterForServlet());
        Assertions.assertNotNull(register.saTokenCorsFilterForReactor());
        Assertions.assertNotNull(register.saFirewallCheckFilterForReactor());
    }

    /** 构造后 SaStrategy 的请求/响应/存储创建策略应该产出 Reactor 版模型 */
    @Test
    public void strategyRewritten() {
        ReactorTestHelper.ensureReactorStrategy();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/strategy");

        Assertions.assertInstanceOf(SaRequestForReactor.class,
                SaStrategy.instance.createSaRequest.apply(exchange.getRequest()));
        Assertions.assertInstanceOf(SaResponseForReactor.class,
                SaStrategy.instance.createSaResponse.apply(exchange.getResponse()));
        Assertions.assertInstanceOf(SaStorageForReactor.class,
                SaStrategy.instance.createSaStorage.apply(exchange));
        Assertions.assertTrue(SaStrategy.instance.routeMatcher.apply("/**", "/any/path"));
    }

}
