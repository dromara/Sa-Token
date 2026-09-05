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
package cn.dev33.satoken.filter;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFailHandleFunction;
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFunction;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ServletTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SaFirewallCheckFilterForServlet} 防火墙校验与异常分支测试
 */
@SaTokenTest
public class SaFirewallCheckFilterForServletTest {

    private SaFirewallCheckFunction backupCheck;
    private SaFirewallCheckFailHandleFunction backupFailHandle;

    @BeforeEach
    public void setUp() {
        ServletTestHelper.ensureServletStrategy();
        backupCheck = SaFirewallStrategy.instance.check;
        backupFailHandle = SaFirewallStrategy.instance.checkFailHandle;
    }

    @AfterEach
    public void tearDown() {
        SaFirewallStrategy.instance.check = backupCheck;
        SaFirewallStrategy.instance.checkFailHandle = backupFailHandle;
    }

    /** 防火墙校验通过时应该继续走 FilterChain */
    @Test
    public void doFilter_checkPass_continueChain() throws Exception {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {};
        SaFirewallCheckFilterForServlet filter = new SaFirewallCheckFilterForServlet();
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/safe");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> chainCalled.set(true));

        Assertions.assertTrue(chainCalled.get());
    }

    /** check 抛 StopMatchException 时应该吞掉并继续走链 */
    @Test
    public void doFilter_stopMatch_continueChain() throws Exception {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {
            throw new StopMatchException();
        };
        SaFirewallCheckFilterForServlet filter = new SaFirewallCheckFilterForServlet();
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/safe");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> chainCalled.set(true));

        Assertions.assertTrue(chainCalled.get());
    }

    /** check 抛 BackResultException 时应该写回响应并中断链条 */
    @Test
    public void doFilter_backResult_writeResponse() throws Exception {
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {
            throw new BackResultException("fw-block");
        };
        SaFirewallCheckFilterForServlet filter = new SaFirewallCheckFilterForServlet();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(ServletTestHelper.newGetRequest("/safe"), response, new MockFilterChain());

        Assertions.assertEquals("fw-block", response.getContentAsString());
    }

    /** FirewallCheckException 且没有自定义 failHandle 时，应该直接把异常信息写回响应 */
    @Test
    public void doFilter_firewallCheckDefault_writeMessage() throws Exception {
        SaFirewallStrategy.instance.checkFailHandle = null;
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {
            throw new FirewallCheckException("bad path");
        };
        SaFirewallCheckFilterForServlet filter = new SaFirewallCheckFilterForServlet();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(ServletTestHelper.newGetRequest("/bad"), response, new MockFilterChain());

        Assertions.assertEquals("bad path", response.getContentAsString());
    }

    /** FirewallCheckException 且配置了 failHandle 时，应该走自定义处理逻辑 */
    @Test
    public void doFilter_firewallCheckCustom_useFailHandle() throws Exception {
        AtomicReference<String> message = new AtomicReference<>();
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {
            throw new FirewallCheckException("bad path");
        };
        SaFirewallStrategy.instance.checkFailHandle = (e, req, res, extArg) -> message.set("handled:" + e.getMessage());
        SaFirewallCheckFilterForServlet filter = new SaFirewallCheckFilterForServlet();

        filter.doFilter(ServletTestHelper.newGetRequest("/bad"), new MockHttpServletResponse(), new MockFilterChain());

        Assertions.assertEquals("handled:bad path", message.get());
    }

}
