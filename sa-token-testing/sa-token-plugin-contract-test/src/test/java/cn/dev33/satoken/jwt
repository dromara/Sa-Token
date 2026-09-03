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
package cn.dev33.satoken.jwt;

import cn.dev33.satoken.jwt.error.SaJwtErrorCode;
import cn.dev33.satoken.jwt.exception.SaJwtException;
import cn.hutool.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * SaJwtTemplate 单元测试
 */
public class SaJwtTemplateTest {

	private static final String KEYT = "abcdefghijklmnopqrstuvwxyz";

	private final SaJwtTemplate template = new SaJwtTemplate();

	@Test
	public void createToken_shouldAllowCustomExtraData() {
		Map<String, Object> extraData = new HashMap<>();
		extraData.put("tenant", "t1");

		String token = template.createToken("login", 10001, "pc", 3600, extraData, KEYT);
		JSONObject payloads = template.getPayloads(token, "login", KEYT);

		Assertions.assertEquals("10001", payloads.getStr(SaJwtTemplate.LOGIN_ID));
		Assertions.assertEquals("t1", payloads.getStr("tenant"));
	}

	@Test
	public void createToken_shouldRejectReservedKeyInExtraData_simpleMode() {
		Map<String, Object> extraData = Collections.singletonMap(SaJwtTemplate.LOGIN_ID, "admin");

		SaJwtException e = Assertions.assertThrows(SaJwtException.class, () ->
				template.createToken("login", 10001, extraData, KEYT)
		);
		Assertions.assertEquals(SaJwtErrorCode.CODE_30207, e.getCode());
	}

	@Test
	public void createToken_shouldRejectReservedKeyInExtraData_fullMode() {
		Map<String, Object> extraData = Collections.singletonMap(SaJwtTemplate.EFF, -1L);

		SaJwtException e = Assertions.assertThrows(SaJwtException.class, () ->
				template.createToken("login", 10001, "pc", 3600, extraData, KEYT)
		);
		Assertions.assertEquals(SaJwtErrorCode.CODE_30207, e.getCode());
	}

	@Test
	public void createToken_shouldNotOverwriteLoginIdWhenExtraDataIsSafe() {
		String token = template.createToken("login", 10001, "pc", 3600, null, KEYT);
		JSONObject payloads = template.getPayloads(token, "login", KEYT);

		Assertions.assertEquals("10001", payloads.getStr(SaJwtTemplate.LOGIN_ID));
	}

}
