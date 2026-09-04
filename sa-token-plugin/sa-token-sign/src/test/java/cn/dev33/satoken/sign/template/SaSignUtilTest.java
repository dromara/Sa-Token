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

import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.template.SaSignUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SaSignUtil} 全部静态方法委托给全局 SaSignTemplate 的行为测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaSignUtilTest {

    private static final String KEY = "SwqFmsKxcbq23";

    @BeforeEach
    public void setupTemplate() {
        // 给全局 SaSignTemplate 配置秘钥，使 SaSignUtil 委托调用可正常工作
        SaSignManager.setSaSignTemplate(SaSignManager.getSaSignTemplate().setSignConfig(new SaSignConfig().setSecretKey(KEY)));
    }

    private static Map<String, Object> sampleParams() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "zhang");
        map.put("age", 18);
        return map;
    }

    /** joinParams 和 joinParamsDictSort 应该委托给全局模板 */
    @Test
    public void joinParams_delegateToGlobalTemplate() {
        Assertions.assertEquals("name=zhang&age=18", SaSignUtil.joinParams(sampleParams()));
        Assertions.assertEquals("age=18&name=zhang", SaSignUtil.joinParamsDictSort(sampleParams()));
    }

    /** createSign 应该委托给全局模板 */
    @Test
    public void createSign_delegateToGlobalTemplate() {
        Assertions.assertEquals(SaSignManager.getSaSignTemplate().createSign(sampleParams()),
                SaSignUtil.createSign(sampleParams()));
    }

    /** addSignParams 和 addSignParamsAndJoin 应该委托给全局模板 */
    @Test
    public void addSignParams_delegateToGlobalTemplate() {
        Map<String, Object> params = new LinkedHashMap<>(sampleParams());
        Map<String, Object> result = SaSignUtil.addSignParams(params);
        Assertions.assertTrue(result.containsKey("timestamp"));
        Assertions.assertTrue(result.containsKey("nonce"));
        Assertions.assertTrue(result.containsKey("sign"));

        String joined = SaSignUtil.addSignParamsAndJoin(new LinkedHashMap<>(sampleParams()));
        Assertions.assertTrue(joined.contains("timestamp="));
        Assertions.assertTrue(joined.contains("sign="));
    }

    /** isValidTimestamp 和 checkTimestamp 应该委托给全局模板 */
    @Test
    public void timestamp_delegateToGlobalTemplate() {
        Assertions.assertTrue(SaSignUtil.isValidTimestamp(System.currentTimeMillis()));
        Assertions.assertFalse(SaSignUtil.isValidTimestamp(System.currentTimeMillis() - 1000 * 60 * 20));
        Assertions.assertDoesNotThrow(() -> SaSignUtil.checkTimestamp(System.currentTimeMillis()));
    }

    /** isValidNonce 和 checkNonce 应该委托给全局模板 */
    @Test
    public void nonce_delegateToGlobalTemplate() {
        Assertions.assertTrue(SaSignUtil.isValidNonce("util-fresh-nonce"));
        Assertions.assertDoesNotThrow(() -> SaSignUtil.checkNonce("util-once-nonce"));
        Assertions.assertFalse(SaSignUtil.isValidNonce("util-once-nonce"));
    }

    /** isValidSign 和 checkSign 应该委托给全局模板 */
    @Test
    public void sign_delegateToGlobalTemplate() {
        String sign = SaSignUtil.createSign(sampleParams());
        Assertions.assertTrue(SaSignUtil.isValidSign(sampleParams(), sign));
        Assertions.assertDoesNotThrow(() -> SaSignUtil.checkSign(sampleParams(), sign));
    }

    /** isValidParamMap 和 checkParamMap 应该委托给全局模板 */
    @Test
    public void paramMap_delegateToGlobalTemplate() {
        Map<String, Object> signed = SaSignUtil.addSignParams(new LinkedHashMap<>(sampleParams()));
        Map<String, String> paramMap = new LinkedHashMap<>();
        signed.forEach((k, v) -> paramMap.put(k, String.valueOf(v)));
        Assertions.assertTrue(SaSignUtil.isValidParamMap(paramMap));
        Assertions.assertDoesNotThrow(() -> SaSignUtil.checkParamMap(paramMap));
    }

    /** isValidRequest 和 checkRequest 应该委托给全局模板 */
    @Test
    public void request_delegateToGlobalTemplate() {
        Map<String, Object> signed = SaSignUtil.addSignParams(new LinkedHashMap<>(sampleParams()));
        Map<String, String> paramMap = new LinkedHashMap<>();
        signed.forEach((k, v) -> paramMap.put(k, String.valueOf(v)));
        SaRequestForMock request = new SaRequestForMock();
        request.parameterMap = paramMap;
        Assertions.assertTrue(SaSignUtil.isValidRequest(request));
        Assertions.assertDoesNotThrow(() -> SaSignUtil.checkRequest(request));
    }

    /** 默认构造函数应该能 new 出来 */
    @Test
    public void constructor_instantiable() {
        Assertions.assertNotNull(new SaSignUtil());
    }
}
