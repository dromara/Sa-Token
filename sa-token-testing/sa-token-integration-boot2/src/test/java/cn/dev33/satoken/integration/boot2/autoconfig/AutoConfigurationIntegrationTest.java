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
import cn.dev33.satoken.spring.SaTokenContextRegister;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;

/**
 * Spring Boot 2 starter 自动配置集成测试：验证核心 Bean 与 Filter 是否成功注册。
 */
@SpringBootTest(classes = IntegrationBoot2Application.class)
public class AutoConfigurationIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

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

    /** SaTokenContextRegister 自动配置应该注册上下文 Filter */
    @Test
    public void saTokenContextRegister_shouldRegisterContextFilter() {
        SaTokenContextRegister register = new SaTokenContextRegister();
        FilterRegistrationBean<SaTokenContextFilterForServlet> bean = register.saTokenContextFilterForServlet();
        Assertions.assertNotNull(bean.getFilter());
    }

    /** starter 自动配置应该能拿到 CORS 与防火墙 Filter Bean */
    @Test
    public void servletFilters_shouldBeRegisteredAsBeans() {
        Assertions.assertNotNull(applicationContext.getBean(SaTokenCorsFilterForServlet.class));
        Assertions.assertNotNull(applicationContext.getBean(SaFirewallCheckFilterForServlet.class));
    }

}
