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
import cn.dev33.satoken.integration.boot3.IntegrationBoot3Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * Boot 3 Starter 冒烟测试：只验证容器能启动且核心 Bean 存在，不重复 boot2 的全量集成。
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

}
