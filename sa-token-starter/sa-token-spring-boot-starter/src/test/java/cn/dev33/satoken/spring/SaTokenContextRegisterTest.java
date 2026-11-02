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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 * {@link SaTokenContextRegister} Bean 注册与 SaStrategy 初始化测试
 */
public class SaTokenContextRegisterTest {

    /** 构造后应该能注册出三个 Filter Bean，且上下文 Filter 要带上正确的 order 和 dispatcherTypes */
    @Test
    public void registerBeans() {
        SaTokenContextRegister register = new SaTokenContextRegister();

        FilterRegistrationBean<?> contextFilter = register.saTokenContextFilterForServlet();
        Assertions.assertNotNull(contextFilter.getFilter());
        Assertions.assertTrue(contextFilter.getUrlPatterns().contains("/*"));
        Assertions.assertNotNull(contextFilter.getOrder());

        Assertions.assertNotNull(register.saTokenCorsFilterForServlet());
        Assertions.assertNotNull(register.saFirewallCheckFilterForServlet());
    }

}
