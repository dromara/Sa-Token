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

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaBeanInject} 构造与插件持有者注入测试
 */
@SaTokenTest
public class SaBeanInjectTest {

    /** 未注入 pluginHolder 时应该回退到默认实例并完成初始化 */
    @Test
    public void construct_withoutPluginHolder_useDefaultHolder() {
        SaTokenPluginHolder before = SaTokenPluginHolder.instance;

        Assertions.assertDoesNotThrow(() -> new SaBeanInject(null, new SaTokenConfig(), null));

        Assertions.assertNotNull(SaTokenPluginHolder.instance);
        Assertions.assertSame(before, SaTokenPluginHolder.instance);
    }

    /** 注入自定义 pluginHolder 时应该使用该实例 */
    @Test
    public void construct_withCustomPluginHolder_useInjectedHolder() {
        SaTokenPluginHolder custom = new SaTokenPluginHolder();

        new SaBeanInject(null, new SaTokenConfig(), custom);

        Assertions.assertSame(custom, SaTokenPluginHolder.instance);
    }

    /** 注入 log 时应该写入 SaManager */
    @Test
    public void construct_withLog_shouldSetLog() {
        SaLog log = new SaLogForConsole();

        new SaBeanInject(log, new SaTokenConfig(), null);

        Assertions.assertSame(log, cn.dev33.satoken.SaManager.getLog());
    }

}
