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
package cn.dev33.satoken.oauth2.data.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * CodeModel：无参带 createTime、全参构造、getter/setter、toString
 */
public class CodeModelTest {

	/** 无参构造应该带上 createTime */
	@Test
	public void noArgCtor_fillsCreateTime() {
		long before = System.currentTimeMillis();
		CodeModel m = new CodeModel();
		Assertions.assertTrue(m.getCreateTime() >= before);
	}

	/** 全参构造应该把六个字段都塞进去 */
	@Test
	public void fullCtor_setsFields() {
		CodeModel m = new CodeModel("code", "1001", Arrays.asList("userinfo"), 10001, "http://c/cb", "n1");
		Assertions.assertEquals("code", m.getCode());
		Assertions.assertEquals("1001", m.getClientId());
		Assertions.assertEquals(Arrays.asList("userinfo"), m.getScopes());
		Assertions.assertEquals(10001, m.getLoginId());
		Assertions.assertEquals("http://c/cb", m.getRedirectUri());
		Assertions.assertEquals("n1", m.getNonce());
	}

	/** setter 应该连缀写回，getter 能读到 */
	@Test
	public void gettersAndSetters_roundTrip() {
		CodeModel m = new CodeModel()
				.setCode("c")
				.setClientId("cid")
				.setScopes(Arrays.asList("a"))
				.setLoginId("u")
				.setRedirectUri("http://x")
				.setNonce("n")
				.setCreateTime(123L);
		Assertions.assertEquals("c", m.getCode());
		Assertions.assertEquals("cid", m.getClientId());
		Assertions.assertEquals(Arrays.asList("a"), m.getScopes());
		Assertions.assertEquals("u", m.getLoginId());
		Assertions.assertEquals("http://x", m.getRedirectUri());
		Assertions.assertEquals("n", m.getNonce());
		Assertions.assertEquals(123L, m.getCreateTime());
	}

	/** toString 里应该能看到 code 和 clientId */
	@Test
	public void toString_containsMainFields() {
		String text = new CodeModel("c", "1001", Arrays.asList("userinfo"), 1, "http://x", "n").toString();
		Assertions.assertTrue(text.startsWith("CodeModel{"));
		Assertions.assertTrue(text.contains("code='c'"));
		Assertions.assertTrue(text.contains("clientId='1001'"));
		Assertions.assertTrue(text.contains("nonce='n'"));
	}

}
