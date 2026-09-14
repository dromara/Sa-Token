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
package cn.dev33.satoken.sign.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaSignException} 构造与断言方法行为测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSignExceptionTest {

    /** 构造函数携带 message 时应该能读回来 */
    @Test
    public void constructor_message() {
        SaSignException ex = new SaSignException("boom");
        Assertions.assertEquals("boom", ex.getMessage());
    }

    /** notTrue flag 为 true 时必须抛异常，为 false 时应该不抛 */
    @Test
    public void notTrue_throwsWhenFlagTrue() {
        Assertions.assertThrows(SaSignException.class, () -> SaSignException.notTrue(true, "true-case"));
        Assertions.assertDoesNotThrow(() -> SaSignException.notTrue(false, "false-case"));
    }

    /** notEmpty value 为空时必须抛异常，非空时应该不抛 */
    @Test
    public void notEmpty_throwsWhenEmpty() {
        Assertions.assertThrows(SaSignException.class, () -> SaSignException.notEmpty(null, "null-case"));
        Assertions.assertThrows(SaSignException.class, () -> SaSignException.notEmpty("", "empty-case"));
        Assertions.assertDoesNotThrow(() -> SaSignException.notEmpty("value", "non-empty-case"));
    }

    /** throwBy (deprecated) flag 为 true 时必须抛异常 */
    @Test
    @SuppressWarnings("deprecation")
    public void throwBy_throwsWhenFlagTrue() {
        Assertions.assertThrows(SaSignException.class, () -> SaSignException.throwBy(true, "throwBy-case"));
        Assertions.assertDoesNotThrow(() -> SaSignException.throwBy(false, "throwBy-case"));
    }

    /** throwByNull (deprecated) value 为空时必须抛异常 */
    @Test
    @SuppressWarnings("deprecation")
    public void throwByNull_throwsWhenEmpty() {
        Assertions.assertThrows(SaSignException.class, () -> SaSignException.throwByNull(null, "null-case"));
        Assertions.assertThrows(SaSignException.class, () -> SaSignException.throwByNull("", "empty-case"));
        Assertions.assertDoesNotThrow(() -> SaSignException.throwByNull("value", "non-empty-case"));
    }
}
