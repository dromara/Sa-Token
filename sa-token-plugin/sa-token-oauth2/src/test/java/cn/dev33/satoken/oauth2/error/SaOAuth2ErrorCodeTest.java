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
package cn.dev33.satoken.oauth2.error;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * OAuth2 错误码常量要对得上文档里的 301xx
 */
public class SaOAuth2ErrorCodeTest {

	/** 30101 到 30115 应该就是这些固定数字 */
	@Test
	public void codes_30101_to_30115() {
		Assertions.assertEquals(30101, SaOAuth2ErrorCode.CODE_30101);
		Assertions.assertEquals(30102, SaOAuth2ErrorCode.CODE_30102);
		Assertions.assertEquals(30103, SaOAuth2ErrorCode.CODE_30103);
		Assertions.assertEquals(30104, SaOAuth2ErrorCode.CODE_30104);
		Assertions.assertEquals(30105, SaOAuth2ErrorCode.CODE_30105);
		Assertions.assertEquals(30106, SaOAuth2ErrorCode.CODE_30106);
		Assertions.assertEquals(30107, SaOAuth2ErrorCode.CODE_30107);
		Assertions.assertEquals(30108, SaOAuth2ErrorCode.CODE_30108);
		Assertions.assertEquals(30109, SaOAuth2ErrorCode.CODE_30109);
		Assertions.assertEquals(30110, SaOAuth2ErrorCode.CODE_30110);
		Assertions.assertEquals(30111, SaOAuth2ErrorCode.CODE_30111);
		Assertions.assertEquals(30112, SaOAuth2ErrorCode.CODE_30112);
		Assertions.assertEquals(30113, SaOAuth2ErrorCode.CODE_30113);
		Assertions.assertEquals(30114, SaOAuth2ErrorCode.CODE_30114);
		Assertions.assertEquals(30115, SaOAuth2ErrorCode.CODE_30115);
	}

	/** 30120 往后这几个散码也应该对得上 */
	@Test
	public void codes_30120_and_after() {
		Assertions.assertEquals(30120, SaOAuth2ErrorCode.CODE_30120);
		Assertions.assertEquals(30122, SaOAuth2ErrorCode.CODE_30122);
		Assertions.assertEquals(30125, SaOAuth2ErrorCode.CODE_30125);
		Assertions.assertEquals(30126, SaOAuth2ErrorCode.CODE_30126);
		Assertions.assertEquals(30127, SaOAuth2ErrorCode.CODE_30127);
		Assertions.assertEquals(30131, SaOAuth2ErrorCode.CODE_30131);
		Assertions.assertEquals(30132, SaOAuth2ErrorCode.CODE_30132);
		Assertions.assertEquals(30133, SaOAuth2ErrorCode.CODE_30133);
		Assertions.assertEquals(30134, SaOAuth2ErrorCode.CODE_30134);
		Assertions.assertEquals(30141, SaOAuth2ErrorCode.CODE_30141);
		Assertions.assertEquals(30142, SaOAuth2ErrorCode.CODE_30142);
		Assertions.assertEquals(30151, SaOAuth2ErrorCode.CODE_30151);
		Assertions.assertEquals(30161, SaOAuth2ErrorCode.CODE_30161);
		Assertions.assertEquals(30191, SaOAuth2ErrorCode.CODE_30191);
	}

}
