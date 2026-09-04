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
package cn.dev33.satoken.sign.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SaSignManyConfigWrapper} getter/setter/toString 行为测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSignManyConfigWrapperTest {

    /** 默认 signMany 为空非 null map */
    @Test
    public void defaultSignMany_emptyMap() {
        SaSignManyConfigWrapper wrapper = new SaSignManyConfigWrapper();
        Assertions.assertNotNull(wrapper.getSignMany());
        Assertions.assertTrue(wrapper.getSignMany().isEmpty());
    }

    /** setSignMany 写入后 getSignMany 读取同一实例 */
    @Test
    public void setSignMany_readBack() {
        Map<String, SaSignConfig> map = new LinkedHashMap<>();
        map.put("app1", new SaSignConfig().setSecretKey("k1"));
        SaSignManyConfigWrapper wrapper = new SaSignManyConfigWrapper();
        wrapper.setSignMany(map);
        Assertions.assertSame(map, wrapper.getSignMany());
        Assertions.assertEquals("k1", wrapper.getSignMany().get("app1").getSecretKey());
    }

    /** toString 包含 signMany 字段 */
    @Test
    public void toString_containsSignMany() {
        SaSignManyConfigWrapper wrapper = new SaSignManyConfigWrapper();
        wrapper.getSignMany().put("app1", new SaSignConfig().setSecretKey("k1"));
        String str = wrapper.toString();
        Assertions.assertTrue(str.contains("signMany"));
        Assertions.assertTrue(str.contains("app1"));
    }
}
