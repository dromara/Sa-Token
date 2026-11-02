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
package cn.dev33.satoken.quick;

import cn.dev33.satoken.quick.config.SaQuickConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaQuickManager} 全局配置读写与 auto 随机账号测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaQuickManagerTest {

    private SaQuickConfig backup;

    /** 先把全局配置存一份，避免弄脏别的用例 */
    @BeforeEach
    public void backup() {
        backup = SaQuickManager.getConfig();
    }

    /** 把全局配置恢复回去 */
    @AfterEach
    public void restore() {
        SaQuickManager.setConfig(backup);
    }

    /** 还没人 setConfig 时，getConfig 应该现场 new 一份默认配置 */
    @Test
    public void getConfig_lazyInitsDefault() {
        SaQuickConfig cfg = SaQuickManager.getConfig();
        Assertions.assertNotNull(cfg);
        Assertions.assertEquals("sa", cfg.getName());
        Assertions.assertEquals("123456", cfg.getPwd());
        Assertions.assertFalse(cfg.getAuto());
    }

    /** getConfig 多次调用应该返回同一个实例 */
    @Test
    public void getConfig_returnsSameInstance() {
        Assertions.assertSame(SaQuickManager.getConfig(), SaQuickManager.getConfig());
    }

    /** 应测尽测：测试 SaQuickManager 无参构造 */
    @Test
    public void constructor_canNew() {
        Assertions.assertNotNull(new SaQuickManager());
    }

    /** auto=false 时 setConfig 不应该改掉手动设置的账号密码 */
    @Test
    public void setConfig_autoFalse_keepsNamePwd() {
        SaQuickConfig cfg = new SaQuickConfig();
        cfg.setAuto(false);
        cfg.setName("keep");
        cfg.setPwd("keep-pwd");
        SaQuickManager.setConfig(cfg);
        Assertions.assertSame(cfg, SaQuickManager.getConfig());
        Assertions.assertEquals("keep", cfg.getName());
        Assertions.assertEquals("keep-pwd", cfg.getPwd());
    }

    /** auto=true 时 setConfig 应该随机生成账号密码 */
    @Test
    public void setConfig_autoTrue_randomizesNamePwd() {
        SaQuickConfig cfg = new SaQuickConfig();
        cfg.setAuto(true);
        cfg.setName("will-be-replaced");
        cfg.setPwd("will-be-replaced");
        SaQuickManager.setConfig(cfg);
        Assertions.assertEquals(8, cfg.getName().length());
        Assertions.assertEquals(8, cfg.getPwd().length());
        Assertions.assertNotEquals("will-be-replaced", cfg.getName());
        Assertions.assertNotEquals("will-be-replaced", cfg.getPwd());
    }

}
