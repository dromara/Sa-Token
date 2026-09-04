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
 * {@link SaSerializerForBase64UseEmoji} 编解码往返与异常分支测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSerializerForBase64UseEmojiTest {

    /** bytesToString 传空字节数组时应该返回空字符串 */
    @Test
    public void bytesToString_empty_returnsEmpty() {
        Assertions.assertEquals("", new SaSerializerForBase64UseEmoji().bytesToString(new byte[]{}));
    }

    /** stringToBytes 传空字符串时应该返回空字节数组 */
    @Test
    public void stringToBytes_empty_returnsEmpty() {
        Assertions.assertArrayEquals(new byte[0], new SaSerializerForBase64UseEmoji().stringToBytes(""));
    }

    /** 编解码往返：1 个字节时应该能还原 */
    @Test
    public void roundTrip_oneByte() {
        SaSerializerForBase64UseEmoji s = new SaSerializerForBase64UseEmoji();
        byte[] data = new byte[]{(byte) 0xFF};
        String encoded = s.bytesToString(data);
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** 编解码往返：3 个字节时应该能还原 */
    @Test
    public void roundTrip_threeBytes() {
        SaSerializerForBase64UseEmoji s = new SaSerializerForBase64UseEmoji();
        byte[] data = "abc".getBytes(StandardCharsets.UTF_8);
        String encoded = s.bytesToString(data);
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** 编解码往返：多字节时应该能还原 */
    @Test
    public void roundTrip_multiBytes() {
        SaSerializerForBase64UseEmoji s = new SaSerializerForBase64UseEmoji();
        byte[] data = "hello sa-token".getBytes(StandardCharsets.UTF_8);
        String encoded = s.bytesToString(data);
        Assertions.assertTrue(encoded.length() > 0);
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** stringToBytes 遇到非法 emoji 时必须抛 IllegalArgumentException */
    @Test
    public void stringToBytes_invalidEmoji_throws() {
        SaSerializerForBase64UseEmoji s = new SaSerializerForBase64UseEmoji();
        // "zz" 不在 emoji 表中
        Assertions.assertThrows(IllegalArgumentException.class, () -> s.stringToBytes("zz"));
    }

    /** stringToBytes 单个 char（奇数长度）时应该安全跳出，返回空字节数组 */
    @Test
    public void stringToBytes_singleChar_returnsEmpty() {
        SaSerializerForBase64UseEmoji s = new SaSerializerForBase64UseEmoji();
        Assertions.assertArrayEquals(new byte[0], s.stringToBytes("a"));
    }

    /** objectToString 和 stringToObject 应该能通过 JDK 序列化往返还原 */
    @Test
    public void objectToString_roundTrip() {
        SaSerializerForBase64UseEmoji s = new SaSerializerForBase64UseEmoji();
        Object result = s.stringToObject(s.objectToString("hello"));
        Assertions.assertEquals("hello", result);
    }
}
