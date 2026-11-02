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
package cn.dev33.satoken.context.dubbo.model;

import cn.dev33.satoken.context.dubbo.support.DubboTestSupport;
import cn.dev33.satoken.util.SaTokenConsts;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Storage 包装：读写 attachment，token 还要回传给 Consumer。
 */
public class SaStorageForDubboTest {

	/** 每条用例后清掉 RpcContext */
	@AfterEach
	public void cleanup() {
		DubboTestSupport.cleanup();
	}

	/** 构造后 getSource 应该就是传入的 RpcContext */
	@Test
	public void ctor_keepsSource() {
		RpcContext ctx = RpcContext.getContext();
		SaStorageForDubbo storage = new SaStorageForDubbo(ctx);
		Assertions.assertSame(ctx, storage.getSource());
	}

	/** 普通 key 应该能 set / get / delete，setter 连缀返回自身 */
	@Test
	public void setGetDelete_roundTrip() {
		RpcContext ctx = RpcContext.getContext();
		SaStorageForDubbo storage = new SaStorageForDubbo(ctx);
		Assertions.assertSame(storage, storage.set("k", "v"));
		Assertions.assertEquals("v", storage.get("k"));
		Assertions.assertSame(storage, storage.delete("k"));
		Assertions.assertNull(storage.get("k"));
	}

	/** 写入 JUST_CREATED_NOT_PREFIX 时，还应该写到 ServerContext 给 Consumer 带回 */
	@Test
	public void setJustCreated_echoesToServerContext() {
		RpcContext ctx = RpcContext.getContext();
		SaStorageForDubbo storage = new SaStorageForDubbo(ctx);
		storage.set(SaTokenConsts.JUST_CREATED_NOT_PREFIX, "tok");
		Assertions.assertEquals("tok", storage.get(SaTokenConsts.JUST_CREATED_NOT_PREFIX));
		Assertions.assertEquals("tok",
				RpcContext.getServerContext().getAttachment(SaTokenConsts.JUST_CREATED_NOT_PREFIX));
	}

	/** 普通 key 不应该去动 ServerContext */
	@Test
	public void setOtherKey_doesNotTouchServerContext() {
		RpcContext ctx = RpcContext.getContext();
		SaStorageForDubbo storage = new SaStorageForDubbo(ctx);
		storage.set("other", "x");
		Assertions.assertNull(RpcContext.getServerContext().getAttachment("other"));
	}

}
