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
package cn.dev33.satoken.integration.boot2.support;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.integration.boot2.IntegrationBoot2Application;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * 基于 MockMvc 的集成测试基类：统一 Spring 上下文、MockMvc 初始化与 SaResult 请求工具。
 */
@SpringBootTest(classes = IntegrationBoot2Application.class)
public abstract class AbstractMockMvcIntegrationTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    protected MockMvcSaResultClient saResultClient;

    /** 每个用例前初始化 MockMvc 与 SaResult 客户端 */
    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        saResultClient = new MockMvcSaResultClient(mockMvc, SaManager.getSaJsonTemplate());
    }

    /** 发送 POST 并返回 SaResult */
    protected SaResult post(String path) throws Exception {
        return saResultClient.post(path);
    }

    /** 发送 GET 并返回 SaResult */
    /** 发送 POST 并返回 SaResult（不向外抛受检异常，方便测试方法书写） */
    protected SaResult request(String path) {
        try {
            return post(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
