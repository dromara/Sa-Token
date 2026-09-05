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
package cn.dev33.satoken.integration.boot2.auth;

import cn.dev33.satoken.integration.boot2.support.AbstractMockMvcIntegrationTest;
import cn.dev33.satoken.integration.boot2.support.MockMvcSaResultClient;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

/**
 * 登录 / 注销 / Token 信息 HTTP 集成测试。
 */
public class LoginIntegrationTest extends AbstractMockMvcIntegrationTest {

    /** 登录成功后应该写 Cookie 并返回 token */
    @Test
    public void login_shouldSetCookieAndReturnToken() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/acc/doLogin")
                                .param("name", "zhang")
                                .param("pwd", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assertions.assertNotNull(mvcResult.getResponse().getHeader("Set-Cookie"));
        SaResult result = parseResult(mvcResult.getResponse().getContentAsString());
        Assertions.assertEquals(200, result.getCode());
        Assertions.assertNotNull(result.get("token"));
    }

    /** 登录后 isLogin、tokenInfo 应该正常，注销后 isLogin 应为 false */
    @Test
    public void loginLogoutFlow_shouldWork() {
        SaResult login = request("/acc/doLogin?name=zhang&pwd=123456");
        String token = login.get("token", String.class);
        Assertions.assertNotNull(token);

        Assertions.assertTrue(request("/acc/isLogin?satoken=" + token).get("data", Boolean.class));
        Map<String, Object> tokenInfo = request("/acc/tokenInfo?satoken=" + token).get("data", Map.class);
        Assertions.assertEquals("satoken", tokenInfo.get("tokenName"));
        Assertions.assertEquals(token, tokenInfo.get("tokenValue"));

        request("/acc/logout?satoken=" + token);
        Assertions.assertFalse(request("/acc/isLogin?satoken=" + token).get("data", Boolean.class));
    }

    private SaResult parseResult(String json) {
        return MockMvcSaResultClient.parseBody(cn.dev33.satoken.SaManager.getSaJsonTemplate(), json);
    }

}
