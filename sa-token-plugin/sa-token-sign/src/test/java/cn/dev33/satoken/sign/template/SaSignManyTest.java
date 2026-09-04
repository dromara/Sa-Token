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
package cn.dev33.satoken.sign.template;

import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.error.SaSignErrorCode;
import cn.dev33.satoken.sign.exception.SaSignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SaSignMany} 多实例签名总控行为测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSignManyTest {

    private Map<String, SaSignConfig> backupSignMany;

    @BeforeEach
    public void backup() {
        backupSignMany = SaSignManager.getSignMany();
    }

    @AfterEach
    public void restore() {
        SaSignManager.setSignMany(backupSignMany);
        // 恢复默认查找函数
        SaSignMany.findSaSignConfigMethod = (appid) -> SaSignManager.getSignMany().get(appid);
    }

    /** getSignTemplate appid 为空时返回全局默认 SaSignTemplate */
    @Test
    public void getSignTemplate_emptyAppid_returnsGlobalTemplate() {
        Assertions.assertSame(SaSignManager.getSaSignTemplate(), SaSignMany.getSignTemplate(""));
        Assertions.assertSame(SaSignManager.getSaSignTemplate(), SaSignMany.getSignTemplate(null));
    }

    /** getSignTemplate 命中 appid 时返回带该配置的 SaSignTemplate 实例 */
    @Test
    public void getSignTemplate_hitAppid_returnsConfiguredTemplate() {
        Map<String, SaSignConfig> map = new LinkedHashMap<>();
        map.put("app1", new SaSignConfig().setSecretKey("k1"));
        SaSignManager.setSignMany(map);

        SaSignTemplate t = SaSignMany.getSignTemplate("app1");
        Assertions.assertEquals("k1", t.getSecretKey());
        Assertions.assertNotSame(SaSignManager.getSaSignTemplate(), t);
    }

    /** getSignTemplate 未命中 appid 时抛 CODE_12211 */
    @Test
    public void getSignTemplate_missAppid_throws() {
        SaSignManager.setSignMany(new LinkedHashMap<>());
        SaSignException ex = Assertions.assertThrows(SaSignException.class,
                () -> SaSignMany.getSignTemplate("not-exist"));
        Assertions.assertEquals(SaSignErrorCode.CODE_12211, ex.getCode());
        Assertions.assertTrue(ex.getMessage().contains("not-exist"));
    }

    /** findSaSignConfigMethod 可被自定义替换 */
    @Test
    public void findSaSignConfigMethod_custom() {
        SaSignConfig custom = new SaSignConfig().setSecretKey("custom-key");
        SaSignMany.findSaSignConfigMethod = (appid) -> "custom-app".equals(appid) ? custom : null;
        Assertions.assertEquals("custom-key", SaSignMany.getSignTemplate("custom-app").getSecretKey());
    }

    /** 默认构造函数可实例化 */
    @Test
    public void constructor_instantiable() {
        Assertions.assertNotNull(new SaSignMany());
    }
}
