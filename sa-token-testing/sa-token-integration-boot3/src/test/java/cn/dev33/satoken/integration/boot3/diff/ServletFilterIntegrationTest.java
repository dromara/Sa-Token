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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.integration.boot3.IntegrationBoot3Application;
import cn.dev33.satoken.json.SaJsonTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

/**
 * Boot 3 SaServletFilter 集成测试：验证生产写法注册的全局鉴权过滤器在真实请求链路中的 include/exclude 与钩子行为。
 */
@SpringBootTest(classes = IntegrationBoot3Application.class)
@AutoConfigureMockMvc
public class ServletFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /** 未登录访问拦截路由时应该被 SaServletFilter 拦截并返回 401 业务码 */
    @Test
    public void authRoute_shouldRejectWhenNotLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/filter/protected"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(401));
    }

    /** 放行路由应该不触发认证函数，但前置函数仍会执行 */
    @Test
    public void excludeRoute_shouldBypassAuthButRunBeforeAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/filter/open/free"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("X-Before-Auth", "yes"));
    }

    /** 非拦截路由的前置函数也会执行 */
    @Test
    public void otherRoute_shouldStillRunBeforeAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/jakarta/requestInfo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("X-Before-Auth", "yes"));
    }

    /** 登录后访问拦截路由应该放行 */
    @Test
    public void authRoute_shouldPassAfterLogin() throws Exception {
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.get("/filter/open/doLogin"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        SaJsonTemplate jsonTemplate = SaManager.getSaJsonTemplate();
        Map<String, Object> map = jsonTemplate.jsonToMap(loginResult.getResponse().getContentAsString());
        String token = String.valueOf(map.get("token"));

        mockMvc.perform(MockMvcRequestBuilders.get("/filter/protected").header("satoken", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
    }

}
