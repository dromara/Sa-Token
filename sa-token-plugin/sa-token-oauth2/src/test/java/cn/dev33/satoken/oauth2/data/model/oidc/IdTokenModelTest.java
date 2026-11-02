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
package cn.dev33.satoken.oauth2.data.model.oidc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * IdTokenModel 目前只有公开字段，赋值读回去就行
 */
public class IdTokenModelTest {

	/** 无参构造后各字段先是默认值 */
	@Test
	public void noArgCtor_defaults() {
		IdTokenModel m = new IdTokenModel();
		Assertions.assertNull(m.iss);
		Assertions.assertNull(m.sub);
		Assertions.assertNull(m.aud);
		Assertions.assertEquals(0, m.exp);
		Assertions.assertEquals(0, m.iat);
		Assertions.assertEquals(0, m.authTime);
		Assertions.assertNull(m.nonce);
		Assertions.assertNull(m.acr);
		Assertions.assertNull(m.amr);
		Assertions.assertNull(m.azp);
		Assertions.assertNull(m.extraData);
	}

	/** 公开字段写进去应该能原样读出来 */
	@Test
	public void publicFields_roundTrip() {
		Map<String, Object> extra = new LinkedHashMap<>();
		extra.put("name", "sa");
		IdTokenModel m = new IdTokenModel();
		m.iss = "http://iss";
		m.sub = 10001;
		m.aud = "1001";
		m.exp = 10;
		m.iat = 1;
		m.authTime = 2;
		m.nonce = "n";
		m.acr = "acr1";
		m.amr = "pwd";
		m.azp = "1001";
		m.extraData = extra;
		Assertions.assertEquals("http://iss", m.iss);
		Assertions.assertEquals(10001, m.sub);
		Assertions.assertEquals("1001", m.aud);
		Assertions.assertEquals(10, m.exp);
		Assertions.assertEquals(1, m.iat);
		Assertions.assertEquals(2, m.authTime);
		Assertions.assertEquals("n", m.nonce);
		Assertions.assertEquals("acr1", m.acr);
		Assertions.assertEquals("pwd", m.amr);
		Assertions.assertEquals("1001", m.azp);
		Assertions.assertSame(extra, m.extraData);
	}

}
