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
package cn.dev33.satoken.secure;

import java.nio.charset.StandardCharsets;

/**
 * Sa-Token Base32 工具类
 *
 * @author click33
 * @since 1.42.0
 */
public class SaBase32Util {

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int[] BASE32_LOOKUP = new int[256];

    static {
        // 初始化解码查找表
        for (int i = 0; i < BASE32_CHARS.length(); i++) {
            char c = BASE32_CHARS.charAt(i);
            BASE32_LOOKUP[c] = i;
            // 支持小写字母解码
            if (c >= 'A' && c <= 'Z') {
                BASE32_LOOKUP[Character.toLowerCase(c)] = i;
            }
        }
    }

    /**
     * Base32 编码（byte[] 转 String）
     */
    public static String encodeBytesToString(byte[] bytes) {
        if (bytes == null) return null;

        StringBuilder result = new StringBuilder();
        int buffer = 0;
        int bufferSize = 0;

        for (byte b : bytes) {
            buffer = (buffer << 8) | (b & 0xFF);
            bufferSize += 8;

            while (bufferSize >= 5) {
                bufferSize -= 5;
                int index = (buffer >> bufferSize) & 0x1F;
                result.append(BASE32_CHARS.charAt(index));
            }
        }

        // 处理剩余位
        if (bufferSize > 0) {
            int index = (buffer << (5 - bufferSize)) & 0x1F;
            result.append(BASE32_CHARS.charAt(index));
        }

        return result.toString();
    }

    /**
     * Base32 解码（String 转 byte[]）
     */
    public static byte[] decodeStringToBytes(String text) {
        if (text == null) return null;

        text = text.replaceAll("=", "").trim();
        if (text.isEmpty()) return new byte[0];

        int buffer = 0;
        int bufferSize = 0;
        int byteCount = (text.length() * 5 + 7) / 8;
        byte[] bytes = new byte[byteCount];
        int byteIndex = 0;

        for (char c : text.toCharArray()) {
            int value = BASE32_LOOKUP[c];
            if (value == 0 && c != 'A') continue; // 跳过非法字符

            buffer = (buffer << 5) | value;
            bufferSize += 5;

            if (bufferSize >= 8) {
                bufferSize -= 8;
                bytes[byteIndex++] = (byte) ((buffer >> bufferSize) & 0xFF);
            }
        }

        // 处理最后一个字节
        if (bufferSize > 0) {
            bytes[byteIndex] = (byte) ((buffer << (8 - bufferSize)) & 0xFF);
        }

        return bytes;
    }

    /**
     * Base32 编码（String 转 String）
     */
    public static String encode(String text) {
        if (text == null) return null;
        return encodeBytesToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base32 解码（String 转 String）
     */
    public static String decode(String base32Text) {
        if (base32Text == null) return null;
        byte[] bytes = decodeStringToBytes(base32Text);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
