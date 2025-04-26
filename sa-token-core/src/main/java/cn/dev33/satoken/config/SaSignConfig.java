/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.config;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.fun.SaParamRetFunction;
import cn.dev33.satoken.secure.SaSecureUtil;

/**
 * Sa-Token API 接口签名/验签 相关配置类
 *
 * @author click33
 * @since 1.34.0
 */
public class SaSignConfig {

    /**
     * API 调用签名秘钥
     */
    private String secretKey;

    /**
     * 接口调用时的时间戳允许的差距（单位：ms），-1 代表不校验差距，默认15分钟
     *
     * <p> 比如此处你配置了60秒，当一个请求从 client 发起后，如果 server 端60秒内没有处理，60秒后再想处理就无法校验通过了。</p>
     * <p> timestamp + nonce 有效防止重放攻击。 </p>
     */
    private long timestampDisparity = 1000  * 60 * 15;

    /**
     * 对 fullStr 的摘要算法
     */
    private String digestAlgo = "md5";

    public SaSignConfig() {
    }

    /**
     * 构造函数
     * @param secretKey 秘钥
     */
    public SaSignConfig(String secretKey) {
        this.secretKey = secretKey;
    }


    // -------------- 扩展方法

    /**
     * 计算保存 nonce 时应该使用的 ttl，单位：秒
     * @return /
     */
    public long getSaveNonceExpire() {
        // 如果 timestampDisparity >= 0，则 nonceTtl 的值等于 timestampDisparity 的值，单位转秒
        if(timestampDisparity >= 0) {
            return timestampDisparity / 1000;
        }
        // 否则，nonceTtl 的值为 24 小时
        else {
            return 60 * 60 * 24;
        }
    }

    /**
     * 复制对象
     * @return /
     */
    public SaSignConfig copy()  {
        SaSignConfig obj = new SaSignConfig();
        obj.secretKey = this.secretKey;
        obj.timestampDisparity = this.timestampDisparity;
        obj.digestAlgo = this.digestAlgo;
        obj.digestMethod = this.digestMethod;
        return obj;
    }


    // -------------- 策略函数

    /**
     * 对 fullStr 的摘要算法函数
     */
    public SaParamRetFunction<String, String> digestMethod = (fullStr) -> {
        // md5
        if(digestAlgo.equalsIgnoreCase("md5")) {
            return SaSecureUtil.md5(fullStr);
        }
        // sha1
        if(digestAlgo.equalsIgnoreCase("sha1")) {
            return SaSecureUtil.sha1(fullStr);
        }
        // sha256
        if(digestAlgo.equalsIgnoreCase("sha256")) {
            return SaSecureUtil.sha256(fullStr);
        }
        // sha384
        if(digestAlgo.equalsIgnoreCase("sha384")) {
            return SaSecureUtil.sha384(fullStr);
        }
        // sha512
        if(digestAlgo.equalsIgnoreCase("sha512")) {
            return SaSecureUtil.sha512(fullStr);
        }
        // 未知
        throw new SaTokenException("不支持的摘要算法：" + digestAlgo + "，你可以自定义摘要算法函数实现");
    };

    /**
     * 设置: 对 fullStr 的摘要算法函数
     *
     * @param digestMethod /
     * @return 对象自身
     */
    public SaSignConfig setDigestMethod(SaParamRetFunction<String, String> digestMethod) {
        this.digestMethod = digestMethod;
        return this;
    }



    // -------------- get/set

    /**
     * 获取 API 调用签名秘钥
     *
     * @return /
     */
    public String getSecretKey() {
        return this.secretKey;
    }

    /**
     * 设置 API 调用签名秘钥
     *
     * @param secretKey /
     * @return 对象自身
     */
    public SaSignConfig setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    /**
     * 获取 接口调用时的时间戳允许的差距（单位：ms），-1 代表不校验差距，默认15分钟
     *
     * <p> 比如此处你配置了60秒，当一个请求从 client 发起后，如果 server 端60秒内没有处理，60秒后再想处理就无法校验通过了。</p>
     * <p> timestamp + nonce 有效防止重放攻击。 </p>
     *
     * @return /
     */
    public long getTimestampDisparity() {
        return this.timestampDisparity;
    }

    /**
     * 设置 接口调用时的时间戳允许的差距（单位：ms），-1 代表不校验差距，默认15分钟
     *
     * <p> 比如此处你配置了60秒，当一个请求从 client 发起后，如果 server 端60秒内没有处理，60秒后再想处理就无法校验通过了。</p>
     * <p> timestamp + nonce 有效防止重放攻击。 </p>
     *
     * @param timestampDisparity /
     * @return 对象自身
     */
    public SaSignConfig setTimestampDisparity(long timestampDisparity) {
        this.timestampDisparity = timestampDisparity;
        return this;
    }

    /**
     * 获取 对 fullStr 的摘要算法
     *
     * @return digestAlgo 对 fullStr 的摘要算法
     */
    public String getDigestAlgo() {
        return this.digestAlgo;
    }

    /**
     * 设置 对 fullStr 的摘要算法
     * @param digestAlgo /
     * @return /
     */
    public SaSignConfig setDigestAlgo(String digestAlgo) {
        this.digestAlgo = digestAlgo;
        return this;
    }

    @Override
    public String toString() {
        return "SaSignConfig ["
                + "secretKey=" + secretKey
                + ", timestampDisparity=" + timestampDisparity
                + "]";
    }

}