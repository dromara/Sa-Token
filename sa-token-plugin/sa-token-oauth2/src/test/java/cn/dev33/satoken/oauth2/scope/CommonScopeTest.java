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
package cn.dev33.satoken.oauth2.scope;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 常见 Scope 字面量要对得上
 */
public class CommonScopeTest {

	/** openid / unionid / userid / oidc 应该就是这些固定串 */
	@Test
	public void constants_keepLiteralValues() {
		Assertions.assertEquals("openid", CommonScope.OPENID);
		Assertions.assertEquals("unionid", CommonScope.UNIONID);
		Assertions.assertEquals("userid", CommonScope.USERID);
		Assertions.assertEquals("oidc", CommonScope.OIDC);
	}

}
