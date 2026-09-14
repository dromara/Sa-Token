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
package cn.dev33.satoken.jwt.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaJwtException} 构造、setCode、throwBy 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaJwtExceptionTest {

    /** SaJwtException 携带 message 时应该能读回来 */
    @Test
    public void exception_message() {
        SaJwtException ex = new SaJwtException("boom");
        Assertions.assertEquals("boom", ex.getMessage());
    }

    /** SaJwtException 携带 cause 时应该能读回来 */
    @Test
    public void exception_cause() {
        Throwable cause = new RuntimeException("root");
        SaJwtException ex = new SaJwtException("boom", cause);
        Assertions.assertEquals("boom", ex.getMessage());
        Assertions.assertSame(cause, ex.getCause());
    }

    /** setCode 写入的异常细分状态码应该能读回来 */
    @Test
    public void exception_setCode() {
        SaJwtException ex = new SaJwtException("boom").setCode(30201);
        Assertions.assertEquals(30201, ex.getCode());
    }

    /** throwBy flag=true 时必须抛异常，flag=false 时应该不抛 */
    @Test
    public void exception_throwBy() {
        Assertions.assertThrows(SaJwtException.class, () -> SaJwtException.throwBy(true, "boom"));
        Assertions.assertDoesNotThrow(() -> SaJwtException.throwBy(false, "boom"));
    }

    /** throwByNull：value 为 null 或空串时必须抛出带 code 的异常，非空时应该不抛 */
    @Test
    public void exception_throwByNull() {
        SaJwtException e1 = Assertions.assertThrows(SaJwtException.class,
                () -> SaJwtException.throwByNull(null, "boom", 30205));
        Assertions.assertEquals(30205, e1.getCode());

        Assertions.assertThrows(SaJwtException.class,
                () -> SaJwtException.throwByNull("", "boom", 30205));

        Assertions.assertDoesNotThrow(() -> SaJwtException.throwByNull("key", "boom", 30205));
    }

}
