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
package cn.dev33.satoken.reactor.model;

import cn.dev33.satoken.testsupport.ReactorTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.util.Arrays;

/**
 * {@link SaResponseForReactor} 响应包装类测试
 */
public class SaResponseForReactorTest {

    /** 初始化 Reactor 策略，保证用例跑在 Reactor 版请求模型上 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
    }

    /** getSource 应该返回底层 ServerHttpResponse 对象 */
    @Test
    public void getSource() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/res");
        SaResponseForReactor res = new SaResponseForReactor(exchange.getResponse());
        Assertions.assertSame(exchange.getResponse(), res.getSource());
    }

    /** setStatus 应该能写入响应状态码 */
    @Test
    public void setStatus() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/res");
        SaResponseForReactor res = new SaResponseForReactor(exchange.getResponse());

        res.setStatus(201);

        Assertions.assertEquals(HttpStatus.CREATED, exchange.getResponse().getStatusCode());
    }

    /** setHeader 应该覆盖写入请求头，addHeader 应该追加同名请求头 */
    @Test
    public void setHeaderAndAddHeader() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/res");
        SaResponseForReactor res = new SaResponseForReactor(exchange.getResponse());

        res.setHeader("X-Tag", "first");
        Assertions.assertEquals("first", exchange.getResponse().getHeaders().getFirst("X-Tag"));

        res.addHeader("X-Tag", "second");
        Assertions.assertEquals(Arrays.asList("first", "second"),
                exchange.getResponse().getHeaders().get("X-Tag"));
    }

    /** redirect 应该把状态码改为 302 并写入 Location */
    @Test
    public void redirect() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/res");
        SaResponseForReactor res = new SaResponseForReactor(exchange.getResponse());

        res.redirect("/login");

        Assertions.assertEquals(HttpStatus.FOUND, exchange.getResponse().getStatusCode());
        Assertions.assertEquals("/login", exchange.getResponse().getHeaders().getLocation().toString());
    }

}
