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

import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.util.SaResult;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

/**
 * 集成测试 HTTP 客户端：统一以 {@link SaResult} 解析 MockMvc 响应。
 */
public class MockMvcSaResultClient {

    private final MockMvc mvc;
    private final SaJsonTemplate jsonTemplate;

    public MockMvcSaResultClient(MockMvc mvc, SaJsonTemplate jsonTemplate) {
        this.mvc = mvc;
        this.jsonTemplate = jsonTemplate;
    }

    /** 发送 POST 请求并解析为 SaResult */
    public SaResult post(String path) throws Exception {
        return execute(MockMvcRequestBuilders.post(path));
    }

    /** 发送 GET 请求并解析为 SaResult */
    public SaResult get(String path) throws Exception {
        return execute(MockMvcRequestBuilders.get(path));
    }

    private SaResult execute(MockHttpServletRequestBuilder builder) throws Exception {
        MvcResult mvcResult = mvc.perform(builder
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        return parseBody(jsonTemplate, body);
    }

    /** 将响应 JSON 解析为 SaResult（避免 SaJsonType 反序列化需要 @class） */
    public static SaResult parseBody(SaJsonTemplate jsonTemplate, String body) {
        Map<String, Object> map = jsonTemplate.jsonToMap(body);
        return new SaResult().setMap(map);
    }

}
