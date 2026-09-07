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
package cn.dev33.satoken.loveqq.boot.model;

import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link LoveqqSaResponse} 响应包装测试
 */
@SaTokenTest
public class LoveqqSaResponseTest {

	/** 状态码、响应头与 addHeader 应该写入底层响应 */
	@Test
	public void setStatusAndHeaders() {
		TestServerResponse response = LoveqqTestHelper.newResponse();
		LoveqqSaResponse saResponse = new LoveqqSaResponse(response);

		saResponse.setStatus(201).setHeader("A", "1").addHeader("B", "2").addHeader("B", "3");

		Assertions.assertSame(response, saResponse.getSource());
		Assertions.assertEquals(201, response.getStatus());
		Assertions.assertEquals("1", response.getHeader("A"));
		Assertions.assertEquals("2", response.getHeader("B"));
		Assertions.assertEquals(2, response.getHeaders("B").size());
	}

	/** redirect 应该交给底层 sendRedirect */
	@Test
	public void redirect_ok_returnLocation() {
		TestServerResponse response = LoveqqTestHelper.newResponse();
		LoveqqSaResponse saResponse = new LoveqqSaResponse(response);

		Assertions.assertEquals("/login", saResponse.redirect("/login"));
		Assertions.assertEquals(302, response.getStatus());
		Assertions.assertEquals("/login", response.getHeader("Location"));
	}

}
