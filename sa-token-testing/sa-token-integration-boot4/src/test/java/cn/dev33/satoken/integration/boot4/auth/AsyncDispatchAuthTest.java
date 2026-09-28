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
package cn.dev33.satoken.integration.boot4.auth;

import cn.dev33.satoken.integration.boot4.IntegrationBoot4Application;
import cn.dev33.satoken.integration.boot4.support.Boot4MockMvcSupport;
import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 异步请求（SSE / StreamingResponseBody / DeferredResult）在 ASYNC 收尾派发上不应重复鉴权：
 * 首次 REQUEST 派发已通过 {@code @SaCheckLogin}，收尾派发时响应多已提交，
 * 若因 token 失效再次抛出 NotLoginException，异常无法转为错误响应，只能被容器中止连接。
 */
@SpringBootTest(classes = IntegrationBoot4Application.class)
public class AsyncDispatchAuthTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ApplicationContext applicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = Boot4MockMvcSupport.create(webApplicationContext, applicationContext);
    }

    /** token 在流进行期间被注销后，ASYNC 收尾派发不应因二次鉴权失败而中止连接 */
    @Test
    public void asyncCompletionDispatch_shouldNotReAuthAfterTokenLogout() throws Exception {
        String token = loginAndGetToken();

        MvcResult asyncResult = mockMvc.perform(get("/at/asyncDeferred").header("satoken", token))
                .andExpect(request().asyncStarted())
                .andReturn();

        // 模拟长连接持续期间 token 被注销 / 过期
        StpUtil.logoutByTokenValue(token);

        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /** token 仍有效时，收尾派发正常完成响应 */
    @Test
    public void asyncCompletionDispatch_shouldPassWithValidToken() throws Exception {
        String token = loginAndGetToken();

        MvcResult asyncResult = mockMvc.perform(get("/at/asyncDeferred").header("satoken", token))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /** 初始 REQUEST 派发的鉴权不受影响：未登录仍被拦截 */
    @Test
    public void initialDispatch_shouldStillRequireLogin() throws Exception {
        mockMvc.perform(get("/at/asyncDeferred"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    /** 登录并返回 token */
    private String loginAndGetToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(get("/at/login"))
                .andExpect(status().isOk())
                .andReturn();
        cn.dev33.satoken.json.SaJsonTemplate jsonTemplate = cn.dev33.satoken.SaManager.getSaJsonTemplate();
        java.util.Map<String, Object> map = jsonTemplate.jsonToMap(loginResult.getResponse().getContentAsString());
        return String.valueOf(map.get("token"));
    }

}
