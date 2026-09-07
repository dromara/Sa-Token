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
package cn.dev33.satoken.jboot.testsupport;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.jboot.SaTokenContextForJboot;
import com.jfinal.core.Controller;
import io.jboot.web.controller.JbootControllerContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JBoot 插件单测公共辅助：造 Servlet 请求、绑 JbootControllerContext。
 */
public final class JbootTestHelper {

    private JbootTestHelper() {
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

    /** 造一个空的响应对象，上下文包装只要 source */
    public static HttpServletResponse mockResponse() {
        return mock(HttpServletResponse.class);
    }

    /** 挂上 JBoot 版 Sa-Token 上下文，并把 Controller 绑到当前线程 */
    public static void withHeldController(HttpServletRequest request, HttpServletResponse response, SaFunction action) {
        Controller controller = mock(Controller.class);
        when(controller.getRequest()).thenReturn(request);
        when(controller.getResponse()).thenReturn(response);
        SaManager.setSaTokenContext(new SaTokenContextForJboot());
        JbootControllerContext.hold(controller);
        try {
            action.run();
        } finally {
            JbootControllerContext.release();
        }
    }
}
