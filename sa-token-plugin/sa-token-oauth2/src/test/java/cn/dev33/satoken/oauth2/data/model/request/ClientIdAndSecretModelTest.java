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
package cn.dev33.satoken.oauth2.data.model.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ClientIdAndSecret：无参/两参构造、getter/setter、toString
 */
public class ClientIdAndSecretModelTest {

	/** 无参构造应该能 new 出来，字段先是空的 */
	@Test
	public void noArgCtor_fieldsNull() {
		ClientIdAndSecretModel m = new ClientIdAndSecretModel();
		Assertions.assertNull(m.getClientId());
		Assertions.assertNull(m.getClientSecret());
	}

	/** 两参构造应该把 id 和 secret 塞进去 */
	@Test
	public void twoArgCtor_setsFields() {
		ClientIdAndSecretModel m = new ClientIdAndSecretModel("1001", "sec");
		Assertions.assertEquals("1001", m.getClientId());
		Assertions.assertEquals("sec", m.getClientSecret());
	}

	/** setter 应该连缀写回 */
	@Test
	public void gettersAndSetters_roundTrip() {
		ClientIdAndSecretModel m = new ClientIdAndSecretModel()
				.setClientId("c")
				.setClientSecret("s");
		Assertions.assertEquals("c", m.getClientId());
		Assertions.assertEquals("s", m.getClientSecret());
	}

	/** toString 里应该能看到 id 和 secret */
	@Test
	public void toString_containsFields() {
		String text = new ClientIdAndSecretModel("1001", "sec").toString();
		Assertions.assertTrue(text.startsWith("ClientIdAndSecretModel{"));
		Assertions.assertTrue(text.contains("clientId='1001'"));
		Assertions.assertTrue(text.contains("clientSecret='sec'"));
	}

}
