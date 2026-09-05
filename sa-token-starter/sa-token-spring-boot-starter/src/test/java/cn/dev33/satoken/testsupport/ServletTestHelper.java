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
package cn.dev33.satoken.testsupport;

import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.servlet.util.SaTokenContextServletUtil;
import cn.dev33.satoken.spring.SaTokenContextRegister;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Servlet 过滤器 / 拦截器单测的公共辅助：初始化 Servlet 策略、在临时上下文中执行断言逻辑。
 */
public final class ServletTestHelper {

    private ServletTestHelper() {
    }

    /** 注册 Boot2 Servlet 版 SaStrategy 创建策略，Filter 测试前需要先调一次 */
    public static void ensureServletStrategy() {
        new SaTokenContextRegister();
    }

    /** 造一个 GET 请求 */
    public static MockHttpServletRequest newGetRequest(String uri) {
        return new MockHttpServletRequest("GET", uri);
    }

    /** 在 Sa-Token Servlet 上下文中执行一段逻辑，结束后自动清理上下文 */
    public static void withContext(MockHttpServletRequest request, MockHttpServletResponse response, Runnable action) {
        SaTokenContextServletUtil.setContext(request, response, (SaFunction) action::run);
    }

}
