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
package cn.dev33.satoken.servlet.model;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.servlet.error.SaServletErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * {@link SaResponseForServlet} 响应包装测试
 */
public class SaResponseForServletTest {

    /** 状态码、响应头与 addHeader 应该写入底层 Response */
    @Test
    public void setStatusAndHeaders() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        SaResponseForServlet saResponse = new SaResponseForServlet(response);

        saResponse.setStatus(201).setHeader("A", "1").addHeader("B", "2");

        Assertions.assertSame(response, saResponse.getSource());
        Assertions.assertEquals(201, response.getStatus());
        Assertions.assertEquals("1", response.getHeader("A"));
        Assertions.assertEquals("2", response.getHeader("B"));
    }

    /** redirect 成功时应该返回 null */
    @Test
    public void redirect_ok_returnNull() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        SaResponseForServlet saResponse = new SaResponseForServlet(response);

        Assertions.assertNull(saResponse.redirect("/login"));
        Assertions.assertEquals("/login", response.getRedirectedUrl());
    }

    /** redirect 失败时应该抛出带 Servlet 错误码的 SaTokenException */
    @Test
    public void redirect_failed_throwSaTokenException() {
        HttpServletResponse response = new HttpServletResponseWrapper(new MockHttpServletResponse()) {
            @Override
            public void sendRedirect(String location) throws IOException {
                throw new IOException("redirect failed");
            }
        };
        SaResponseForServlet saResponse = new SaResponseForServlet(response);

        SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () -> saResponse.redirect("/login"));

        Assertions.assertEquals(SaServletErrorCode.CODE_20002, ex.getCode());
    }

}
