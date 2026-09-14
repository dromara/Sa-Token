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
package cn.dev33.satoken.core.httpauth;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpBasicAccount 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaHttpBasicAccountTest {

	/** 双参构造与 getter/setter 应正确读写用户名和密码 */
	@Test
	void twoArgConstructor_andGettersSetters() {
		SaHttpBasicAccount account = new SaHttpBasicAccount("admin", "secret");
		Assertions.assertEquals("admin", account.getUsername());
		Assertions.assertEquals("secret", account.getPassword());

		account.setUsername("root");
		account.setPassword("pwd");
		Assertions.assertEquals("root", account.getUsername());
		Assertions.assertEquals("pwd", account.getPassword());
	}

	/** 单参构造应解析 username:password 格式 */
	@Test
	void oneArgConstructor_parsesUsernameAndPassword() {
		SaHttpBasicAccount account = new SaHttpBasicAccount("sa:123456");
		Assertions.assertEquals("sa", account.getUsername());
		Assertions.assertEquals("123456", account.getPassword());
	}

	/** 空或 null 账号字符串构造应抛出 SaTokenException */
	@Test
	void oneArgConstructor_empty_throws() {
		Assertions.assertThrows(SaTokenException.class, () -> new SaHttpBasicAccount(""));
		Assertions.assertThrows(SaTokenException.class, () -> new SaHttpBasicAccount(null));
	}

	/** 非法格式账号字符串构造应抛出 SaTokenException */
	@Test
	void oneArgConstructor_invalidFormat_throws() {
		Assertions.assertThrows(SaTokenException.class, () -> new SaHttpBasicAccount("only-user"));
		Assertions.assertThrows(SaTokenException.class, () -> new SaHttpBasicAccount("a:b:c"));
	}

	/** toString 应包含用户名和密码字段信息 */
	@Test
	void toString_containsFields() {
		SaHttpBasicAccount account = new SaHttpBasicAccount("user", "pass");
		String text = account.toString();
		Assertions.assertTrue(text.contains("user"));
		Assertions.assertTrue(text.contains("pass"));
		Assertions.assertTrue(text.startsWith("SaHttpBasicAccount{"));
	}

}
