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
package cn.dev33.satoken.integration.boot3.autoconfig;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet;
import cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet;
import cn.dev33.satoken.integration.boot3.IntegrationBoot3Application;
import cn.dev33.satoken.spring.SaTokenContextRegister;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Boot 3 Starter 冒烟测试：验证容器能启动，且 Jakarta Servlet 自动配置 Bean 已注册。
 */
@SpringBootTest(classes = IntegrationBoot3Application.class)
public class Boot3StarterSmokeTest {

    @Autowired
    private ApplicationContext applicationContext;

    /** Boot 3 上下文应该能正常启动 */
    @Test
    public void context_shouldStart() {
        Assertions.assertNotNull(applicationContext);
    }

    /** sa-token 配置应该能绑定为 SaTokenConfig Bean */
    @Test
    public void saTokenConfig_shouldBindFromApplicationYml() {
        SaTokenConfig config = applicationContext.getBean(SaTokenConfig.class);
        Assertions.assertEquals("satoken", config.getTokenName());
    }

    /** Jakarta Servlet 上下文注册器与核心 Filter 应该作为 Bean 存在 */
    @Test
    @SuppressWarnings("unchecked")
    public void jakartaServletBeans_shouldBeRegistered() {
        Assertions.assertNotNull(applicationContext.getBean(SaTokenContextRegister.class));
        Assertions.assertNotNull(applicationContext.getBean(SaFirewallCheckFilterForJakartaServlet.class));

        boolean contextFilterRegistered = applicationContext.getBeansOfType(FilterRegistrationBean.class).values().stream()
                .anyMatch(bean -> bean.getFilter() instanceof SaTokenContextFilterForJakartaServlet);
        Assertions.assertTrue(contextFilterRegistered, "SaTokenContextFilterForJakartaServlet 应该通过 FilterRegistrationBean 注册");
    }

    /** 运行时类路径只应包含容器自带的 Servlet API，不应被 sa-token-jakarta-servlet 传递带入重复的 jakarta.servlet-api */
    @Test
    public void servletApi_shouldNotLeakDuplicateHttpServletClass() throws IOException {
        List<URL> resources = Collections.list(getClass().getClassLoader().getResources("jakarta/servlet/http/HttpServlet.class"));
        Assertions.assertEquals(1, resources.size(), "HttpServlet.class 应仅由容器提供，实际发现: " + resources);
        Assertions.assertFalse(resources.get(0).toString().contains("jakarta.servlet-api"), "不应加载 jakarta.servlet-api jar: " + resources);
    }

}
