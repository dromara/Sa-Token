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
package cn.dev33.satoken.plugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTokenPluginForSerializerFeatures} 插件安装行为测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenPluginForSerializerFeaturesTest {

    /** install 默认不注册任何组件，执行时应该不抛异常 */
    @Test
    public void install_doesNotThrow() {
        Assertions.assertDoesNotThrow(() -> new SaTokenPluginForSerializerFeatures().install());
    }
}
