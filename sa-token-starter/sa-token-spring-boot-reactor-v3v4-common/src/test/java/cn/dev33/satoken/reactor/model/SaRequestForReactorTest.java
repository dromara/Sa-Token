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

import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ReactorTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SaRequestForReactor} 请求包装类测试
 */
@SaTokenTest
public class SaRequestForReactorTest {

    /** 初始化 Reactor 策略，保证用例跑在 Reactor 版请求模型上 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
    }

    /** 造一个带参数、请求头、Cookie 的请求包装对象 */
    private SaRequestForReactor buildRequest() {
        // 注意：MockServerHttpRequest 不会解析原始 Cookie 头，getCookieFirstValue 依赖 builder 造的 cookies，
        // 而 getCookieLastValue 依赖原始 Cookie 头解析，两者都需要
        MockServerHttpRequest httpRequest = MockServerHttpRequest
                .get("http://localhost/api/user?name=zhang")
                .header("Cache-Control", "no-cache")
                .cookie(new org.springframework.http.HttpCookie("token", "abc"),
                        new org.springframework.http.HttpCookie("sid", "9"),
                        new org.springframework.http.HttpCookie("token", "xyz"))
                .header("Cookie", "token=abc; sid=9; token=xyz")
                .build();
        return new SaRequestForReactor(httpRequest);
    }

    /** getSource 应该返回底层 ServerHttpRequest 对象 */
    @Test
    public void getSource() {
        SaRequestForReactor req = buildRequest();
        Assertions.assertSame(req.request, req.getSource());
    }

    /** getParam 应该能读取查询参数，不存在的参数返回 null */
    @Test
    public void getParam() {
        SaRequestForReactor req = buildRequest();
        Assertions.assertEquals("zhang", req.getParam("name"));
        Assertions.assertNull(req.getParam("missing"));
    }

    /** getParamNames 和 getParamMap 应该能读取全部查询参数 */
    @Test
    public void getParamNamesAndGetParamMap() {
        SaRequestForReactor req = buildRequest();
        Assertions.assertTrue(req.getParamNames().contains("name"));
        Assertions.assertEquals("zhang", req.getParamMap().get("name"));
    }

    /** getHeader 应该能读取请求头，不存在的头返回 null */
    @Test
    public void getHeader() {
        SaRequestForReactor req = buildRequest();
        Assertions.assertEquals("no-cache", req.getHeader("Cache-Control"));
        Assertions.assertNull(req.getHeader("missing"));
    }

    /** getCookieValue 应该取同名 Cookie 的最后一个值，不存在的 Cookie 返回 null */
    @Test
    public void getCookieValue() {
        SaRequestForReactor req = buildRequest();
        Assertions.assertEquals("xyz", req.getCookieValue("token"));
        Assertions.assertEquals("9", req.getCookieValue("sid"));
        Assertions.assertNull(req.getCookieValue("missing"));
    }

    /** getCookieFirstValue 应该取同名 Cookie 的第一个值 */
    @Test
    public void getCookieFirstValue() {
        SaRequestForReactor req = buildRequest();
        Assertions.assertEquals("abc", req.getCookieFirstValue("token"));
        Assertions.assertNull(req.getCookieFirstValue("missing"));
    }

    /** Cookie 头缺失或包含格式不完整条目时 getCookieValue 应该容错处理 */
    @Test
    public void getCookieValue_malformedCookie() {
        // 无 Cookie 头：getCookieLastValue 应该返回 null
        SaRequestForReactor noCookie = new SaRequestForReactor(
                MockServerHttpRequest.get("http://localhost/api/x").build());
        Assertions.assertNull(noCookie.getCookieValue("token"));

        // Cookie 头含无等号条目：应该跳过异常条目继续解析
        MockServerHttpRequest badRequest = MockServerHttpRequest.get("http://localhost/api/x")
                .header("Cookie", "novalue; token=ok")
                .build();
        SaRequestForReactor bad = new SaRequestForReactor(badRequest);
        Assertions.assertEquals("ok", bad.getCookieValue("token"));
        Assertions.assertNull(bad.getCookieValue("novalue"));
    }

    /** getRequestPath、getMethod、getHost、getUrl 应该能读取请求基础信息 */
    @Test
    public void requestBasicInfo() {
        SaRequestForReactor req = buildRequest();
        Assertions.assertEquals("/api/user", req.getRequestPath());
        Assertions.assertEquals("GET", req.getMethod());
        Assertions.assertEquals("localhost", req.getHost());
        Assertions.assertEquals("http://localhost/api/user?name=zhang", req.getUrl());
    }

    /** 配置 currDomain 后 getUrl 应该返回拼凑后的地址 */
    @Test
    public void getUrl_withCurrDomain() {
        cn.dev33.satoken.SaManager.getConfig().setCurrDomain("https://sso.com");
        SaRequestForReactor req = buildRequest();
        Assertions.assertEquals("https://sso.com/api/user", req.getUrl());
    }

    /** forward 应该基于改写后的路径构造新请求继续走链（返回 Mono，需要订阅） */
    @Test
    public void forward() {
        MockServerHttpRequest httpRequest = MockServerHttpRequest.get("http://localhost/api/user").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(httpRequest);
        SaRequestForReactor req = new SaRequestForReactor(httpRequest);
        AtomicReference<String> forwardedPath = new AtomicReference<>();
        SaReactorSyncHolder.setContext(exchange);
        exchange.getAttributes().put(SaReactorHolder.CHAIN_KEY, (WebFilterChain) e -> {
            forwardedPath.set(e.getRequest().getPath().toString());
            return Mono.empty();
        });

        Object result = req.forward("/forwarded");
        Assertions.assertNotNull(result);
        ((Mono<?>) result).block();

        Assertions.assertEquals("/forwarded", forwardedPath.get());
        SaReactorSyncHolder.clearContext();
    }

}
