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
package cn.dev33.satoken.testsupport;

import cn.dev33.satoken.reactor.spring.SaTokenContextRegister;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

/**
 * Reactor 过滤器单测的公共辅助：初始化 Reactor 策略、构造 Mock 交换对象。
 */
public final class ReactorTestHelper {

    private ReactorTestHelper() {
    }

    /** 注册 Reactor 版 SaStrategy 创建策略，Filter 测试前需要先调一次 */
    public static void ensureReactorStrategy() {
        new SaTokenContextRegister();
    }

    /** 造一个 GET 请求对应的 MockServerWebExchange */
    public static MockServerWebExchange newGetExchange(String uri) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(uri).build());
    }

}
