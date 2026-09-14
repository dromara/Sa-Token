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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoForHutoolTimedCache;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTokenPluginForHutoolCache} 插件安装行为测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTokenPluginForHutoolCacheTest {

    /** install 之后全局 DAO 应该变成 Hutool-TimedCache 实现 */
    @Test
    public void install_setsSaTokenDao() {
        new SaTokenPluginForHutoolCache().install();
        Assertions.assertTrue(SaManager.getSaTokenDao() instanceof SaTokenDaoForHutoolTimedCache);
    }

}
