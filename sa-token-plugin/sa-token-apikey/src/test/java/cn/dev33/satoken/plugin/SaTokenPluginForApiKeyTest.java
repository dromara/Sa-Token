/*
 * Copyright 2020-2099 sa-token.com
 *
 * Licensed under the Apache License, Version  2.0 (the "License");
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
package cn.dev33.satoken.plugin;

import cn.dev33.satoken.apikey.annotation.SaCheckApiKey;
import cn.dev33.satoken.apikey.annotation.handle.SaCheckApiKeyHandler;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTokenPluginForApiKey} 插件安装行为测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenPluginForApiKeyTest {

    @AfterEach
    public void cleanup() {
        SaAnnotationStrategy.instance.removeAnnotationHandler(SaCheckApiKey.class);
    }

    /** install 应该把 SaCheckApiKeyHandler 注册到 SaAnnotationStrategy */
    @Test
    public void install_registersSaCheckApiKeyHandler() {
        new SaTokenPluginForApiKey().install();
        Object handler = SaAnnotationStrategy.instance.annotationHandlerMap.get(SaCheckApiKey.class);
        Assertions.assertNotNull(handler);
        Assertions.assertTrue(handler instanceof SaCheckApiKeyHandler);
    }
}
