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
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
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

/**
 * {@link SaTokenCorsFilterForServlet} CORS 策略执行与异常分支测试
 */
@SaTokenTest
public class SaTokenCorsFilterForServletTest {

    private SaCorsHandleFunction backupCorsHandle;

    @BeforeEach
    public void setUp() {
        ServletTestHelper.ensureServletStrategy();
        backupCorsHandle = SaStrategy.instance.corsHandle;
    }

    @AfterEach
    public void tearDown() {
        SaStrategy.instance.corsHandle = backupCorsHandle;
    }

    /** corsHandle 正常执行后应该继续走 FilterChain */
    @Test
    public void doFilter_corsPass_continueChain() throws Exception {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaStrategy.instance.corsHandle = (req, res, sto) -> {};
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/cors");
        MockHttpServletResponse response = new MockHttpServletResponse();
        SaTokenContextFilterForServlet contextFilter = new SaTokenContextFilterForServlet();
        SaTokenCorsFilterForServlet corsFilter = new SaTokenCorsFilterForServlet();

        contextFilter.doFilter(request, response, (req, res) ->
                corsFilter.doFilter(req, res, (r, w) -> chainCalled.set(true)));

        Assertions.assertTrue(chainCalled.get());
    }

    /** corsHandle 抛 StopMatchException 时应该吞掉并继续走链 */
    @Test
    public void doFilter_stopMatch_continueChain() throws Exception {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaStrategy.instance.corsHandle = (req, res, sto) -> {
            throw new StopMatchException();
        };
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/cors");
        MockHttpServletResponse response = new MockHttpServletResponse();
        SaTokenContextFilterForServlet contextFilter = new SaTokenContextFilterForServlet();
        SaTokenCorsFilterForServlet corsFilter = new SaTokenCorsFilterForServlet();

        contextFilter.doFilter(request, response, (req, res) ->
                corsFilter.doFilter(req, res, (r, w) -> chainCalled.set(true)));

        Assertions.assertTrue(chainCalled.get());
    }

    /** corsHandle 抛 BackResultException 时应该写回响应并中断链条 */
    @Test
    public void doFilter_backResult_writeResponse() throws Exception {
        SaStrategy.instance.corsHandle = (req, res, sto) -> {
            throw new BackResultException("cors-block");
        };
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/cors");
        MockHttpServletResponse response = new MockHttpServletResponse();
        SaTokenContextFilterForServlet contextFilter = new SaTokenContextFilterForServlet();
        SaTokenCorsFilterForServlet corsFilter = new SaTokenCorsFilterForServlet();

        contextFilter.doFilter(request, response, (req, res) ->
                corsFilter.doFilter(req, res, new MockFilterChain()));

        Assertions.assertEquals("cors-block", response.getContentAsString());
    }

}
