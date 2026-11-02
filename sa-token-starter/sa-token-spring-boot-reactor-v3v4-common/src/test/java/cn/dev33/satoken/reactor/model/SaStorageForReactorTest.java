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
import org.springframework.mock.web.server.MockServerWebExchange;

/**
 * {@link SaStorageForReactor} 存储器包装类测试
 */
public class SaStorageForReactorTest {

    /** 初始化 Reactor 策略，保证用例跑在 Reactor 版请求模型上 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
    }

    /** getSource 应该返回底层 ServerWebExchange 对象 */
    @Test
    public void getSource() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/sto");
        SaStorageForReactor storage = new SaStorageForReactor(exchange);
        Assertions.assertSame(exchange, storage.getSource());
    }

    /** set + get 应该能正常读写，不存在的 key 返回 null */
    @Test
    public void setAndGet() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/sto");
        SaStorageForReactor storage = new SaStorageForReactor(exchange);

        storage.set("k1", "v1");

        Assertions.assertEquals("v1", storage.get("k1"));
        Assertions.assertNull(storage.get("missing"));
    }

    /** delete 之后应该取不到值 */
    @Test
    public void delete() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/sto");
        SaStorageForReactor storage = new SaStorageForReactor(exchange);

        storage.set("k1", "v1");
        storage.delete("k1");

        Assertions.assertNull(storage.get("k1"));
    }

}
