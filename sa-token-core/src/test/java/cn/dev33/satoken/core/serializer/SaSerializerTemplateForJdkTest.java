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
package cn.dev33.satoken.core.serializer;

import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseHex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SaSerializerTemplateForJdk JDK 序列化 round-trip 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSerializerTemplateForJdkTest {

	/** objectToString/stringToObject 应对 Map 完成 Hex 编码往返 */
	@Test
	void stringRoundTrip() {
		SaSerializerTemplateForJdkUseHex serializer = new SaSerializerTemplateForJdkUseHex();
		Map<String, Object> source = new LinkedHashMap<>();
		source.put("name", "lisi");
		source.put("score", 99);

		String encoded = serializer.objectToString(source);
		Assertions.assertNotNull(encoded);
		Assertions.assertFalse(encoded.isEmpty());

		@SuppressWarnings("unchecked")
		Map<String, Object> restored = (Map<String, Object>) serializer.stringToObject(encoded);
		Assertions.assertEquals("lisi", restored.get("name"));
		Assertions.assertEquals(99, restored.get("score"));
	}

	/** objectToBytes/bytesToObject 应往返还原对象，null 入参应返回 null */
	@Test
	void bytesRoundTripAndNull() {
		SaSerializerTemplateForJdkUseHex serializer = new SaSerializerTemplateForJdkUseHex();
		SimplePayload payload = new SimplePayload("msg", 7);

		byte[] bytes = serializer.objectToBytes(payload);
		SimplePayload restored = (SimplePayload) serializer.bytesToObject(bytes);
		Assertions.assertEquals("msg", restored.message);
		Assertions.assertEquals(7, restored.count);

		Assertions.assertNull(serializer.objectToString(null));
		Assertions.assertNull(serializer.stringToObject(null));
		Assertions.assertNull(serializer.objectToBytes(null));
		Assertions.assertNull(serializer.bytesToObject(null));
	}

	private static class SimplePayload implements Serializable {
		private static final long serialVersionUID = 1L;
		private final String message;
		private final int count;

		SimplePayload(String message, int count) {
			this.message = message;
			this.count = count;
		}
	}

}
