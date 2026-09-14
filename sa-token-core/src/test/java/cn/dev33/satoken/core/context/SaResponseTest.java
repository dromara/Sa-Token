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
package cn.dev33.satoken.core.context;

import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.model.SaCookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaResponse 默认方法测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaResponseTest {

	/** Cookie 默认包装方法应写入正确的响应头 */
	@Test
	void cookieDefaultMethods() {
		SaResponseForMock response = new SaResponseForMock();

		response.addCookie("sid", "abc", "/app", "example.com", 60);
		String cookieHeader = response.headerMap.get(SaCookie.HEADER_NAME);
		Assertions.assertTrue(cookieHeader.startsWith("sid=abc; Max-Age=60;"));
		Assertions.assertTrue(cookieHeader.contains("Domain=example.com"));
		Assertions.assertTrue(cookieHeader.contains("Path=/app"));

		response.addCookie(new SaCookie("theme", "dark").setPath("/"));
		Assertions.assertEquals("theme=dark; Path=/", response.headerMap.get(SaCookie.HEADER_NAME));

		response.deleteCookie("sid");
		Assertions.assertTrue(response.headerMap.get(SaCookie.HEADER_NAME).startsWith("sid=null; Max-Age=0;"));
		Assertions.assertTrue(response.headerMap.get(SaCookie.HEADER_NAME).contains("Path=/"));

		response.deleteCookie("sid", "/app", "example.com");
		cookieHeader = response.headerMap.get(SaCookie.HEADER_NAME);
		Assertions.assertTrue(cookieHeader.contains("Domain=example.com"));
		Assertions.assertTrue(cookieHeader.contains("Path=/app"));
	}

	/** setServer 默认包装方法应委托至 setHeader */
	@Test
	void setServerDelegatesToSetHeader() {
		SaResponseForMock response = new SaResponseForMock();

		Assertions.assertSame(response, response.setServer("sa-token"));
		Assertions.assertEquals("sa-token", response.headerMap.get("Server"));
	}

}
