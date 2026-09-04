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
package cn.dev33.satoken.core.context.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.context.model.SaCookie;

/**
 * SaCookie 测试
 */
public class SaCookieTest {

	/** getter 与 toHeaderValue 应输出完整 Set-Cookie 头 */
	@Test
	public void gettersAndToHeaderValue() {
		SaCookie cookie = new SaCookie("satoken", "xxxx-xxxx-xxxx-xxxx")
				.setDomain("https://sa-token.com/")
				.setMaxAge(-1)
				.setPath("/")
				.setSameSite("Lax")
				.setHttpOnly(true)
				.setSecure(true);

		Assertions.assertEquals("satoken", cookie.getName());
		Assertions.assertEquals("xxxx-xxxx-xxxx-xxxx", cookie.getValue());
		Assertions.assertEquals("https://sa-token.com/", cookie.getDomain());
		Assertions.assertEquals(-1, cookie.getMaxAge());
		Assertions.assertEquals("/", cookie.getPath());
		Assertions.assertEquals("Lax", cookie.getSameSite());
		Assertions.assertEquals(true, cookie.getHttpOnly());
		Assertions.assertEquals(true, cookie.getSecure());
		Assertions.assertEquals(
				"satoken=xxxx-xxxx-xxxx-xxxx; Domain=https://sa-token.com/; Path=/; Secure; HttpOnly; SameSite=Lax",
				cookie.toHeaderValue());

		Assertions.assertNotNull(cookie.toString());
	}

}
