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

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ServletTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link SaTokenContextFilterForServlet} 上下文写入与清理测试
 */
@SaTokenTest
public class SaTokenContextFilterForServletTest {

    @BeforeEach
    public void setUp() {
        ServletTestHelper.ensureServletStrategy();
    }

    /** 过滤器执行期间应该能拿到 SaHolder 上下文，结束后要清掉 */
    @Test
    public void doFilter_setAndClearContext() throws Exception {
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/ctx");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean seenInChain = new AtomicBoolean(false);
        SaTokenContextFilterForServlet filter = new SaTokenContextFilterForServlet();

        filter.doFilter(request, response, (req, res) -> seenInChain.set(SaHolder.getRequest() != null));

        Assertions.assertTrue(seenInChain.get());
        Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
    }

    /** 链条抛异常时 finally 也应该清理上下文 */
    @Test
    public void doFilter_clearContextWhenChainThrows() {
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/ctx");
        MockHttpServletResponse response = new MockHttpServletResponse();
        SaTokenContextFilterForServlet filter = new SaTokenContextFilterForServlet();

        Assertions.assertThrows(RuntimeException.class, () ->
                filter.doFilter(request, response, (req, res) -> {
                    throw new RuntimeException("chain failed");
                }));

        Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
    }

}
