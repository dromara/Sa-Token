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
package cn.dev33.satoken.servlet.testsupport;

import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.fun.SaRetGenericFunction;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;
import cn.dev33.satoken.servlet.util.SaTokenContextServletUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet 模型单测公共辅助：注册 Servlet 版 SaStrategy，并在临时上下文中执行断言逻辑。
 */
public final class ServletModelTestSupport {

    private ServletModelTestSupport() {
    }

    /** 注册 Servlet 版 Request/Response/Storage 创建策略 */
    public static void ensureServletStrategy() {
        SaStrategy.instance.createSaRequest = source -> new SaRequestForServlet((HttpServletRequest) source);
        SaStrategy.instance.createSaResponse = source -> new SaResponseForServlet((HttpServletResponse) source);
        SaStrategy.instance.createSaStorage = source -> new SaStorageForServlet((HttpServletRequest) source);
    }

    /** 在 Sa-Token Servlet 上下文中执行一段逻辑，结束后自动清理上下文 */
    public static void withContext(MockHttpServletRequest request, MockHttpServletResponse response, Runnable action) {
        ensureServletStrategy();
        SaTokenContextServletUtil.setContext(request, response, (SaFunction) action::run);
    }

    /** 在 Sa-Token Servlet 上下文中执行一段带返回值的逻辑，结束后自动清理上下文 */
    public static <T> T withContext(MockHttpServletRequest request, MockHttpServletResponse response, SaRetGenericFunction<T> action) {
        ensureServletStrategy();
        return SaTokenContextServletUtil.setContext(request, response, action);
    }

}
