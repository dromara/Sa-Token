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
package cn.dev33.satoken.sso.message.handle;

import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.template.SaSsoTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 简单消息处理器要把 type 和 lambda 转过去
 */
public class SaSsoMessageSimpleHandleTest {

	/** getHandlerType 应该就是构造时传入的 type，handle 应该调 lambda */
	@Test
	public void handle_delegatesToLambda() {
		SaSsoTemplate template = new SaSsoTemplate();
		SaSsoMessage message = new SaSsoMessage("hi");
		SaSsoMessageSimpleHandle handle = new SaSsoMessageSimpleHandle("hi", (t, m) -> {
			Assertions.assertSame(template, t);
			Assertions.assertSame(message, m);
			return "done";
		});
		Assertions.assertEquals("hi", handle.getHandlerType());
		Assertions.assertEquals("done", handle.handle(template, message));
	}

}
