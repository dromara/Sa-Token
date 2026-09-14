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
package cn.dev33.satoken.jboot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

/**
 * {@link SaJdkSerializer} JDK 序列化测试
 */
public class SaJdkSerializerTest {

    /** 普通可序列化对象应该能来回转 */
    @Test
    public void serialize_roundTrip() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        Payload payload = new Payload("zhang", 18);
        Object restored = serializer.deserialize(serializer.serialize(payload));
        Assertions.assertEquals(payload, restored);
    }

    /** serialize(null) 应该直接返回 null */
    @Test
    public void serialize_null_shouldReturnNull() {
        Assertions.assertNull(new SaJdkSerializer().serialize(null));
    }

    /** bytes 是 null 或空数组时，deserialize 应该直接返回 null */
    @Test
    public void deserialize_nullOrEmpty() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        Assertions.assertNull(serializer.deserialize(null));
        Assertions.assertNull(serializer.deserialize(new byte[0]));
    }

    /** 不能序列化的对象写进去，应该包成 RuntimeException 抛出来 */
    @Test
    public void serialize_notSerializable_shouldThrow() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        Assertions.assertThrows(RuntimeException.class, () -> serializer.serialize(new Object()));
    }

    /** 随便塞一串坏字节，读出来也应该炸 */
    @Test
    public void deserialize_invalidBytes_shouldThrow() {
        SaJdkSerializer serializer = new SaJdkSerializer();
        Assertions.assertThrows(RuntimeException.class, () -> serializer.deserialize(new byte[] {1, 2, 3}));
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
