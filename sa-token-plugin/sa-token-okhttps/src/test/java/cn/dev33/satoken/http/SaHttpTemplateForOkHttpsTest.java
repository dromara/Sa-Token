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
package cn.dev33.satoken.http;

import com.pj.test.http.SaHttpTemplateTestCommon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaHttpTemplateForOkHttps} 测试：基于本地 Http 服务器做真实请求验证
 *
 * @author click33
 * @since 1.46.0
 */
public class SaHttpTemplateForOkHttpsTest extends SaHttpTemplateTestCommon {

    @Override
    protected SaHttpTemplate createTemplate() {
        return new SaHttpTemplateForOkHttps();
    }

    /** get 请求应该能拿到服务端响应，且 method、query 参数正确到达服务端 */
    @Test
    public void httpGet() {
        assertGet();
    }

    /** postByFormData 请求应该能把表单参数传到服务端 */
    @Test
    public void httpPostByFormData() {
        assertPostByFormData();
    }

    /** 默认构造函数应该能 new 出来 */
    @Test
    public void constructor_instantiable() {
        Assertions.assertNotNull(new SaHttpTemplateForOkHttps());
    }

}
