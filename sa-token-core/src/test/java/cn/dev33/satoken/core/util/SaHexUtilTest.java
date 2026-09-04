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

import cn.dev33.satoken.util.SaHexUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHexUtil 十六进制工具测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaHexUtilTest {

	/** bytesToHex 与 hexToBytes 应互逆，且支持大小写 */
	@Test
	void bytesToHex_and_hexToBytes() {
		byte[] bytes = new byte[] {0x00, 0x0F, (byte) 0xA5, (byte) 0xFF};
		Assertions.assertEquals("000FA5FF", SaHexUtil.bytesToHex(bytes));
		Assertions.assertArrayEquals(bytes, SaHexUtil.hexToBytes("000FA5FF"));
		Assertions.assertArrayEquals(bytes, SaHexUtil.hexToBytes("000fa5ff"));
	}

	/** null 入参应返回 null */
	@Test
	void nullInput() {
		Assertions.assertNull(SaHexUtil.bytesToHex(null));
		Assertions.assertNull(SaHexUtil.hexToBytes(null));
	}

	/** 非法十六进制字符串应抛出 IllegalArgumentException */
	@Test
	void invalidHexString() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> SaHexUtil.hexToBytes("ABC"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> SaHexUtil.hexToBytes("GG"));
	}

}

