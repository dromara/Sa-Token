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
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.error.SaSignErrorCode;
import cn.dev33.satoken.sign.exception.SaSignException;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * API 接口签名 {@link SaSignTemplate} 行为测试：拼接参数 / 创建签名 / 追加签名参数 / 时间戳与 nonce 校验
 *
 * @author click33
 * @since 2022-9-2
 */
@SaTokenTest
public class SaSignTemplateTest {

    private static final String KEY = "SwqFmsKxcbq23";

    /** 构造一个已配置秘钥的签名模板实例，避免污染全局 SaSignManager */
    private static SaSignTemplate template() {
        return new SaSignTemplate(new SaSignConfig().setSecretKey(KEY));
    }

    /** 示例参数（按插入顺序：name、age、sex） */
    private static Map<String, Object> sampleParams() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "zhang");
        map.put("age", 18);
        map.put("sex", "女");
        return map;
    }

    /** joinParams 不排序，按传入顺序拼接，空值被跳过，末尾 & 被删除 */
    @Test
    public void joinParams_keepInsertionOrderAndSkipEmpty() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "zhang");
        map.put("age", 18);
        map.put("sex", "女");
        map.put("empty", "");
        Assertions.assertEquals("name=zhang&age=18&sex=女", template().joinParams(map));
        Assertions.assertEquals("", template().joinParams(new LinkedHashMap<>()));
    }

    /** joinParamsDictSort 按字典序拼接参数，TreeMap 入参不再重新包装 */
    @Test
    public void joinParamsDictSort_sortByDictOrder() {
        Assertions.assertEquals("age=18&name=zhang&sex=女",
                template().joinParamsDictSort(sampleParams()));
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("b", "2");
        treeMap.put("a", "1");
        Assertions.assertEquals("a=1&b=2", template().joinParamsDictSort(treeMap));
    }

    /** createSign 对相同参数生成稳定的 md5 签名，含 sign 字段时会被排除 */
    @Test
    public void createSign_stableAndExcludeSignKey() {
        SaSignTemplate t = template();
        String sign = t.createSign(sampleParams());
        Assertions.assertEquals("6f5e844a53e74363c2f6b24f64c4f0ff", sign);
        Assertions.assertEquals(sign, t.createSign(sampleParams()));
        Map<String, Object> withSign = new LinkedHashMap<>(sampleParams());
        withSign.put(SaSignTemplate.sign, "should-be-ignored");
        Assertions.assertEquals(sign, t.createSign(withSign));
    }

    /** createSign 秘钥为空时通过继承的 SaTokenException.notEmpty 抛出带 CODE_12201 的异常 */
    @Test
    public void createSign_secretKeyEmpty_throws() {
        SaSignTemplate t = new SaSignTemplate(new SaSignConfig());
        cn.dev33.satoken.exception.SaTokenException ex = Assertions.assertThrows(
                cn.dev33.satoken.exception.SaTokenException.class,
                () -> t.createSign(sampleParams()));
        Assertions.assertEquals(SaSignErrorCode.CODE_12201, ex.getCode());
    }

    /** digestFullStr 走配置的摘要算法函数 */
    @Test
    public void digestFullStr_usesConfigDigestMethod() {
        SaSignTemplate t = new SaSignTemplate(new SaSignConfig().setSecretKey(KEY).setDigestAlgo("sha1"));
        Assertions.assertEquals(t.getSignConfigOrGlobal().digestMethod.run("abc"), t.digestFullStr("abc"));
    }

    /** addSignParams 追加 timestamp、nonce、sign 三个字段，addSignParamsAndJoin 拼接为字符串 */
    @Test
    public void addSignParams_appendsAndJoins() {
        SaSignTemplate t = template();
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("data", "hello");
        Map<String, Object> result = t.addSignParams(new LinkedHashMap<>(params));
        Assertions.assertTrue(result.containsKey(SaSignTemplate.timestamp));
        Assertions.assertTrue(result.containsKey(SaSignTemplate.nonce));
        Assertions.assertTrue(result.containsKey(SaSignTemplate.sign));
        Assertions.assertEquals(32, String.valueOf(result.get(SaSignTemplate.nonce)).length());

        // sign 是基于含 timestamp、nonce 的参数算出的，移除 sign 后重新算应一致
        Map<String, Object> withoutSign = new LinkedHashMap<>(result);
        withoutSign.remove(SaSignTemplate.sign);
        Assertions.assertEquals(t.createSign(withoutSign), result.get(SaSignTemplate.sign));

        String joined = t.addSignParamsAndJoin(new LinkedHashMap<>(params));
        Assertions.assertTrue(joined.contains("data=hello"));
        Assertions.assertTrue(joined.contains("timestamp="));
        Assertions.assertTrue(joined.contains("nonce="));
        Assertions.assertTrue(joined.contains("sign="));
    }

    /** isValidTimestamp 当前时间在范围内为 true，超出为 false，配置 -1 时恒为 true */
    @Test
    public void isValidTimestamp_rangeAndDisable() {
        SaSignTemplate t = template();
        Assertions.assertTrue(t.isValidTimestamp(System.currentTimeMillis()));
        Assertions.assertFalse(t.isValidTimestamp(System.currentTimeMillis() - 1000 * 60 * 20));
        SaSignTemplate disable = new SaSignTemplate(new SaSignConfig().setSecretKey(KEY).setTimestampDisparity(-1));
        Assertions.assertTrue(disable.isValidTimestamp(0L));
        Assertions.assertTrue(disable.isValidTimestamp(System.currentTimeMillis() - 1000000L));
    }

    /** checkTimestamp 超出范围抛出 CODE_12203，在范围内不抛异常 */
    @Test
    public void checkTimestamp_outOfRangeThrows() {
        long past = System.currentTimeMillis() - 1000 * 60 * 20;
        SaSignException ex = Assertions.assertThrows(SaSignException.class,
                () -> template().checkTimestamp(past));
        Assertions.assertEquals(SaSignErrorCode.CODE_12203, ex.getCode());
        Assertions.assertDoesNotThrow(() -> template().checkTimestamp(System.currentTimeMillis()));
    }

    /** isValidNonce 空为 false，未使用为 true，同一 nonce 可多次判断有效（不缓存） */
    @Test
    public void isValidNonce_emptyAndFreshAndRepeatable() {
        SaSignTemplate t = template();
        Assertions.assertFalse(t.isValidNonce(""));
        Assertions.assertFalse(t.isValidNonce(null));
        Assertions.assertTrue(t.isValidNonce("fresh-nonce-1"));
        Assertions.assertTrue(t.isValidNonce("fresh-nonce-1"));
    }

    /** checkNonce 空抛异常，通过后再次校验同一 nonce 抛异常 */
    @Test
    public void checkNonce_emptyAndSecondTimeThrows() {
        SaSignTemplate t = template();
        Assertions.assertThrows(SaSignException.class, () -> t.checkNonce(""));
        t.checkNonce("once-nonce-1");
        Assertions.assertThrows(SaSignException.class, () -> t.checkNonce("once-nonce-1"));
    }

    /** isValidSign 正确签名 true，错误签名 false；checkSign 无效抛 CODE_12202，有效不抛 */
    @Test
    public void isValidSign_andCheckSign() {
        SaSignTemplate t = template();
        String sign = t.createSign(sampleParams());
        Assertions.assertTrue(t.isValidSign(sampleParams(), sign));
        Assertions.assertFalse(t.isValidSign(sampleParams(), "wrong-sign"));
        SaSignException ex = Assertions.assertThrows(SaSignException.class,
                () -> t.checkSign(sampleParams(), "wrong-sign"));
        Assertions.assertEquals(SaSignErrorCode.CODE_12202, ex.getCode());
        Assertions.assertDoesNotThrow(() -> t.checkSign(sampleParams(), sign));
    }

    /** 构造一组带 timestamp/nonce/sign 的合法请求参数 */
    private Map<String, String> buildValidParamMap(SaSignTemplate t) {
        Map<String, Object> signed = t.addSignParams(sampleParams());
        Map<String, String> result = new LinkedHashMap<>();
        signed.forEach((k, v) -> result.put(k, String.valueOf(v)));
        return result;
    }

    /** isValidParamMap 缺 timestamp/sign 返回 false，全部合法返回 true，timestamp 超出或签名错误返回 false */
    @Test
    public void isValidParamMap_branches() {
        SaSignTemplate t = template();
        Map<String, String> valid = buildValidParamMap(t);
        Assertions.assertTrue(t.isValidParamMap(valid));

        Map<String, String> noTimestamp = new LinkedHashMap<>(valid);
        noTimestamp.remove(SaSignTemplate.timestamp);
        Assertions.assertFalse(t.isValidParamMap(noTimestamp));

        Map<String, String> noSign = new LinkedHashMap<>(valid);
        noSign.remove(SaSignTemplate.sign);
        Assertions.assertFalse(t.isValidParamMap(noSign));

        Map<String, String> outOfRange = new LinkedHashMap<>(valid);
        outOfRange.put(SaSignTemplate.timestamp, String.valueOf(System.currentTimeMillis() - 1000 * 60 * 20));
        outOfRange.put(SaSignTemplate.sign, t.createSign(outOfRange));
        Assertions.assertFalse(t.isValidParamMap(outOfRange));

        Map<String, String> wrongSign = new LinkedHashMap<>(valid);
        wrongSign.put(SaSignTemplate.sign, "wrong-sign");
        Assertions.assertFalse(t.isValidParamMap(wrongSign));
    }

    /** checkParamMap 缺 timestamp/nonce/sign 分别抛对应异常，全部合法时不抛 */
    @Test
    public void checkParamMap_missingAndValid() {
        SaSignTemplate t = template();
        Map<String, String> valid = buildValidParamMap(t);
        Assertions.assertDoesNotThrow(() -> t.checkParamMap(valid));

        Map<String, String> noTimestamp = new LinkedHashMap<>(valid);
        noTimestamp.remove(SaSignTemplate.timestamp);
        Assertions.assertTrue(Assertions.assertThrows(SaSignException.class,
                () -> t.checkParamMap(noTimestamp)).getMessage().contains("timestamp"));

        Map<String, String> noNonce = new LinkedHashMap<>(valid);
        noNonce.remove(SaSignTemplate.nonce);
        Assertions.assertTrue(Assertions.assertThrows(SaSignException.class,
                () -> t.checkParamMap(noNonce)).getMessage().contains("nonce"));

        Map<String, String> noSign = new LinkedHashMap<>(valid);
        noSign.remove(SaSignTemplate.sign);
        Assertions.assertTrue(Assertions.assertThrows(SaSignException.class,
                () -> t.checkParamMap(noSign)).getMessage().contains("sign"));
    }

    /** isValidRequest 不指定 paramNames 校验全部参数，指定 paramNames 只取指定参数参与签名 */
    @Test
    public void isValidRequest_allAndSpecifiedParams() {
        SaSignTemplate t = template();
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("data", "hello");
        Map<String, Object> signed = t.addSignParams(params);
        Map<String, String> paramMap = new LinkedHashMap<>();
        signed.forEach((k, v) -> paramMap.put(k, String.valueOf(v)));

        SaRequestForMock request = new SaRequestForMock();
        request.parameterMap = paramMap;
        Assertions.assertTrue(t.isValidRequest(request));

        // 指定 paramNames 时，extra 参数不参与签名校验
        Map<String, String> withExtra = new LinkedHashMap<>(paramMap);
        withExtra.put("extra", "not-signed");
        SaRequestForMock request2 = new SaRequestForMock();
        request2.parameterMap = withExtra;
        Assertions.assertTrue(t.isValidRequest(request2, "data"));
        Assertions.assertFalse(t.isValidRequest(request2));
    }

    /** checkRequest 全部合法不抛异常，签名错误抛异常 */
    @Test
    public void checkRequest_validAndInvalid() {
        SaSignTemplate t = template();
        Map<String, String> paramMap = buildValidParamMap(t);
        SaRequestForMock request = new SaRequestForMock();
        request.parameterMap = paramMap;
        Assertions.assertDoesNotThrow(() -> t.checkRequest(request));

        paramMap.put(SaSignTemplate.sign, "wrong-sign");
        SaRequestForMock badRequest = new SaRequestForMock();
        badRequest.parameterMap = paramMap;
        Assertions.assertThrows(SaSignException.class, () -> t.checkRequest(badRequest));
    }

    /** splicingNonceSaveKey 拼接为 {tokenName}:sign:nonce:{nonce} */
    @Test
    public void splicingNonceSaveKey_format() {
        Assertions.assertEquals("satoken:sign:nonce:abc", template().splicingNonceSaveKey("abc"));
    }

    /** getSignConfigOrGlobal 实例有配置用实例配置，无配置用全局配置 */
    @Test
    public void getSignConfigOrGlobal_instanceAndGlobal() {
        SaSignConfig instanceConfig = new SaSignConfig().setSecretKey(KEY);
        SaSignTemplate t = new SaSignTemplate(instanceConfig);
        Assertions.assertSame(instanceConfig, t.getSignConfig());
        Assertions.assertSame(instanceConfig, t.getSignConfigOrGlobal());
        Assertions.assertEquals(KEY, t.getSecretKey());

        SaSignTemplate noConfig = new SaSignTemplate();
        Assertions.assertNull(noConfig.getSignConfig());
        Assertions.assertSame(cn.dev33.satoken.sign.SaSignManager.getConfig(), noConfig.getSignConfigOrGlobal());
    }

    /** setSignConfig 可重新设置实例配置并返回 this */
    @Test
    public void setSignConfig_returnsThisAndUpdates() {
        SaSignTemplate t = new SaSignTemplate();
        SaSignConfig config = new SaSignConfig().setSecretKey(KEY);
        Assertions.assertSame(t, t.setSignConfig(config));
        Assertions.assertSame(config, t.getSignConfig());
    }
}
