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
package cn.dev33.satoken.quick.boot;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.quick.SaQuickManager;
import cn.dev33.satoken.quick.SaQuickRegister;
import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Quick-Login 自动装配后，登录页、登录接口与全局过滤器应该按生产路径工作
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
@AutoConfigureMockMvc
@SpringBootTest(classes = SaQuickLoginTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SaQuickLoginSpringTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaQuickConfig config;

    @Autowired
    private SaServletFilter saServletFilter;

    private boolean backupAuth;
    private String backupInclude;
    private String backupExclude;
    private String backupName;
    private String backupPwd;
    private boolean backupAuto;

    /** 每个用例前先记下配置，避免互相改脏 */
    @BeforeEach
    public void backupConfig() {
        backupAuth = config.getAuth();
        backupInclude = config.getInclude();
        backupExclude = config.getExclude();
        backupName = config.getName();
        backupPwd = config.getPwd();
        backupAuto = config.getAuto();
    }

    /** 每个用例后把配置改回去 */
    @AfterEach
    public void restoreConfig() {
        config.setAuth(backupAuth);
        config.setInclude(backupInclude);
        config.setExclude(backupExclude);
        config.setName(backupName);
        config.setPwd(backupPwd);
        config.setAuto(backupAuto);
        SaQuickManager.setConfig(config);
    }

    /** 自动装配应该把配置、过滤器都注册上 */
    @Test
    public void autoConfig_registersBeans() {
        Assertions.assertNotNull(config);
        Assertions.assertNotNull(saServletFilter);
        Assertions.assertSame(config, SaQuickManager.getConfig());
        Assertions.assertEquals("sa", SaQuickRegister.CONFIG_VERSION);
        Assertions.assertTrue(saServletFilter.includeList.contains("/**"));
        Assertions.assertTrue(saServletFilter.excludeList.contains("/saLogin"));
        Assertions.assertTrue(saServletFilter.excludeList.contains("/doLogin"));
    }

    /** GET /saLogin 应该渲染登录页，标题来自配置 */
    @Test
    public void saLogin_rendersPage() throws Exception {
        mockMvc.perform(get("/saLogin"))
                .andExpect(status().isOk())
                .andExpect(view().name("sa-login.html"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sa-Token 登录")));
    }

    /** POST /doLogin 账号密码正确时应该返回 200 和 token */
    @Test
    public void doLogin_success() throws Exception {
        MvcResult result = mockMvc.perform(post("/doLogin")
                        .param("name", "sa")
                        .param("pwd", "123456")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SaResult.CODE_SUCCESS))
                .andExpect(jsonPath("$.data.tokenValue").isString())
                .andReturn();

        String token = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.data.tokenValue");
        mockMvc.perform(get("/").header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(content().string("index"));
    }

    /** POST /doLogin 密码错误时应该返回错误码 */
    @Test
    public void doLogin_wrongPassword() throws Exception {
        mockMvc.perform(post("/doLogin").param("name", "sa").param("pwd", "bad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SaResult.CODE_ERROR))
                .andExpect(jsonPath("$.msg").value("账号或密码输入错误"));
    }

    /** POST /doLogin 账号或密码为空时应该返回 500 */
    @Test
    public void doLogin_emptyNameOrPwd() throws Exception {
        mockMvc.perform(post("/doLogin").param("name", "").param("pwd", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请输入账号和密码"));
    }

    /** 关闭 auth 后未登录访问业务页应该直接放行 */
    @Test
    public void filter_authOff_passThrough() throws Exception {
        config.setAuth(false);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("index"));
    }

    /** exclude 命中的路径未登录也应该放行 */
    @Test
    public void filter_exclude_passThrough() throws Exception {
        config.setExclude("/open");
        mockMvc.perform(get("/open"))
                .andExpect(status().isOk())
                .andExpect(content().string("open"));
    }

    /** include 没命中时不应该拦 */
    @Test
    public void filter_includeMiss_passThrough() throws Exception {
        config.setInclude("/admin/**");
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("index"));
    }

    /** Http Basic 账号密码正确时应该直接登录并放行 */
    @Test
    public void filter_httpBasic_success() throws Exception {
        String basic = "Basic " + SaBase64Util.encode("sa:123456");
        mockMvc.perform(get("/").header("Authorization", basic))
                .andExpect(status().isOk())
                .andExpect(content().string("index"));
    }

    /** Http Basic 密码错误时应该把登录失败结果写回 */
    @Test
    public void filter_httpBasic_fail() throws Exception {
        String basic = "Basic " + SaBase64Util.encode("sa:wrong");
        MvcResult result = mockMvc.perform(get("/").header("Authorization", basic))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        Assertions.assertTrue(body.contains("账号或密码输入错误") || body.contains("500"));
    }

    /** Http Basic 格式不对时应该走过滤器 error 回调 */
    @Test
    public void filter_httpBasic_malformed_hitsError() throws Exception {
        String basic = "Basic " + SaBase64Util.encode("nocolon");
        MvcResult result = mockMvc.perform(get("/").header("Authorization", basic))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        Assertions.assertTrue(body.contains("格式错误") || body.contains("UsernameAndPassword"));
    }

    /** 未登录访问业务页时过滤器应该转发到登录页 */
    @Test
    public void filter_notLogin_forwardsLogin() throws Exception {
        MvcResult result = mockMvc.perform(get("/")).andReturn();
        String forwarded = result.getResponse().getForwardedUrl();
        String body = result.getResponse().getContentAsString();
        boolean forwardedToLogin = "/saLogin".equals(forwarded) || body.contains("Sa-Token 登录");
        Assertions.assertTrue(forwardedToLogin, "未登录应转发或渲染登录页, forwarded=" + forwarded + ", body=" + body);
    }

}
