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

import cn.dev33.satoken.spring.context.path.ApplicationContextPathLoading;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaBeanRegister} 上下文路径加载器测试（默认 SaTokenConfig 工厂由 Boot 集成冒烟覆盖）
 */
public class SaBeanRegisterTest {

    /** 上下文路径加载器工厂应该能 new 出来 */
    @Test
    public void getApplicationContextPathLoading_notNull() {
        ApplicationContextPathLoading loading = new SaBeanRegister().getApplicationContextPathLoading();
        Assertions.assertNotNull(loading);
    }

}
