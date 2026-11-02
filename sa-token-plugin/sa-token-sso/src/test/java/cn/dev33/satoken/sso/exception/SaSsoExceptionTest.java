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
package cn.dev33.satoken.sso.exception;

import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SSO 异常构造、setCode、断言工具
 */
public class SaSsoExceptionTest {

	/** 只传 message 时应该把文案带上 */
	@Test
	public void ctor_messageOnly() {
		SaSsoException ex = new SaSsoException("sso boom");
		Assertions.assertEquals("sso boom", ex.getMessage());
	}

	/** 传 code + message 时两个都应该留下 */
	@Test
	public void ctor_codeAndMessage() {
		SaSsoException ex = new SaSsoException(SaSsoErrorCode.CODE_30001, "bad redirect");
		Assertions.assertEquals(SaSsoErrorCode.CODE_30001, ex.getCode());
		Assertions.assertEquals("bad redirect", ex.getMessage());
	}

	/** setCode 应该连缀写回自身 */
	@Test
	public void setCode_returnsSelf() {
		SaSsoException ex = new SaSsoException("x");
		Assertions.assertSame(ex, ex.setCode(SaSsoErrorCode.CODE_30004));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30004, ex.getCode());
	}

	/** notTrue 在 flag=true 时应该抛，false 时放过 */
	@Test
	public void notTrue_throwsWhenFlagTrue() {
		SaSsoException.notTrue(false, "no", SaSsoErrorCode.CODE_30023);
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> SaSsoException.notTrue(true, "bad", SaSsoErrorCode.CODE_30023));
		Assertions.assertEquals("bad", ex.getMessage());
		Assertions.assertEquals(SaSsoErrorCode.CODE_30023, ex.getCode());
	}

	/** notEmpty 空值应该抛，有值就过 */
	@Test
	public void notEmpty_throwsWhenBlank() {
		SaSsoException.notEmpty("ok", "no", SaSsoErrorCode.CODE_30012);
		SaSsoException n = Assertions.assertThrows(SaSsoException.class,
				() -> SaSsoException.notEmpty(null, "empty", SaSsoErrorCode.CODE_30012));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30012, n.getCode());
		SaSsoException e = Assertions.assertThrows(SaSsoException.class,
				() -> SaSsoException.notEmpty("", "empty", SaSsoErrorCode.CODE_30012));
		Assertions.assertEquals("empty", e.getMessage());
	}

	/** 过时的 throwBy 在 flag=true 时也应该抛 */
	@Test
	public void throwBy_deprecatedStillWorks() {
		SaSsoException.throwBy(false, "no");
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> SaSsoException.throwBy(true, "by"));
		Assertions.assertEquals("by", ex.getMessage());
	}

}
