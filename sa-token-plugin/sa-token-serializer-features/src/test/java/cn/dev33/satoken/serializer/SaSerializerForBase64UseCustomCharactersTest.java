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
package cn.dev33.satoken.serializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * {@link SaSerializerForBase64UseCustomCharacters} 构造校验与编解码往返测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSerializerForBase64UseCustomCharactersTest {

    /** 标准 base64 字符集加填充符 =，方便构造合法的编码串 */
    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final char PAD = '=';

    /** 构造函数字符集长度不是 64 时必须抛 IllegalArgumentException */
    @Test
    public void constructor_charsLengthNot64_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SaSerializerForBase64UseCustomCharacters("abc", PAD));
    }

    /** 构造函数填充符在字符集里时必须抛 IllegalArgumentException */
    @Test
    public void constructor_padCharInChars_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SaSerializerForBase64UseCustomCharacters(CHARS, 'A'));
    }

    /** 构造函数参数合法时应该正常实例化并把字段存起来 */
    @Test
    public void constructor_validArgs_storesFields() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseCustomCharacters(CHARS, PAD);
        Assertions.assertEquals(CHARS, s.CUSTOM_CHARS);
        Assertions.assertEquals(PAD, s.PAD_CHAR);
    }

    /** bytesToString 传空字节数组时应该返回空字符串 */
    @Test
    public void bytesToString_empty_returnsEmpty() {
        Assertions.assertEquals("", new SaSerializerForBase64UseCustomCharacters(CHARS, PAD).bytesToString(new byte[]{}));
    }

    /** stringToBytes 传空字符串时应该返回空字节数组 */
    @Test
    public void stringToBytes_empty_returnsEmpty() {
        Assertions.assertArrayEquals(new byte[0],
                new SaSerializerForBase64UseCustomCharacters(CHARS, PAD).stringToBytes(""));
    }

    /** 编解码往返：1 个字节时应该产生 2 个填充符 */
    @Test
    public void roundTrip_oneByte() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseCustomCharacters(CHARS, PAD);
        byte[] data = new byte[]{(byte) 0xFF};
        String encoded = s.bytesToString(data);
        Assertions.assertEquals(4, encoded.length());
        Assertions.assertEquals(2, countPad(encoded));
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** 编解码往返：2 个字节时应该产生 1 个填充符 */
    @Test
    public void roundTrip_twoBytes() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseCustomCharacters(CHARS, PAD);
        byte[] data = "ab".getBytes(StandardCharsets.UTF_8);
        String encoded = s.bytesToString(data);
        Assertions.assertEquals(4, encoded.length());
        Assertions.assertEquals(1, countPad(encoded));
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** 编解码往返：3 个字节时应该没有填充 */
    @Test
    public void roundTrip_threeBytes() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseCustomCharacters(CHARS, PAD);
        byte[] data = "abc".getBytes(StandardCharsets.UTF_8);
        String encoded = s.bytesToString(data);
        Assertions.assertEquals(4, encoded.length());
        Assertions.assertEquals(0, countPad(encoded));
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** 编解码往返：6 个字节时应该是两组且没有填充 */
    @Test
    public void roundTrip_sixBytes() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseCustomCharacters(CHARS, PAD);
        byte[] data = "abcdef".getBytes(StandardCharsets.UTF_8);
        String encoded = s.bytesToString(data);
        Assertions.assertEquals(8, encoded.length());
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** stringToBytes 编码串长度不是 4 的倍数时必须抛 IllegalArgumentException */
    @Test
    public void stringToBytes_lengthNotMultipleOf4_throws() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseCustomCharacters(CHARS, PAD);
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.stringToBytes("abc"));
    }

    /** stringToBytes 编码串里有无效字符时必须抛 IllegalArgumentException */
    @Test
    public void stringToBytes_invalidChar_throws() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseCustomCharacters(CHARS, PAD);
        // '?' 不在字符集中也不是填充符
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.stringToBytes("abc?"));
    }

    /** objectToString 和 stringToObject 应该能通过 JDK 序列化往返还原 */
    @Test
    public void objectToString_roundTrip() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseCustomCharacters(CHARS, PAD);
        String result = (String) s.stringToObject(s.objectToString("hello"));
        Assertions.assertEquals("hello", result);
    }

    private static int countPad(String s) {
        int n = 0;
        for (int i = s.length() - 1; i >= 0 && s.charAt(i) == PAD; i--) {
            n++;
        }
        return n;
    }
}
