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
package cn.dev33.satoken.spring;

import cn.dev33.satoken.exception.NotWebContextException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * {@link SpringMVCUtil} 在非 Web 上下文与 Spring RequestContextHolder 下的行为测试
 */
public class SpringMVCUtilTest {

    @AfterEach
    public void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    /** 没有绑定 RequestContextHolder 时，取 request/response 应该抛 NotWebContextException */
    @Test
    public void getRequestOrResponse_withoutContext_throw() {
        Assertions.assertThrows(NotWebContextException.class, SpringMVCUtil::getRequest);
        Assertions.assertThrows(NotWebContextException.class, SpringMVCUtil::getResponse);
        Assertions.assertFalse(SpringMVCUtil.isWeb());
    }

    /** 绑定 RequestContextHolder 后，应该能正常取到 request/response */
    @Test
    public void getRequestOrResponse_withContext_ok() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

        Assertions.assertTrue(SpringMVCUtil.isWeb());
        Assertions.assertSame(request, SpringMVCUtil.getRequest());
        Assertions.assertSame(response, SpringMVCUtil.getResponse());
    }

}
