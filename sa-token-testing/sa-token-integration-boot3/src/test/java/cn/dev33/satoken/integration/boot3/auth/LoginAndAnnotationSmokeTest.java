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
package cn.dev33.satoken.integration.boot3.auth;

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
 * Boot 3 登录成功 + {@code @SaCheckLogin} 注解鉴权冒烟。
 */
@SpringBootTest(classes = IntegrationBoot3Application.class)
@AutoConfigureMockMvc
public class LoginAndAnnotationSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    /** 登录成功后应该返回 token，并且 isLogin 为 true */
    @Test
    public void login_shouldReturnTokenAndIsLoginTrue() throws Exception {
        String token = loginAndGetToken("/acc/doLogin");

        mockMvc.perform(MockMvcRequestBuilders.get("/acc/isLogin").header("satoken", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    /** 未登录访问 @SaCheckLogin 应返回 401，登录后再访问应放行 */
    @Test
    public void checkLogin_shouldRejectThenPassAfterLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/at/checkLogin"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(401));

        String token = loginAndGetToken("/at/login");
        mockMvc.perform(MockMvcRequestBuilders.get("/at/checkLogin").header("satoken", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
    }

    /** 调登录接口并取出 token */
    private String loginAndGetToken(String path) throws Exception {
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.get(path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        SaJsonTemplate jsonTemplate = SaManager.getSaJsonTemplate();
        Map<String, Object> map = jsonTemplate.jsonToMap(loginResult.getResponse().getContentAsString());
        return String.valueOf(map.get("token"));
    }

}
