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
package cn.dev33.satoken.jfinal;

import com.jfinal.plugin.redis.serializer.JdkSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

/**
 * {@link SaJdkSerializer} JDK 序列化测试
 */
public class SaJdkSerializerTest {

    /** key 编解码应该能来回转成原字符串 */
    @Test
    public void key_roundTrip() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        byte[] bytes = serializer.keyToBytes("satoken:login:1");
        Assertions.assertEquals("satoken:login:1", serializer.keyFromBytes(bytes));
    }

    /** 普通可序列化对象应该能 value 来回转 */
    @Test
    public void value_roundTrip() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        Payload payload = new Payload("zhang", 18);
        Object restored = serializer.valueFromBytes(serializer.valueToBytes(payload));
        Assertions.assertEquals(payload, restored);
    }

    /** field 编解码应该跟 value 走同一套 */
    @Test
    public void field_roundTrip() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        Assertions.assertEquals("name", serializer.fieldFromBytes(serializer.fieldToBytes("name")));
    }

    /** bytes 是 null 或空数组时，valueFromBytes 应该直接返回 null */
    @Test
    public void valueFromBytes_nullOrEmpty() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        Assertions.assertNull(serializer.valueFromBytes(null));
        Assertions.assertNull(serializer.valueFromBytes(new byte[0]));
    }

    /** 不能序列化的对象写进去，应该包成 RuntimeException 抛出来 */
    @Test
    public void valueToBytes_notSerializable_shouldThrow() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        Assertions.assertThrows(RuntimeException.class, () -> serializer.valueToBytes(new Object()));
    }

    /** 随便塞一串坏字节，读出来也应该炸 */
    @Test
    public void valueFromBytes_invalidBytes_shouldThrow() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        Assertions.assertThrows(RuntimeException.class, () -> serializer.valueFromBytes(new byte[] {1, 2, 3}));
    }

    /** me 字段当前指向的是 JFinal 自带的 JdkSerializer */
    @Test
    public void me_isJfinalJdkSerializer() {
        Assertions.assertTrue(SaJdkSerializer.me instanceof JdkSerializer);
    }

    /** 给序列化来回转用的小对象 */
    public static class Payload implements Serializable {
        private static final long serialVersionUID = 1L;
        public String name;
        public int age;

        public Payload(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Payload)) {
                return false;
            }
            Payload other = (Payload) obj;
            return age == other.age && name.equals(other.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode() * 31 + age;
        }
    }
}
