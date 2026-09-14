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
package cn.dev33.satoken.sso.message;

import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.message.handle.SaSsoMessageSimpleHandle;
import cn.dev33.satoken.sso.template.SaSsoTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 消息处理器持有器：增删查、找不到处理器
 */
public class SaSsoMessageHolderTest {

	/** addHandle / hasHandle / getHandle / removeHandle 应该成套好使 */
	@Test
	public void addGetRemove_handleLifecycle() {
		SaSsoMessageHolder holder = new SaSsoMessageHolder();
		SaSsoMessageSimpleHandle handle = new SaSsoMessageSimpleHandle("ping", (t, m) -> "pong");
		Assertions.assertSame(holder, holder.addHandle(handle));
		Assertions.assertTrue(holder.hasHandle("ping"));
		Assertions.assertSame(handle, holder.getHandle("ping"));
		Assertions.assertSame(holder, holder.removeHandle("ping"));
		Assertions.assertFalse(holder.hasHandle("ping"));
		Assertions.assertNull(holder.getHandle("ping"));
	}

	/** 按类型加 lambda 处理器，handleMessage 应该跑到它 */
	@Test
	public void addHandleByType_runsLambda() {
		SaSsoMessageHolder holder = new SaSsoMessageHolder();
		holder.addHandle("echo", (t, m) -> m.getType());
		Object out = holder.handleMessage(new SaSsoTemplate(), new SaSsoMessage("echo"));
		Assertions.assertEquals("echo", out);
	}

	/** 没有对应处理器时应该抛 30021 */
	@Test
	public void handleMessage_missingThrows30021() {
		SaSsoMessageHolder holder = new SaSsoMessageHolder();
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> holder.handleMessage(new SaSsoTemplate(), new SaSsoMessage("nope")));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30021, ex.getCode());
		Assertions.assertTrue(ex.getMessage().contains("nope"));
	}

}
