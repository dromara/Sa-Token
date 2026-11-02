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
package cn.dev33.satoken.context.grpc.context;

import cn.dev33.satoken.context.grpc.support.GrpcTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * grpc Context 里那份 Map：create 之后才能读写。
 */
public class SaTokenGrpcContextTest {

	/** 每条用例后把 grpc Context 拨回 ROOT */
	@AfterEach
	public void cleanup() {
		GrpcTestSupport.cleanup();
	}

	/** 应测尽测：测试 SaTokenGrpcContext 无参构造 */
	@Test
	public void ctor_canNew() {
		new SaTokenGrpcContext();
	}

	/** 没 create 时 isNotNull 应该是 false */
	@Test
	public void isNotNull_falseBeforeCreate() {
		Assertions.assertFalse(SaTokenGrpcContext.isNotNull());
	}

	/** 没 create 时 set / remove 也不该 NPE */
	@Test
	public void setRemove_withoutCreate_shouldNoop() {
		SaTokenGrpcContext.set("k", "v");
		SaTokenGrpcContext.removeKey("k");
		Assertions.assertNull(SaTokenGrpcContext.get("k"));
	}

	/** create 挂上之后应该能 set / get / delete，getContext 就是那份 Map */
	@Test
	public void create_thenSetGetDelete() {
		GrpcTestSupport.withGrpcContext(() -> {
			Assertions.assertTrue(SaTokenGrpcContext.isNotNull());
			SaTokenGrpcContext.set("k", "v");
			Assertions.assertEquals("v", SaTokenGrpcContext.get("k"));
			Assertions.assertEquals("v", SaTokenGrpcContext.getContext().get("k"));
			SaTokenGrpcContext.removeKey("k");
			Assertions.assertNull(SaTokenGrpcContext.get("k"));
		});
	}

}
