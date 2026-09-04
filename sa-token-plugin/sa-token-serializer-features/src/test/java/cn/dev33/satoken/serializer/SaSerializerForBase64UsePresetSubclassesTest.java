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
 * {@link SaSerializerForBase64UsePeriodicTable}、
 * {@link SaSerializerForBase64UseSpecialSymbols}、
 * {@link SaSerializerForBase64UseTianGan} 三个预置子类的编解码往返测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSerializerForBase64UsePresetSubclassesTest {

    /** 元素周期表子类：构造后应该能往返编解码 */
    @Test
    public void periodicTable_roundTrip() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UsePeriodicTable();
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        String encoded = s.bytesToString(data);
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** 特殊符号子类：构造后应该能往返编解码 */
    @Test
    public void specialSymbols_roundTrip() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseSpecialSymbols();
        byte[] data = "world".getBytes(StandardCharsets.UTF_8);
        String encoded = s.bytesToString(data);
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** 天干地支子类：构造后应该能往返编解码 */
    @Test
    public void tianGan_roundTrip() {
        SaSerializerForBase64UseCustomCharacters s = new SaSerializerForBase64UseTianGan();
        byte[] data = "sa-token".getBytes(StandardCharsets.UTF_8);
        String encoded = s.bytesToString(data);
        Assertions.assertArrayEquals(data, s.stringToBytes(encoded));
    }

    /** 三个子类的填充符都不在各自字符集里，构造时应该不抛异常 */
    @Test
    public void presetSubclasses_constructWithoutException() {
        Assertions.assertNotNull(new SaSerializerForBase64UsePeriodicTable());
        Assertions.assertNotNull(new SaSerializerForBase64UseSpecialSymbols());
        Assertions.assertNotNull(new SaSerializerForBase64UseTianGan());
    }
}
