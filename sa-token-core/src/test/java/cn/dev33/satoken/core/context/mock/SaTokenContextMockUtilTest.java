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
package cn.dev33.satoken.core.context.mock;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaTokenContextMockUtil 与 SaResponseForMock 测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTokenContextMockUtilTest {

	/** 有返回值的 Mock 上下文回调应返回结果并清理上下文 */
	@Test
	void setMockContextWithResult() {
		String result = SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertNotNull(SaHolder.getRequest());
			return "done";
		});

		Assertions.assertEquals("done", result);
		Assertions.assertThrows(RuntimeException.class, SaHolder::getRequest);
	}

	/** Mock 响应应保留状态、响应头和重定向目标 */
	@Test
	void responseForMockStoresResponseData() {
		SaResponseForMock response = new SaResponseForMock();

		Assertions.assertSame(response, response.setStatus(201));
		Assertions.assertSame(response, response.setHeader("X-Token", "token"));
		Assertions.assertSame(response, response.addHeader("X-Extra", "value"));
		Assertions.assertNull(response.redirect("/login"));
		Assertions.assertNull(response.getSource());
		Assertions.assertEquals(201, response.status);
		Assertions.assertEquals("token", response.headerMap.get("X-Token"));
		Assertions.assertEquals("value", response.headerMap.get("X-Extra"));
		Assertions.assertEquals("/login", response.redirectTo);
	}

	/** 默认构造函数应可正常创建实例 */
	@Test
	void defaultConstructor() {
		Assertions.assertDoesNotThrow(SaTokenContextMockUtil::new);
	}

}
