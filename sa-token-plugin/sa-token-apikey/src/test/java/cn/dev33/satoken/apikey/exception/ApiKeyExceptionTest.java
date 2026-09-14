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
package cn.dev33.satoken.apikey.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link ApiKeyException} 与 {@link ApiKeyScopeException} 构造、setter、throwBy 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class ApiKeyExceptionTest {

    /** ApiKeyException 携带 message 时应该能读回来 */
    @Test
    public void apiKeyException_message() {
        ApiKeyException ex = new ApiKeyException("boom");
        Assertions.assertEquals("boom", ex.getMessage());
    }

    /** ApiKeyException 携带 cause 时应该能读回来 */
    @Test
    public void apiKeyException_cause() {
        Throwable cause = new RuntimeException("root");
        ApiKeyException ex = new ApiKeyException(cause);
        Assertions.assertSame(cause, ex.getCause());
    }

    /** setApiKey/getApiKey 读写应该一致 */
    @Test
    public void apiKeyException_setApiKey() {
        ApiKeyException ex = new ApiKeyException("m").setApiKey("AK-1");
        Assertions.assertEquals("AK-1", ex.getApiKey());
        Assertions.assertEquals("AK-1", ex.apiKey);
    }

    /** throwBy flag=true 时必须抛异常，flag=false 时应该不抛 */
    @Test
    public void apiKeyException_throwBy() {
        Assertions.assertThrows(ApiKeyException.class,
                () -> ApiKeyException.throwBy(true, "m", 1));
        Assertions.assertDoesNotThrow(() -> ApiKeyException.throwBy(false, "m", 1));
    }

    /** ApiKeyScopeException 携带 message 时应该是 ApiKeyException 的子类 */
    @Test
    public void apiKeyScopeException_message() {
        ApiKeyScopeException ex = new ApiKeyScopeException("no scope");
        Assertions.assertEquals("no scope", ex.getMessage());
        Assertions.assertTrue(ex instanceof ApiKeyException);
    }

    /** ApiKeyScopeException 携带 cause 时应该能读回来 */
    @Test
    public void apiKeyScopeException_cause() {
        Throwable cause = new RuntimeException("root");
        Assertions.assertSame(cause, new ApiKeyScopeException(cause).getCause());
    }

    /** setApiKey/setScope/getApiKey/getScope 读写应该一致 */
    @Test
    public void apiKeyScopeException_setters() {
        ApiKeyScopeException ex = new ApiKeyScopeException("m").setApiKey("AK-1").setScope("read");
        Assertions.assertEquals("AK-1", ex.getApiKey());
        Assertions.assertEquals("read", ex.getScope());
    }

    /** throwBy flag=true 时必须抛异常，flag=false 时应该不抛 */
    @Test
    public void apiKeyScopeException_throwBy() {
        Assertions.assertThrows(ApiKeyScopeException.class,
                () -> ApiKeyScopeException.throwBy(true, "m", 1));
        Assertions.assertDoesNotThrow(() -> ApiKeyScopeException.throwBy(false, "m", 1));
    }
}
