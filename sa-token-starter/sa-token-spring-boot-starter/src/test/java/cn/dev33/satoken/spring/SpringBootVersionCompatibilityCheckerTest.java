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
package cn.dev33.satoken.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringBootVersion;

/**
 * {@link SpringBootVersionCompatibilityChecker} 在 Boot2 测试环境下应该能正常构造
 */
public class SpringBootVersionCompatibilityCheckerTest {

    /** Boot2 依赖下构造时不应该抛异常（版本以 2. 开头会走兼容分支） */
    @Test
    public void construct_onBoot2_ok() {
        Assertions.assertDoesNotThrow(() -> new SpringBootVersionCompatibilityChecker());
    }

    /** 空版本或 1.x 版本应该视为兼容并直接返回 */
    @Test
    public void construct_onEmptyOrVersion1_ok() {
        try (MockedStatic<SpringBootVersion> mocked = Mockito.mockStatic(SpringBootVersion.class)) {
            mocked.when(SpringBootVersion::getVersion).thenReturn("");
            Assertions.assertDoesNotThrow(() -> new SpringBootVersionCompatibilityChecker());

            mocked.when(SpringBootVersion::getVersion).thenReturn("1.5.22");
            Assertions.assertDoesNotThrow(() -> new SpringBootVersionCompatibilityChecker());
        }
    }

    /** Boot3/4 版本应该抛出 SaTokenException 提醒替换 starter */
    @Test
    public void construct_onBoot3_shouldThrow() {
        try (MockedStatic<SpringBootVersion> mocked = Mockito.mockStatic(SpringBootVersion.class)) {
            mocked.when(SpringBootVersion::getVersion).thenReturn("3.2.0");

            cn.dev33.satoken.exception.SaTokenException ex = Assertions.assertThrows(
                    cn.dev33.satoken.exception.SaTokenException.class,
                    () -> new SpringBootVersionCompatibilityChecker());

            Assertions.assertTrue(ex.getMessage().contains("不兼容"));
        }
    }

}
