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
package cn.dev33.satoken.context.grpc.model;

import cn.dev33.satoken.context.grpc.context.SaTokenGrpcContext;
import cn.dev33.satoken.context.grpc.support.GrpcTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Storage 包装：读写 grpc Context 里那份 Map。
 */
public class SaStorageForGrpcTest {

	/** 每条用例后把 grpc Context 拨回 ROOT */
	@AfterEach
	public void cleanup() {
		GrpcTestSupport.cleanup();
	}

	/** 挂上 grpc Context 后 getSource 应该就是那份 Map */
	@Test
	public void getSource_isGrpcMap() {
		GrpcTestSupport.withGrpcContext(() -> {
			SaStorageForGrpc storage = new SaStorageForGrpc();
			Assertions.assertSame(SaTokenGrpcContext.getContext(), storage.getSource());
		});
	}

	/** 普通 key 应该能 set / get / delete，setter 连缀返回自身 */
	@Test
	public void setGetDelete_roundTrip() {
		GrpcTestSupport.withGrpcContext(() -> {
			SaStorageForGrpc storage = new SaStorageForGrpc();
			Assertions.assertSame(storage, storage.set("k", "v"));
			Assertions.assertEquals("v", storage.get("k"));
			Assertions.assertSame(storage, storage.delete("k"));
			Assertions.assertNull(storage.get("k"));
		});
	}

}
