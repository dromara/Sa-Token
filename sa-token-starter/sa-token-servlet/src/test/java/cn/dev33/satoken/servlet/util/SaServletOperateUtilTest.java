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
package cn.dev33.satoken.servlet.util;

import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * {@link SaServletOperateUtil} 输出流写入测试
 */
public class SaServletOperateUtilTest {

    /** 未设置 Content-Type 时 writeResult 应该写入默认 text/plain */
    @Test
    public void writeResult_withoutContentType_setDefault() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        SaServletOperateUtil.writeResult(response, "hello");

        Assertions.assertEquals(SaTokenConsts.CONTENT_TYPE_TEXT_PLAIN, response.getContentType());
        Assertions.assertEquals("hello", response.getContentAsString());
    }

    /** 已有 Content-Type 时 writeResult 不应该覆盖 */
    @Test
    public void writeResult_withExistingContentType_keepIt() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setContentType("application/json");

        SaServletOperateUtil.writeResult(response, "{\"ok\":true}");

        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertEquals("{\"ok\":true}", response.getContentAsString());
    }

}
