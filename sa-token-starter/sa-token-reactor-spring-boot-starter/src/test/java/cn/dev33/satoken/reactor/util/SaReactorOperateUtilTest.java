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
package cn.dev33.satoken.reactor.util;

import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ReactorTestHelper;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.server.MockServerWebExchange;

/**
 * {@link SaReactorOperateUtil} 响应写回工具测试
 */
@SaTokenTest
public class SaReactorOperateUtilTest {

    /** 初始化 Reactor 策略，保证用例跑在 Reactor 版请求模型上 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
    }

    /** writeResult 应该把结果写进响应体，并补上默认的 text/plain Content-Type */
    @Test
    public void writeResult_setsBodyAndContentType() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/write");

        SaReactorOperateUtil.writeResult(exchange, "hello").block();

        Assertions.assertEquals("hello", exchange.getResponse().getBodyAsString().block());
        Assertions.assertEquals(SaTokenConsts.CONTENT_TYPE_TEXT_PLAIN,
                exchange.getResponse().getHeaders().getFirst(SaTokenConsts.CONTENT_TYPE_KEY));
    }

    /** 响应已有 Content-Type 时 writeResult 不应该覆盖它 */
    @Test
    public void writeResult_keepsExistingContentType() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/write");
        exchange.getResponse().getHeaders().set(SaTokenConsts.CONTENT_TYPE_KEY, "application/json");

        SaReactorOperateUtil.writeResult(exchange, "{}").block();

        Assertions.assertEquals("application/json",
                exchange.getResponse().getHeaders().getFirst(SaTokenConsts.CONTENT_TYPE_KEY));
        Assertions.assertEquals("{}", exchange.getResponse().getBodyAsString().block());
    }


    /** 默认构造函数应该能 new 出来 */
    @Test
    public void constructor_instantiable() {
        Assertions.assertNotNull(new SaReactorOperateUtil());
    }
}
