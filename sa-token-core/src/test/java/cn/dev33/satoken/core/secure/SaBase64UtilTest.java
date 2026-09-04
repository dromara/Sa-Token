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
package cn.dev33.satoken.core.secure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import cn.dev33.satoken.secure.SaBase64Util;
/**
 * SaBase64Util 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaBase64UtilTest {
	/** encode/decode 应对含中文的字符串正确编解码 */
	@Test
	public void encodeAndDecode() {
		String text = "Sa-Token 一个轻量级java权限认证框架";
		String base64Text = SaBase64Util.encode(text);
		Assertions.assertEquals("U2EtVG9rZW4g5LiA5Liq6L276YeP57qnamF2Yeadg+mZkOiupOivgeahhuaetg==", base64Text);
		String text2 = SaBase64Util.decode(base64Text);
		Assertions.assertEquals(text, text2);
	}
}
