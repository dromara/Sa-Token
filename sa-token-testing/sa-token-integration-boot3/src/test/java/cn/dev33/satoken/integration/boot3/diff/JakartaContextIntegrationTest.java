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
package cn.dev33.satoken.integration.boot3.diff;

import cn.dev33.satoken.integration.boot3.IntegrationBoot3Application;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Boot 3 Jakarta Servlet 上下文差异测试：验证 request / response / storage / context util 在真实请求链路中可用。
 */
@SpringBootTest(classes = IntegrationBoot3Application.class)
@AutoConfigureMockMvc
public class JakartaContextIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /** 真实请求应该能读到 Jakarta 版 Request 包装的 path、参数、Cookie 和请求头 */
    @Test
    public void requestInfo_shouldExposeServletRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/jakarta/requestInfo")
                        .param("q", "abc123")
                        .header("X-Test-Header", "header-value")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .cookie(new Cookie("token", "first"), new Cookie("token", "last")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value("http://localhost/jakarta/requestInfo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("/jakarta/requestInfo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.method").value("GET"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.param").value("abc123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.header").value("header-value"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.host").value("localhost"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cookieFirst").value("first"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cookieLast").value("last"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isAjax").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sourceNotNull").value(true));
    }

    /** 真实请求应该能通过 Jakarta 版 Response 包装写入响应头 */
    @Test
    public void responseInfo_shouldExposeServletResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/jakarta/responseInfo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("X-Test-Set", "set-value"))
                .andExpect(MockMvcResultMatchers.header().string("X-Test-Add", "add-value"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sourceNotNull").value(true));
    }

    /** 真实请求应该能通过 Jakarta 版 Storage 包装读写 request 作用域 */
    @Test
    public void storageInfo_shouldExposeServletStorage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/jakarta/storageInfo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").value("stored-value"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterDelete").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sourceNotNull").value(true));
    }

    /** 真实请求里调用 setContext 函数版应该能拿到 request / response / ModelBox */
    @Test
    public void contextUtil_shouldExposeSetContextWithFunction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/jakarta/contextUtil"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("/jakarta/contextUtil"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestSame").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseSame").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.modelBoxNotNull").value(true));
    }

}
