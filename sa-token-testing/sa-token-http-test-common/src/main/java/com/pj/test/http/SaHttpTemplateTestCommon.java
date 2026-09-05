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
package com.pj.test.http;

import cn.dev33.satoken.http.SaHttpTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SaHttpTemplate} 实现类公共测试基类：基于本地 Http 服务器做真实请求往返验证。
 * 各 Http 插件测试类继承本类，只需实现 {@link #createTemplate()} 提供各自实例，
 * 测试方法统一调用本类的 protected 断言方法（本类不承载 @Test）。
 *
 * @author click33
 * @since 1.46.0
 */
public abstract class SaHttpTemplateTestCommon {

    /** 本地测试服务器 */
    protected LocalHttpServer server;

    /** 每个用例前启动本地服务器 */
    @BeforeEach
    public void startServer() throws IOException {
        server = new LocalHttpServer();
        server.start();
    }

    /** 每个用例后关闭本地服务器 */
    @AfterEach
    public void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    /** 提供被测的 SaHttpTemplate 实现对象 */
    protected abstract SaHttpTemplate createTemplate();

    /** get 请求时应该能拿到服务端响应，且 method、query 参数正确到达服务端 */
    protected void assertGet() {
        SaHttpTemplate template = createTemplate();

        String res = template.get(server.getBaseUrl() + LocalHttpServer.PATH_GET + "?name=zhang&age=18");

        Assertions.assertEquals("get-ok", res);
        Assertions.assertEquals("GET", server.getLastMethod());
        Assertions.assertEquals("name=zhang&age=18", server.getLastQuery());
    }

    /** postByFormData 请求时应该能把表单参数传到服务端 */
    protected void assertPostByFormData() {
        SaHttpTemplate template = createTemplate();
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "张三");
        params.put("age", 18);

        String res = template.postByFormData(server.getBaseUrl() + LocalHttpServer.PATH_POST, params);

        Assertions.assertEquals("post-ok", res);
        Assertions.assertEquals("POST", server.getLastMethod());
        Assertions.assertEquals("张三", server.getLastFormParams().get("name"));
        Assertions.assertEquals("18", server.getLastFormParams().get("age"));
    }

    /** postByFormData 参数值为 null 时应该按空串传到服务端 */
    protected void assertPostByFormDataWithNullValue() {
        SaHttpTemplate template = createTemplate();
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", null);

        template.postByFormData(server.getBaseUrl() + LocalHttpServer.PATH_POST, params);

        Assertions.assertEquals("", server.getLastFormParams().get("name"));
    }

}
