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
package cn.dev33.satoken.integration.boot2.autoconfig;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.filter.SaFirewallCheckFilterForServlet;
import cn.dev33.satoken.filter.SaTokenContextFilterForServlet;
import cn.dev33.satoken.filter.SaTokenCorsFilterForServlet;
import cn.dev33.satoken.integration.boot2.IntegrationBoot2Application;
import cn.dev33.satoken.spring.SaBeanRegister;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Spring Boot 2 starter 自动配置集成测试：验证核心 Bean、Filter 注册与真实过滤链。
 */
@SpringBootTest(classes = IntegrationBoot2Application.class)
@AutoConfigureMockMvc
public class AutoConfigurationIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    /** application.yml 中的 sa-token 配置应该绑定为 SaTokenConfig Bean */
    @Test
    public void saTokenConfig_shouldBindFromApplicationYml() {
        SaTokenConfig config = applicationContext.getBean(SaTokenConfig.class);
        Assertions.assertEquals("satoken", config.getTokenName());
        Assertions.assertEquals(2_592_000L, config.getTimeout());
        Assertions.assertTrue(config.getIsConcurrent());
    }

    /** SaBeanRegister 应该注册配置加载相关 Bean */
    @Test
    public void saBeanRegister_shouldBePresent() {
        Assertions.assertNotNull(applicationContext.getBean(SaBeanRegister.class));
    }

    /** 自动装配注册的上下文 Filter 应该挂上全局拦截与框架约定顺序，且 Filter 为容器注入实例 */
    @Test
    public void contextFilter_shouldBeRegisteredByContainer() {
        FilterRegistrationBean<?> bean = applicationContext.getBean(
                "saTokenContextFilterForServlet", FilterRegistrationBean.class);
        Assertions.assertNotNull(bean.getFilter());
        Assertions.assertInstanceOf(SaTokenContextFilterForServlet.class, bean.getFilter());
        Assertions.assertEquals(SaTokenConsts.SA_TOKEN_CONTEXT_FILTER_ORDER, bean.getOrder());
        Assertions.assertTrue(bean.getUrlPatterns().contains("/*"));
        Assertions.assertTrue(bean.isAsyncSupported());
    }

    /** starter 自动配置应该能拿到 CORS 与防火墙 Filter Bean */
    @Test
    public void servletFilters_shouldBeRegisteredAsBeans() {
        Assertions.assertNotNull(applicationContext.getBean(SaTokenCorsFilterForServlet.class));
        Assertions.assertNotNull(applicationContext.getBean(SaFirewallCheckFilterForServlet.class));
    }

    /** 注册的上下文 Filter 应该在真实过滤链中执行，使普通 Controller 能读到 Sa-Token 上下文 */
    @Test
    public void contextFilter_shouldRunInRealFilterChain() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/acc/isLogin"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
