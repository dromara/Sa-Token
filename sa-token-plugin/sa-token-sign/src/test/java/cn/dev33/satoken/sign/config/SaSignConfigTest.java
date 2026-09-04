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

import cn.dev33.satoken.exception.SaTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaSignConfig} getter/setter/copy/getSaveNonceExpire/digestMethod 行为测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSignConfigTest {

    /** getter/setter 读写一致，默认 digestAlgo 为 md5，timestampDisparity 默认 15 分钟 */
    @Test
    public void getterSetter_defaults() {
        SaSignConfig config = new SaSignConfig();
        Assertions.assertEquals("md5", config.getDigestAlgo());
        Assertions.assertEquals(1000 * 60 * 15, config.getTimestampDisparity());
        Assertions.assertNull(config.getSecretKey());

        config.setSecretKey("k1").setTimestampDisparity(1000).setDigestAlgo("sha256");
        Assertions.assertEquals("k1", config.getSecretKey());
        Assertions.assertEquals(1000, config.getTimestampDisparity());
        Assertions.assertEquals("sha256", config.getDigestAlgo());
    }

    /** 带秘钥的构造函数 */
    @Test
    public void constructor_withSecretKey() {
        Assertions.assertEquals("abc", new SaSignConfig("abc").getSecretKey());
    }

    /** getSaveNonceExpire：timestampDisparity>=0 时取毫秒转秒，<0 时取 24 小时 */
    @Test
    public void getSaveNonceExpire_branches() {
        SaSignConfig config = new SaSignConfig();
        config.setTimestampDisparity(60_000);
        Assertions.assertEquals(60, config.getSaveNonceExpire());

        config.setTimestampDisparity(-1);
        Assertions.assertEquals(60 * 60 * 24, config.getSaveNonceExpire());
    }

    /** copy 复制所有字段，与原对象独立 */
    @Test
    public void copy_independent() {
        SaSignConfig config = new SaSignConfig().setSecretKey("k").setTimestampDisparity(2000).setDigestAlgo("sha1");
        SaSignConfig copied = config.copy();
        Assertions.assertEquals("k", copied.getSecretKey());
        Assertions.assertEquals(2000, copied.getTimestampDisparity());
        Assertions.assertEquals("sha1", copied.getDigestAlgo());
        Assertions.assertSame(config.digestMethod, copied.digestMethod);

        copied.setSecretKey("k2");
        Assertions.assertEquals("k", config.getSecretKey());
    }

    /** digestMethod 默认支持 md5/sha1/sha256/sha384/sha512，未知算法抛 SaTokenException */
    @Test
    public void digestMethod_supportedAlgos() {
        SaSignConfig config = new SaSignConfig();
        String sample = "hello";
        Assertions.assertEquals(md5Expect(sample), config.digestMethod.run(sample));

        for (String algo : new String[]{"md5", "sha1", "sha256", "sha384", "sha512"}) {
            config.setDigestAlgo(algo);
            Assertions.assertNotNull(config.digestMethod.run(sample));
        }

        config.setDigestAlgo("unknown-algo");
        Assertions.assertThrows(SaTokenException.class, () -> config.digestMethod.run(sample));
    }

    /** setDigestMethod 可替换摘要函数 */
    @Test
    public void setDigestMethod_custom() {
        SaSignConfig config = new SaSignConfig();
        config.setDigestMethod(s -> "custom-" + s);
        Assertions.assertEquals("custom-abc", config.digestMethod.run("abc"));
    }

    /** toString 包含 secretKey 与 timestampDisparity */
    @Test
    public void toString_containsKeyFields() {
        String str = new SaSignConfig().setSecretKey("kk").toString();
        Assertions.assertTrue(str.contains("secretKey=kk"));
        Assertions.assertTrue(str.contains("timestampDisparity="));
    }

    private static String md5Expect(String s) {
        return cn.dev33.satoken.secure.SaSecureUtil.md5(s);
    }
}
