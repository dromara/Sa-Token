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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.servlet.error.SaServletErrorCode;
import cn.dev33.satoken.servlet.testsupport.ServletModelTestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.List;

/**
 * {@link SaRequestForServlet} 请求包装测试
 */
@SaTokenTest
public class SaRequestForServletTest {

    @AfterEach
    public void tearDown() {
        ApplicationInfo.routePrefix = null;
        SaManager.getConfig().setCurrDomain(null);
    }

    /** 参数、请求头、方法、host 等基础读取应该正常 */
    @Test
    public void readBasicRequestInfo() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user/save");
        request.setServerName("example.com");
        request.addParameter("name", "zhang");
        request.addHeader("X-Token", "abc");

        SaRequestForServlet saRequest = new SaRequestForServlet(request);

        Assertions.assertSame(request, saRequest.getSource());
        Assertions.assertEquals("zhang", saRequest.getParam("name"));
        Assertions.assertEquals("abc", saRequest.getHeader("X-Token"));
        Assertions.assertEquals("POST", saRequest.getMethod());
        Assertions.assertEquals("example.com", saRequest.getHost());
        Assertions.assertEquals("/user/save", saRequest.getRequestPath());
    }

    /** getParamNames 和 getParamMap 应该返回请求里的全部参数 */
    @Test
    public void readParamNamesAndMap() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("a", "1");
        request.addParameter("b", "2", "ignored");

        SaRequestForServlet saRequest = new SaRequestForServlet(request);
        List<String> names = Arrays.asList("a", "b");

        Assertions.assertTrue(saRequest.getParamNames().containsAll(names));
        Assertions.assertEquals("1", saRequest.getParamMap().get("a"));
        Assertions.assertEquals("2", saRequest.getParamMap().get("b"));
    }

    /** Cookie 首值/末值/默认值应该按同名 Cookie 顺序读取 */
    @Test
    public void readCookieValues() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("token", "first"),
                new Cookie("token", "last"),
                new Cookie("other", null)
        );

        SaRequestForServlet saRequest = new SaRequestForServlet(request);

        Assertions.assertEquals("first", saRequest.getCookieFirstValue("token"));
        Assertions.assertEquals("last", saRequest.getCookieLastValue("token"));
        Assertions.assertEquals("last", saRequest.getCookieValue("token"));
        Assertions.assertNull(saRequest.getCookieFirstValue("missing"));
    }

    /** 没有 Cookie 时读取 Cookie 应该返回 null */
    @Test
    public void readCookie_whenNoCookies_returnNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        SaRequestForServlet saRequest = new SaRequestForServlet(request);

        Assertions.assertNull(saRequest.getCookieFirstValue("token"));
        Assertions.assertNull(saRequest.getCookieLastValue("token"));
    }

    /** Cookie 数组里遇到 null 元素时应该跳过继续查找 */
    @Test
    public void readCookie_skipNullCookieElement() {
        HttpServletRequest request = new HttpServletRequestWrapper(new MockHttpServletRequest()) {
            @Override
            public Cookie[] getCookies() {
                return new Cookie[]{null, new Cookie("token", "ok")};
            }
        };

        SaRequestForServlet saRequest = new SaRequestForServlet(request);

        Assertions.assertEquals("ok", saRequest.getCookieFirstValue("token"));
    }

    /** 配置了 routePrefix 时 getRequestPath 应该裁掉前缀 */
    @Test
    public void getRequestPath_withRoutePrefix_cutPrefix() {
        ApplicationInfo.routePrefix = "/api";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");

        SaRequestForServlet saRequest = new SaRequestForServlet(request);

        Assertions.assertEquals("/user", saRequest.getRequestPath());
    }

    /** 配置了 currDomain 时 getUrl 应该拼接域名和请求路径 */
    @Test
    public void getUrl_withCurrDomain_concatDomainAndPath() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        SaManager.getConfig().setCurrDomain("https://sa-token.com");

        SaRequestForServlet saRequest = new SaRequestForServlet(request);

        Assertions.assertEquals("https://sa-token.com/api/user", saRequest.getUrl());
    }

    /** 未配置 currDomain 时 getUrl 应该直接返回原始请求 URL */
    @Test
    public void getUrl_withoutCurrDomain_useRequestUrl() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");

        SaRequestForServlet saRequest = new SaRequestForServlet(request);

        Assertions.assertTrue(saRequest.getUrl().contains("/api/user"));
    }

    /** forward 成功时应该返回 null */
    @Test
    public void forward_ok_returnNull() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/from");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Object result = ServletModelTestSupport.withContext(request, response, () ->
                new SaRequestForServlet(request).forward("/to"));

        Assertions.assertNull(result);
    }

    /** forward 失败时应该抛出带 Servlet 错误码的 SaTokenException */
    @Test
    public void forward_failed_throwSaTokenException() {
        MockHttpServletRequest base = new MockHttpServletRequest("GET", "/from");
        HttpServletRequest request = new HttpServletRequestWrapper(base) {
            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                return new RequestDispatcher() {
                    @Override
                    public void forward(ServletRequest req, ServletResponse res) throws ServletException {
                        throw new ServletException("forward failed");
                    }

                    @Override
                    public void include(ServletRequest req, ServletResponse res) {
                    }
                };
            }
        };
        MockHttpServletResponse response = new MockHttpServletResponse();

        SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () ->
                ServletModelTestSupport.withContext(base, response, () ->
                        new SaRequestForServlet(request).forward("/broken")));

        Assertions.assertEquals(SaServletErrorCode.CODE_20001, ex.getCode());
    }

}
