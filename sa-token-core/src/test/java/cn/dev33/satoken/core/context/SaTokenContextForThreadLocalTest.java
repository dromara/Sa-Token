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

import cn.dev33.satoken.context.SaTokenContextForThreadLocal;
import cn.dev33.satoken.context.SaTokenContextForThreadLocalStaff;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaStorageForMock;
import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.exception.SaTokenContextException;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SaTokenContextForThreadLocal 上下文处理器测试
 */
@SaTokenTest
public class SaTokenContextForThreadLocalTest {

	private final SaTokenContextForThreadLocal context = new SaTokenContextForThreadLocal();

	@BeforeEach
	@AfterEach
	void clearThreadLocal() {
		SaTokenContextForThreadLocalStaff.clearModelBox();
	}

	/** setContext 前后 isValid 应分别为 false 和 true */
	@Test
	void isValidBeforeAndAfterSetContext() {
		Assertions.assertFalse(context.isValid());
		SaRequestForMock request = new SaRequestForMock();
		SaResponseForMock response = new SaResponseForMock();
		SaStorageForMock storage = new SaStorageForMock();
		context.setContext(request, response, storage);
		Assertions.assertTrue(context.isValid());
	}

	/** getModelBox 应返回与 setContext 相同的 Request / Response / Storage */
	@Test
	void getModelBoxReturnsSameObjects() {
		SaRequestForMock request = new SaRequestForMock();
		request.requestPath = "/thread-local";
		SaResponseForMock response = new SaResponseForMock();
		SaStorageForMock storage = new SaStorageForMock();
		storage.set("k", "v");
		context.setContext(request, response, storage);
		SaTokenContextModelBox box = context.getModelBox();
		Assertions.assertSame(request, box.getRequest());
		Assertions.assertSame(response, box.getResponse());
		Assertions.assertSame(storage, box.getStorage());
		Assertions.assertEquals("/thread-local", box.getRequest().getRequestPath());
		Assertions.assertEquals("v", box.getStorage().get("k"));
	}

	/** clearContext 后上下文应失效 */
	@Test
	void clearContextRemovesBox() {
		context.setContext(new SaRequestForMock(), new SaResponseForMock(), new SaStorageForMock());
		Assertions.assertTrue(context.isValid());
		context.clearContext();
		Assertions.assertFalse(context.isValid());
	}

	/** 未初始化时 getModelBox 应抛出 SaTokenContextException */
	@Test
	void getModelBoxThrowsWhenNotInitialized() {
		Assertions.assertThrows(SaTokenContextException.class, context::getModelBox);
	}

	/** Staff 工具类应能直接读写 ThreadLocal 中的上下文对象 */
	@Test
	void staffGetters() {
		SaRequestForMock request = new SaRequestForMock();
		SaResponseForMock response = new SaResponseForMock();
		SaStorageForMock storage = new SaStorageForMock();
		SaTokenContextForThreadLocalStaff.setModelBox(request, response, storage);
		Assertions.assertSame(request, SaTokenContextForThreadLocalStaff.getRequest());
		Assertions.assertSame(response, SaTokenContextForThreadLocalStaff.getResponse());
		Assertions.assertSame(storage, SaTokenContextForThreadLocalStaff.getStorage());
		Assertions.assertNotNull(SaTokenContextForThreadLocalStaff.getModelBoxOrNull());
	}

}
