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

import cn.dev33.satoken.application.SaApplication;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaStorageForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHolder 上下文持有类测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaHolderTest {

	/** Mock 上下文中应能获取 Request、Response 及 Context */
	@Test
	void getContextRequestResponse() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequest request = SaHolder.getRequest();
			SaResponse response = SaHolder.getResponse();
			Assertions.assertNotNull(request);
			Assertions.assertNotNull(response);
			Assertions.assertSame(SaHolder.getContext(), SaHolder.getContext());
		});
	}

	/** Storage 的 set / get / delete 应正常工作 */
	@Test
	void storageGetSetDelete() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaStorage storage = SaHolder.getStorage();
			storage.set("key1", "value1");
			Assertions.assertEquals("value1", storage.get("key1"));
			storage.delete("key1");
			Assertions.assertNull(storage.get("key1"));
			SaStorageForMock mockStorage = (SaStorageForMock) storage;
			mockStorage.set("num", 100);
			Assertions.assertEquals(100, mockStorage.get("num"));
		});
	}

	/** 通过 SaHolder 应能读取 Mock Request 的路径、方法、Host 和参数 */
	@Test
	void requestFieldsViaHolder() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/test";
			req.method = "GET";
			req.host = "localhost";
			req.parameterMap.put("id", "1");
			Assertions.assertEquals("/api/test", SaHolder.getRequest().getRequestPath());
			Assertions.assertEquals("GET", SaHolder.getRequest().getMethod());
			Assertions.assertEquals("localhost", SaHolder.getRequest().getHost());
			Assertions.assertEquals("1", SaHolder.getRequest().getParam("id"));
		});
	}

	/** getApplication 应返回默认 SaApplication 实例 */
	@Test
	void getApplication() {
		Assertions.assertSame(SaApplication.defaultInstance, SaHolder.getApplication());
	}

	/** 默认构造函数应可正常创建实例 */
	@Test
	void defaultConstructor() {
		Assertions.assertDoesNotThrow(SaHolder::new);
	}

}
