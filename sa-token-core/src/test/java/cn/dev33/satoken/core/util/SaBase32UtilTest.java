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
package cn.dev33.satoken.core.util;

import cn.dev33.satoken.secure.SaBase32Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaBase32Util Base32 编解码测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaBase32UtilTest {

	/** encode/decode 应对字符串正确编解码 */
	@Test
	void encodeAndDecodeString() {
		String encoded = SaBase32Util.encode("Hi");
		Assertions.assertNotNull(encoded);
		Assertions.assertFalse(encoded.isEmpty());
		Assertions.assertEquals("Hi", SaBase32Util.decode(encoded).replace("\0", ""));
	}

	/** encodeBytesToString/decodeStringToBytes 应对字节数组正确编解码 */
	@Test
	void encodeBytesAndDecodeBytes() {
		byte[] bytes = new byte[] {0x48, 0x69};
		String encoded = SaBase32Util.encodeBytesToString(bytes);
		byte[] decoded = SaBase32Util.decodeStringToBytes(encoded);
		Assertions.assertEquals("Hi", new String(decoded, java.nio.charset.StandardCharsets.UTF_8).replace("\0", ""));
	}

	/** null 入参应返回 null */
	@Test
	void nullInput() {
		Assertions.assertNull(SaBase32Util.encode(null));
		Assertions.assertNull(SaBase32Util.decode(null));
		Assertions.assertNull(SaBase32Util.encodeBytesToString(null));
		Assertions.assertNull(SaBase32Util.decodeStringToBytes(null));
	}

}

