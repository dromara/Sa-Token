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
package cn.dev33.satoken.sign;

import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SaSignManager} 全局组件 lazy init 与读写测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSignManagerTest {

    private SaSignConfig backupConfig;
    private Map<String, SaSignConfig> backupSignMany;
    private SaSignTemplate backupTemplate;

    @BeforeEach
    public void backup() {
        backupConfig = SaSignManager.getConfig();
        backupSignMany = SaSignManager.getSignMany();
        backupTemplate = SaSignManager.getSaSignTemplate();
    }

    @AfterEach
    public void restore() {
        SaSignManager.setConfig(backupConfig);
        SaSignManager.setSignMany(backupSignMany);
        SaSignManager.setSaSignTemplate(backupTemplate);
    }

    /** getConfig 首次调用会 lazy init 默认 SaSignConfig */
    @Test
    public void getConfig_lazyInit() {
        // 通过反射无法重置 static volatile，这里验证多次调用返回同一实例
        SaSignConfig c1 = SaSignManager.getConfig();
        SaSignConfig c2 = SaSignManager.getConfig();
        Assertions.assertSame(c1, c2);
    }

    /** setConfig 写入后 getConfig 读取同一实例 */
    @Test
    public void setConfig_readBack() {
        SaSignConfig config = new SaSignConfig().setSecretKey("mgr-key");
        SaSignManager.setConfig(config);
        Assertions.assertSame(config, SaSignManager.getConfig());
    }

    /** getSignMany 首次调用 lazy init 非 null map */
    @Test
    public void getSignMany_lazyInit() {
        Assertions.assertNotNull(SaSignManager.getSignMany());
    }

    /** setSignMany 写入后 getSignMany 读取同一实例 */
    @Test
    public void setSignMany_readBack() {
        Map<String, SaSignConfig> map = new LinkedHashMap<>();
        map.put("app1", new SaSignConfig().setSecretKey("k1"));
        SaSignManager.setSignMany(map);
        Assertions.assertSame(map, SaSignManager.getSignMany());
        Assertions.assertEquals("k1", SaSignManager.getSignMany().get("app1").getSecretKey());
    }

    /** getSaSignTemplate 首次调用 lazy init 默认 SaSignTemplate */
    @Test
    public void getSaSignTemplate_lazyInit() {
        SaSignTemplate t1 = SaSignManager.getSaSignTemplate();
        SaSignTemplate t2 = SaSignManager.getSaSignTemplate();
        Assertions.assertSame(t1, t2);
    }

    /** setSaSignTemplate 写入后 getSaSignTemplate 读取同一实例 */
    @Test
    public void setSaSignTemplate_readBack() {
        SaSignTemplate template = new SaSignTemplate(new SaSignConfig().setSecretKey("t-key"));
        SaSignManager.setSaSignTemplate(template);
        Assertions.assertSame(template, SaSignManager.getSaSignTemplate());
    }

    /** 默认构造函数可实例化 */
    @Test
    public void constructor_instantiable() {
        Assertions.assertNotNull(new SaSignManager());
    }
}
