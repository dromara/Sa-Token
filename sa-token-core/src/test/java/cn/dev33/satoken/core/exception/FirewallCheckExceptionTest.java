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
package cn.dev33.satoken.core.exception;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.RequestPathInvalidException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * FirewallCheckException 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class FirewallCheckExceptionTest {

	/** 仅 message 构造时应正确保存消息和错误码 */
	@Test
	void messageConstructor() {
		FirewallCheckException ex = new FirewallCheckException("firewall blocked");
		ex.setCode(SaErrorCode.CODE_10002);
		Assertions.assertEquals("firewall blocked", ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_10002, ex.getCode());
	}

	/** 仅 cause 构造时应正确保存原始异常 */
	@Test
	void causeConstructor() {
		RuntimeException cause = new RuntimeException("root");
		FirewallCheckException ex = new FirewallCheckException(cause);
		Assertions.assertSame(cause, ex.getCause());
	}

	/** message + cause 构造时应同时保存消息和原始异常 */
	@Test
	void messageAndCauseConstructor() {
		RuntimeException cause = new RuntimeException("root");
		FirewallCheckException ex = new FirewallCheckException("firewall blocked", cause);
		Assertions.assertEquals("firewall blocked", ex.getMessage());
		Assertions.assertSame(cause, ex.getCause());
	}

	/** RequestPathInvalidException 应继承 FirewallCheckException 并保存非法路径 */
	@Test
	void requestPathInvalidExceptionExtendsFirewallCheckException() {
		RequestPathInvalidException ex = new RequestPathInvalidException("非法请求：/bad", "/bad");
		Assertions.assertInstanceOf(FirewallCheckException.class, ex);
		Assertions.assertEquals("/bad", ex.getPath());
	}

}
