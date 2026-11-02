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

import cn.dev33.satoken.httpauth.digest.SaHttpDigestModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpDigestModel 参数实体测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaHttpDigestModelTest {

	/** 构造方法与 DEFAULT_REALM、DEFAULT_QOP 常量应正确 */
	@Test
	void constructorsAndConstants() {
		Assertions.assertEquals("Sa-Token", SaHttpDigestModel.DEFAULT_REALM);
		Assertions.assertEquals("auth", SaHttpDigestModel.DEFAULT_QOP);

		SaHttpDigestModel empty = new SaHttpDigestModel();
		Assertions.assertEquals(SaHttpDigestModel.DEFAULT_REALM, empty.getRealm());

		SaHttpDigestModel withUser = new SaHttpDigestModel("sa", "123456");
		Assertions.assertEquals("sa", withUser.getUsername());
		Assertions.assertEquals("123456", withUser.getPassword());

		SaHttpDigestModel withRealm = new SaHttpDigestModel("sa", "123456", "MyRealm");
		Assertions.assertEquals("MyRealm", withRealm.getRealm());
	}

	/** getter/setter 与链式赋值应正确读写各 Digest 字段 */
	@Test
	void gettersSettersAndChainMethods() {
		SaHttpDigestModel model = new SaHttpDigestModel()
				.setUsername("zhangsan")
				.setPassword("secret")
				.setRealm("app-realm")
				.setNonce("nonce-001")
				.setUri("/api/data")
				.setMethod("GET")
				.setQop("auth")
				.setNc("00000001")
				.setCnonce("client-nonce")
				.setOpaque("opaque-value")
				.setResponse("digest-response");

		Assertions.assertEquals("zhangsan", model.getUsername());
		Assertions.assertEquals("secret", model.getPassword());
		Assertions.assertEquals("app-realm", model.getRealm());
		Assertions.assertEquals("nonce-001", model.getNonce());
		Assertions.assertEquals("/api/data", model.getUri());
		Assertions.assertEquals("GET", model.getMethod());
		Assertions.assertEquals("auth", model.getQop());
		Assertions.assertEquals("00000001", model.getNc());
		Assertions.assertEquals("client-nonce", model.getCnonce());
		Assertions.assertEquals("opaque-value", model.getOpaque());
		Assertions.assertEquals("digest-response", model.getResponse());

		Assertions.assertTrue(model.toString().contains("username=zhangsan"));
		Assertions.assertTrue(model.toString().contains("response=digest-response"));
	}

}
