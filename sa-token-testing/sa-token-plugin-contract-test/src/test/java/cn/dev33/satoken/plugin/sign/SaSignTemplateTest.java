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
package cn.dev33.satoken.plugin.sign;

import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API 接口签名测试
 *
 * @author click33
 * @since 2022-9-2
 */
public class SaSignTemplateTest {

    private static final String KEY = "SwqFmsKxcbq23";

    private static Map<String, Object> sampleParams() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "zhang");
        map.put("age", 18);
        map.put("sex", "女");
        return map;
    }

    @Test
    public void testJoinParamsDictSort() {
        String str = SaSignManager.getSaSignTemplate().joinParamsDictSort(sampleParams());
        Assertions.assertEquals("age=18&name=zhang&sex=女", str);
    }

    @Test
    public void testCreateSign() {
        SaSignManager.getSaSignTemplate().setSignConfig(new SaSignConfig().setSecretKey(KEY));
        String sign = SaSignManager.getSaSignTemplate().createSign(sampleParams());
        Assertions.assertEquals("6f5e844a53e74363c2f6b24f64c4f0ff", sign);
        Assertions.assertEquals(sign, SaSignManager.getSaSignTemplate().createSign(sampleParams()));
    }

}
