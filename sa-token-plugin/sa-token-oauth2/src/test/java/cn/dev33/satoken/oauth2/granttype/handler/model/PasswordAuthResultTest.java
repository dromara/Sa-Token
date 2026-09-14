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
package cn.dev33.satoken.oauth2.granttype.handler.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Password 认证结果：空构造、loginId 构造、get/set、toString 多出来的逗号按现状断言
 */
public class PasswordAuthResultTest {

	/** 空构造 loginId 先是 null */
	@Test
	public void emptyCtor_loginIdNull() {
		Assertions.assertNull(new PasswordAuthResult().getLoginId());
	}

	/** 带 loginId 的构造应该直接塞进去 */
	@Test
	public void loginIdCtor_setsField() {
		Assertions.assertEquals(10001, new PasswordAuthResult(10001).getLoginId());
	}

	/** setter 应该连缀写回 */
	@Test
	public void getSet_roundTrip() {
		PasswordAuthResult m = new PasswordAuthResult().setLoginId("u");
		Assertions.assertEquals("u", m.getLoginId());
	}

	/** toString 现在是 PasswordAuthResult{, loginId=...}，多了一个逗号，先按现状断言 */
	@Test
	public void toString_hasExtraComma() {
		Assertions.assertEquals("PasswordAuthResult{, loginId=10001}", new PasswordAuthResult(10001).toString());
	}

}
