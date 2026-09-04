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

    /** 造一个配好秘钥的签名模板实例，别污染全局 SaSignManager */
    private static SaSignTemplate template() {
        return new SaSignTemplate(new SaSignConfig().setSecretKey(KEY));
    }

    /** 示例参数，按插入顺序是 name、age、sex */
    private static Map<String, Object> sampleParams() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "zhang");
        map.put("age", 18);
        map.put("sex", "女");
        return map;
    }

    /** joinParams 不排序时应该按插入顺序拼接，空值要跳过，末尾的 & 也得删掉 */
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

    /** joinParamsDictSort 应该按字典序拼接参数，传 TreeMap 进来时不用重新包装 */
    @Test
    public void joinParamsDictSort_sortByDictOrder() {
        Assertions.assertEquals("age=18&name=zhang&sex=女",
                template().joinParamsDictSort(sampleParams()));
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("b", "2");
        treeMap.put("a", "1");
        Assertions.assertEquals("a=1&b=2", template().joinParamsDictSort(treeMap));
    }

    /** 相同参数多次 createSign 应该得到一样的 md5 签名，参数里带 sign 字段时应该把它排除掉 */
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

    /** createSign 秘钥为空时必须抛异常，code 得是 CODE_12201 */
    @Test
    public void createSign_secretKeyEmpty_throws() {
        SaSignTemplate t = new SaSignTemplate(new SaSignConfig());
        cn.dev33.satoken.exception.SaTokenException ex = Assertions.assertThrows(
                cn.dev33.satoken.exception.SaTokenException.class,
                () -> t.createSign(sampleParams()));
        Assertions.assertEquals(SaSignErrorCode.CODE_12201, ex.getCode());
    }

    /** digestFullStr 应该走配置里的摘要算法函数 */
    @Test
    public void digestFullStr_usesConfigDigestMethod() {
        SaSignTemplate t = new SaSignTemplate(new SaSignConfig().setSecretKey(KEY).setDigestAlgo("sha1"));
        Assertions.assertEquals(t.getSignConfigOrGlobal().digestMethod.run("abc"), t.digestFullStr("abc"));
    }

    /** addSignParams 应该追加 timestamp、nonce、sign 三个字段，addSignParamsAndJoin 应该把它们拼成字符串 */
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

        // sign 是拿带 timestamp、nonce 的参数算出来的，移掉 sign 后重新算应该一样
        Map<String, Object> withoutSign = new LinkedHashMap<>(result);
        withoutSign.remove(SaSignTemplate.sign);
        Assertions.assertEquals(t.createSign(withoutSign), result.get(SaSignTemplate.sign));

        String joined = t.addSignParamsAndJoin(new LinkedHashMap<>(params));
        Assertions.assertTrue(joined.contains("data=hello"));
        Assertions.assertTrue(joined.contains("timestamp="));
        Assertions.assertTrue(joined.contains("nonce="));
        Assertions.assertTrue(joined.contains("sign="));
    }

    /** isValidTimestamp 当前时间在范围内应该返回 true，超出了应该返回 false，配置成 -1 时应该一直返回 true */
    @Test
    public void isValidTimestamp_rangeAndDisable() {
        SaSignTemplate t = template();
        Assertions.assertTrue(t.isValidTimestamp(System.currentTimeMillis()));
        Assertions.assertFalse(t.isValidTimestamp(System.currentTimeMillis() - 1000 * 60 * 20));
        SaSignTemplate disable = new SaSignTemplate(new SaSignConfig().setSecretKey(KEY).setTimestampDisparity(-1));
        Assertions.assertTrue(disable.isValidTimestamp(0L));
        Assertions.assertTrue(disable.isValidTimestamp(System.currentTimeMillis() - 1000000L));
    }

    /** checkTimestamp 时间戳超出范围时必须抛 CODE_12203，在范围内时应该不抛异常 */
    @Test
    public void checkTimestamp_outOfRangeThrows() {
        long past = System.currentTimeMillis() - 1000 * 60 * 20;
        SaSignException ex = Assertions.assertThrows(SaSignException.class,
                () -> template().checkTimestamp(past));
        Assertions.assertEquals(SaSignErrorCode.CODE_12203, ex.getCode());
        Assertions.assertDoesNotThrow(() -> template().checkTimestamp(System.currentTimeMillis()));
    }

    /** isValidNonce 空值时应该返回 false，没用过的应该返回 true，同一个 nonce 多次判断应该都有效（不缓存） */
    @Test
    public void isValidNonce_emptyAndFreshAndRepeatable() {
        SaSignTemplate t = template();
        Assertions.assertFalse(t.isValidNonce(""));
        Assertions.assertFalse(t.isValidNonce(null));
        Assertions.assertTrue(t.isValidNonce("fresh-nonce-1"));
        Assertions.assertTrue(t.isValidNonce("fresh-nonce-1"));
    }

    /** checkNonce 空值时必须抛异常，校验通过后再用同一个 nonce 必须抛异常 */
    @Test
    public void checkNonce_emptyAndSecondTimeThrows() {
        SaSignTemplate t = template();
        Assertions.assertThrows(SaSignException.class, () -> t.checkNonce(""));
        t.checkNonce("once-nonce-1");
        Assertions.assertThrows(SaSignException.class, () -> t.checkNonce("once-nonce-1"));
    }

    /** isValidSign 签名正确时应该返回 true，错了应该返回 false；checkSign 签名无效时必须抛 CODE_12202，有效时应该不抛 */
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

    /** 造一组带 timestamp、nonce、sign 的合法请求参数 */
    private Map<String, String> buildValidParamMap(SaSignTemplate t) {
        Map<String, Object> signed = t.addSignParams(sampleParams());
        Map<String, String> result = new LinkedHashMap<>();
        signed.forEach((k, v) -> result.put(k, String.valueOf(v)));
        return result;
    }

    /** isValidParamMap 缺 timestamp 或 sign 时应该返回 false，全都合法时应该返回 true，timestamp 超出或签名错了时应该返回 false */
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

    /** checkParamMap 缺 timestamp、nonce、sign 时应该分别抛对应的异常，全都合法时应该不抛 */
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

    /** isValidRequest 不指定 paramNames 时应该校验全部参数，指定 paramNames 时应该只拿指定参数参与签名 */
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

        // 指定 paramNames 时，extra 参数不应该参与签名校验
        Map<String, String> withExtra = new LinkedHashMap<>(paramMap);
        withExtra.put("extra", "not-signed");
        SaRequestForMock request2 = new SaRequestForMock();
        request2.parameterMap = withExtra;
        Assertions.assertTrue(t.isValidRequest(request2, "data"));
        Assertions.assertFalse(t.isValidRequest(request2));
    }

    /** checkRequest 全都合法时应该不抛异常，签名错了时必须抛异常 */
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

    /** splicingNonceSaveKey 应该拼成 {tokenName}:sign:nonce:{nonce} 这样的格式 */
    @Test
    public void splicingNonceSaveKey_format() {
        Assertions.assertEquals("satoken:sign:nonce:abc", template().splicingNonceSaveKey("abc"));
    }

    /** getSignConfigOrGlobal 实例自己有配置时应该用实例的，没有时应该用全局的 */
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

    /** setSignConfig 应该能重新设置实例配置，并且返回 this */
    @Test
    public void setSignConfig_returnsThisAndUpdates() {
        SaSignTemplate t = new SaSignTemplate();
        SaSignConfig config = new SaSignConfig().setSecretKey(KEY);
        Assertions.assertSame(t, t.setSignConfig(config));
        Assertions.assertSame(config, t.getSignConfig());
    }
}
