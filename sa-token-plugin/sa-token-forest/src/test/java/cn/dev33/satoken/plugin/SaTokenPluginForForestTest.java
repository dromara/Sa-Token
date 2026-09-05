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
import cn.dev33.satoken.http.SaHttpTemplate;
import cn.dev33.satoken.http.SaHttpTemplateForForest;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTokenPluginForForest} 插件安装行为测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTokenPluginForForestTest {

    private SaHttpTemplate backupTemplate;

    @BeforeEach
    public void backup() {
        // 记录安装前的全局模板，测完恢复，避免影响同 JVM 的其它用例
        backupTemplate = SaManager.getSaHttpTemplate();
    }

    @AfterEach
    public void restore() {
        SaManager.setSaHttpTemplate(backupTemplate);
    }

    /** install 之后全局 Http 模板应该变成 Forest 实现 */
    @Test
    public void install_setsSaHttpTemplateForForest() {
        new SaTokenPluginForForest().install();
        Assertions.assertTrue(SaManager.getSaHttpTemplate() instanceof SaHttpTemplateForForest);
    }

}
