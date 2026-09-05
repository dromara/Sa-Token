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
 * {@link SaBeanRegister} Bean 注册测试
 */
public class SaBeanRegisterTest {

    /** 应该能注册出配置对象和上下文路径加载器 */
    @Test
    public void registerBeans_shouldCreateObjects() {
        SaBeanRegister register = new SaBeanRegister();

        Assertions.assertNotNull(register.getSaTokenConfig());
        ApplicationContextPathLoading loading = register.getApplicationContextPathLoading();
        Assertions.assertNotNull(loading);
    }

}
