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
package cn.dev33.satoken.servlet.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * {@link SaStorageForServlet} Request 作用域存储测试
 */
public class SaStorageForServletTest {

    /** set/get/delete 应该读写 request attribute */
    @Test
    public void setGetDelete_attributeOnRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        SaStorageForServlet storage = new SaStorageForServlet(request);

        Assertions.assertSame(request, storage.getSource());
        storage.set("k", "v");
        Assertions.assertEquals("v", storage.get("k"));
        storage.delete("k");
        Assertions.assertNull(storage.get("k"));
    }

}
