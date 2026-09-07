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
package cn.dev33.satoken.jfinal.testsupport;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.jfinal.SaControllerContext;
import cn.dev33.satoken.jfinal.SaTokenContextForJfinal;
import com.jfinal.core.Controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JFinal 插件单测公共辅助：造 Servlet 请求、绑 Controller 上下文。
 */
public final class JfinalTestHelper {

    private JfinalTestHelper() {
    }

    /** 造一个能读写 attribute 的 GET 请求 */
    public static HttpServletRequest mockRequest(String uri) {
        Map<String, Object> attrs = new HashMap<String, Object>();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getContextPath()).thenReturn("");
        when(request.getServletPath()).thenReturn(uri);
        when(request.getMethod()).thenReturn("GET");
        when(request.getQueryString()).thenReturn(null);
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader(anyString())).thenReturn(null);
        when(request.getParameter(anyString())).thenReturn(null);
        when(request.getParameterNames()).thenReturn(Collections.emptyEnumeration());
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(request.getAttributeNames()).thenAnswer(invocation -> Collections.enumeration(attrs.keySet()));
        when(request.getAttribute(anyString())).thenAnswer(invocation -> attrs.get(invocation.getArgument(0)));
        doAnswer(invocation -> {
            attrs.put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(request).setAttribute(anyString(), any());
        doAnswer(invocation -> {
            attrs.remove((String) invocation.getArgument(0));
            return null;
        }).when(request).removeAttribute(anyString());
        return request;
    }

    /** 造一个能写 Writer / OutputStream 的响应，JFinal ErrorRender 两条通道都要用 */
    public static HttpServletResponse mockResponse() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(buffer, true);
        ServletOutputStream outputStream = new ServletOutputStream() {
            @Override
            public void write(int b) {
                buffer.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }
        };
        HttpServletResponse response = mock(HttpServletResponse.class);
        try {
            when(response.getWriter()).thenReturn(pw);
            when(response.getOutputStream()).thenReturn(outputStream);
            when(response.getCharacterEncoding()).thenReturn(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return response;
    }

    /** 挂上 JFinal 版 Sa-Token 上下文，并把 Controller 绑到当前线程 */
    public static void withHeldController(HttpServletRequest request, HttpServletResponse response, SaFunction action) {
        Controller controller = mock(Controller.class);
        when(controller.getRequest()).thenReturn(request);
        when(controller.getResponse()).thenReturn(response);
        SaManager.setSaTokenContext(new SaTokenContextForJfinal());
        SaControllerContext.hold(controller);
        try {
            action.run();
        } finally {
            SaControllerContext.release();
        }
    }
}
