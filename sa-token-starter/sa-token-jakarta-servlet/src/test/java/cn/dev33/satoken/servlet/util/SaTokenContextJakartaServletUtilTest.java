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
package cn.dev33.satoken.servlet.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.servlet.testsupport.JakartaServletModelTestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * {@link SaTokenContextJakartaServletUtil} 上下文读写测试
 */
@SaTokenTest
public class SaTokenContextJakartaServletUtilTest {

    /** 每个用例开始前准备测试现场 */
    @BeforeEach
    public void setUp() {
        JakartaServletModelTestSupport.ensureServletStrategy();
    }

    /** 应测尽测：测试 SaTokenContextJakartaServletUtil 无参构造 */
    @Test
    public void constructor_ok() {
        new SaTokenContextJakartaServletUtil();
    }

    /** Runnable 版 setContext 抛异常时也应该清理上下文 */
    @Test
    public void setContext_withRunnable_clearOnException() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/ctx");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertThrows(RuntimeException.class, () ->
                JakartaServletModelTestSupport.withContext(request, response, () -> {
                    throw new RuntimeException("boom");
                }));

        Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
    }

    /** 无回调的 setContext 应该写入上下文，手动清理后失效 */
    @Test
    public void setContext_withoutCallback_manualClear() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/ctx");
        MockHttpServletResponse response = new MockHttpServletResponse();

        SaTokenContextJakartaServletUtil.setContext(request, response);
        try {
            Assertions.assertNotNull(SaTokenContextJakartaServletUtil.getModelBox());
            Assertions.assertSame(request, SaTokenContextJakartaServletUtil.getRequest());
            Assertions.assertSame(response, SaTokenContextJakartaServletUtil.getResponse());
            Assertions.assertNotNull(SaHolder.getRequest());
        } finally {
            SaTokenContextJakartaServletUtil.clearContext();
        }

        Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
    }

    /** Runnable 版 setContext 应该在执行后自动清理上下文 */
    @Test
    public void setContext_withRunnable_autoClear() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/ctx");
        MockHttpServletResponse response = new MockHttpServletResponse();

        JakartaServletModelTestSupport.withContext(request, response, () ->
                Assertions.assertEquals("/ctx", SaHolder.getRequest().getRequestPath()));

        Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
    }

    /** 泛型版 setContext 应该返回函数结果并在结束后清理上下文 */
    @Test
    public void setContext_withGenericFunction_returnValue() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/ctx");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String value = JakartaServletModelTestSupport.withContext(request, response, () -> "ok");

        Assertions.assertEquals("ok", value);
        Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
    }

}
