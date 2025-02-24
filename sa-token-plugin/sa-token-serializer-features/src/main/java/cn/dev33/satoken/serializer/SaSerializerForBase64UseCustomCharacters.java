/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.serializer;

import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdk;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器，base64 算法，采用自定义字符集
 * 
 * @author click33
 * @since 1.41.0
 */
public class SaSerializerForBase64UseCustomCharacters implements SaSerializerTemplateForJdk {
	
	// 自定义字符集，需确保包含64个中文字符
	public String CUSTOM_CHARS;

	// 填充符，确保不在字符集中
	public char PAD_CHAR;

	public SaSerializerForBase64UseCustomCharacters(String customChars, char padChar) {
		if (customChars.length() != 64) {
			throw new IllegalArgumentException("自定义字符集长度必须为64");
		}
		if (customChars.indexOf(padChar) != -1) {
			throw new IllegalArgumentException("填充符不能在自定义字符集中");
		}
		this.CUSTOM_CHARS = customChars;
		this.PAD_CHAR = padChar;
	}

	@Override
	public String bytesToString(byte[] data) {

		StringBuilder encoded = new StringBuilder();
		int length = data.length;
		int i = 0;

		// 处理完整的3字节组
		while (i < length - 2) {
			int byte1 = data[i++] & 0xFF;
			int byte2 = data[i++] & 0xFF;
			int byte3 = data[i++] & 0xFF;

			int combined = (byte1 << 16) | (byte2 << 8) | byte3;
			encoded.append(CUSTOM_CHARS.charAt((combined >> 18) & 0x3F));
			encoded.append(CUSTOM_CHARS.charAt((combined >> 12) & 0x3F));
			encoded.append(CUSTOM_CHARS.charAt((combined >> 6) & 0x3F));
			encoded.append(CUSTOM_CHARS.charAt(combined & 0x3F));
		}

		// 处理剩余字节（0、1或2个）
		int remaining = length - i;
		if (remaining > 0) {
			int byte1 = data[i++] & 0xFF;
			int byte2 = remaining > 1 ? data[i++] & 0xFF : 0;

			int combined = (byte1 << 16) | (byte2 << 8);
			encoded.append(CUSTOM_CHARS.charAt((combined >> 18) & 0x3F));
			encoded.append(CUSTOM_CHARS.charAt((combined >> 12) & 0x3F));

			if (remaining == 1) {
				encoded.append(PAD_CHAR).append(PAD_CHAR);
			} else {
				encoded.append(CUSTOM_CHARS.charAt((combined >> 6) & 0x3F));
				encoded.append(PAD_CHAR);
			}
		}

		return encoded.toString();
	}

	@Override
	public byte[] stringToBytes(String encodedStr) {
		if (CUSTOM_CHARS.length() != 64) {
			throw new IllegalStateException("自定义字符集长度必须为64");
		}

		Map<Character, Integer> charMap = new HashMap<>();
		for (int i = 0; i < CUSTOM_CHARS.length(); i++) {
			charMap.put(CUSTOM_CHARS.charAt(i), i);
		}

		int length = encodedStr.length();
		if (length % 4 != 0) {
			throw new IllegalArgumentException("编码字符串长度无效");
		}

		// 计算填充符数量
		int paddingCount = 0;
		for (int i = length - 1; i >= 0 && encodedStr.charAt(i) == PAD_CHAR; i--) {
			paddingCount++;
		}

		int numGroups = length / 4;
		byte[] decoded = new byte[numGroups * 3 - paddingCount];
		int decodedIndex = 0;

		for (int group = 0; group < numGroups; group++) {
			int[] indices = new int[4];
			for (int j = 0; j < 4; j++) {
				char c = encodedStr.charAt(group * 4 + j);
				if (c == PAD_CHAR) {
					indices[j] = 0; // 填充符处理为0，后续根据paddingCount调整
				} else {
					Integer index = charMap.get(c);
					if (index == null) {
						throw new IllegalArgumentException("无效字符: " + c);
					}
					indices[j] = index;
				}
			}

			int combined = (indices[0] << 18) | (indices[1] << 12) | (indices[2] << 6) | indices[3];
			for (int k = 0; k < 3; k++) {
				if (decodedIndex < decoded.length) {
					decoded[decodedIndex++] = (byte) ((combined >> (16 - 8 * k)) & 0xFF);
				}
			}
		}

		return decoded;
	}
}
