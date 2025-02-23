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
package cn.dev33.satoken.util;

/**
 * 十六进制工具类
 *
 * @author deepseek
 * @since 2025/2/24
 */
public class SaHexUtil {

    // 十六进制字符表（大写）
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * 将字节数组转换为十六进制字符串（JDK8兼容）
     * @param bytes 要转换的字节数组
     * @return 十六进制字符串（大写）
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return null;
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 将十六进制字符串转换为字节数组（JDK8兼容）
     * @param hexString 有效的十六进制字符串（不区分大小写）
     * @return 对应的字节数组
     * @throws IllegalArgumentException 输入字符串格式错误时抛出异常
     */
    public static byte[] hexToBytes(String hexString) {
        if (hexString == null) return null;
        int len = hexString.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length");
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(hexString.charAt(i), 16);
            int low = Character.digit(hexString.charAt(i+1), 16);

            if (high == -1 || low == -1) {
                throw new IllegalArgumentException(
                        "Invalid hex character at position " + i + " or " + (i+1)
                );
            }

            data[i/2] = (byte) ((high << 4) + low);
        }
        return data;
    }

}