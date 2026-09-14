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
package cn.dev33.satoken.integration.boot4.support;

import cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet;
import cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Boot 4 MockMvc 组装辅助：按框架约定顺序挂上 Jakarta 过滤器链。
 */
public final class Boot4MockMvcSupport {

    private Boot4MockMvcSupport() {
    }

    /** 取出容器里的 Jakarta 过滤器并按 context → cors → firewall → servletFilter 顺序组装 MockMvc */
    @SuppressWarnings("unchecked")
    public static MockMvc create(WebApplicationContext webApplicationContext, ApplicationContext applicationContext) {
        SaTokenContextFilterForJakartaServlet contextFilter = applicationContext.getBeansOfType(FilterRegistrationBean.class).values().stream()
                .map(bean -> (FilterRegistrationBean<?>) bean)
                .filter(bean -> bean.getFilter() instanceof SaTokenContextFilterForJakartaServlet)
                .map(bean -> (SaTokenContextFilterForJakartaServlet) bean.getFilter())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("SaTokenContextFilterForJakartaServlet 未注册"));
        return MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(
                        contextFilter,
                        applicationContext.getBean(SaTokenCorsFilterForJakartaServlet.class),
                        applicationContext.getBean(SaFirewallCheckFilterForJakartaServlet.class),
                        applicationContext.getBean(SaServletFilter.class)
                )
                .build();
    }

}
