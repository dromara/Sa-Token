/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.core.context.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.context.model.SaCookie;

/**
 * SaFoxUtil 工具类测试 
 * 
 * @author click33
 * @since 2022-2-8 22:14:25
 */
public class SaCookieTest {

    @Test
    public void test() {
    	SaCookie cookie = new SaCookie("satoken", "xxxx-xxxx-xxxx-xxxx")
    			.setDomain("https://sa-token.cc/")
    			.setMaxAge(-1)
    			.setPath("/")
    			.setSameSite("Lax")
    			.setHttpOnly(true)
    			.setSecure(true);

    	Assertions.assertEquals(cookie.getName(), "satoken");
    	Assertions.assertEquals(cookie.getValue(), "xxxx-xxxx-xxxx-xxxx");
    	Assertions.assertEquals(cookie.getDomain(), "https://sa-token.cc/");
    	Assertions.assertEquals(cookie.getMaxAge(), -1);
    	Assertions.assertEquals(cookie.getPath(), "/");
    	Assertions.assertEquals(cookie.getSameSite(), "Lax");
    	Assertions.assertEquals(cookie.getHttpOnly(), true);
    	Assertions.assertEquals(cookie.getSecure(), true);
    	Assertions.assertEquals(cookie.toHeaderValue(), "satoken=xxxx-xxxx-xxxx-xxxx; Domain=https://sa-token.cc/; Path=/; Secure; HttpOnly; SameSite=Lax");
    	
    	Assertions.assertNotNull(cookie.toString());
    }

}
