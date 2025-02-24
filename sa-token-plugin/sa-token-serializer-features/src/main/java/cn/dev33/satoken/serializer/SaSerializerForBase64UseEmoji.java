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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 序列化器，base64 算法，采用 64 个 Emoji 小黄脸作为元字符集，无填充字符
 *
 * @author click33
 * @since 1.41.0
 */
public class SaSerializerForBase64UseEmoji implements SaSerializerTemplateForJdk {

	private final List<String> EMOJI_TABLE = new ArrayList<>();  // 编码表
	private final Map<String, Integer> EMOJI_MAP = new HashMap<>();  // 解码表

	public SaSerializerForBase64UseEmoji() {
		// 初始化编码表（64个Emoji，U+1F600 到 U+1F63F）
		for (int i = 0; i < 64; i++) {
			int codePoint = 0x1F600 + i;
			String emoji = new String(Character.toChars(codePoint));
			EMOJI_TABLE.add(emoji);
			EMOJI_MAP.put(emoji, i);
		}
	}

	@Override
	public String bytesToString(byte[] data) {
		StringBuilder binaryStr = new StringBuilder();
		for (byte b : data) {
			binaryStr.append(String.format("%8s", Integer.toBinaryString(b & 0xFF))
					.replace(' ', '0'));
		}

		// 补零到6的倍数
		int bitLength = binaryStr.length();
		int paddingBits = (6 - (bitLength % 6)) % 6;
		for (int i = 0; i < paddingBits; i++) {
			binaryStr.append('0');
		}

		// 转换为索引
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < binaryStr.length(); i += 6) {
			String chunk = binaryStr.substring(i, Math.min(i + 6, binaryStr.length()));
			indices.add(Integer.parseInt(chunk, 2));
		}

		// 拼接Emoji
		StringBuilder result = new StringBuilder();
		for (int index : indices) {
			result.append(EMOJI_TABLE.get(index));
		}
		return result.toString();
	}

	@Override
	public byte[] stringToBytes(String encoded) {
		List<Integer> indices = new ArrayList<>();

		// 提取索引（每个Emoji占2个char）
		for (int i = 0; i < encoded.length(); ) {
			if (i + 1 >= encoded.length()) break;
			String emoji = encoded.substring(i, i + 2);
			i += 2;

			Integer index = EMOJI_MAP.get(emoji);
			if (index == null) {
				throw new IllegalArgumentException("非法Emoji: " + emoji);
			}
			indices.add(index);
		}

		// 转换为二进制字符串
		StringBuilder binaryStr = new StringBuilder();
		for (int index : indices) {
			binaryStr.append(String.format("%6s", Integer.toBinaryString(index))
					.replace(' ', '0'));
		}

		// 转换为字节数组（自动处理末尾补零）
		List<Byte> bytes = new ArrayList<>();
		for (int i = 0; i < binaryStr.length(); i += 8) {
			int endIndex = Math.min(i + 8, binaryStr.length());
			String byteStr = binaryStr.substring(i, endIndex);
			if (byteStr.length() < 8) break; // 忽略末尾不足8位的部分
			bytes.add((byte) Integer.parseInt(byteStr, 2));
		}

		byte[] result = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			result[i] = bytes.get(i);
		}
		return result;
	}

}
