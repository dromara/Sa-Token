package cn.dev33.satoken.config;

/**
 * Sa-Token API 接口签名/验签 相关配置类
 *
 * @author click33
 * @since 2023/5/2
 */
public class SaSignConfig {

    /**
     * API 调用签名秘钥
     */
    private String secretKey;

    /**
     * 接口调用时的时间戳允许的差距（单位：ms），-1代表不校验差距，默认15分钟
     */
    private long timestampDisparity = 1000  * 60 * 15;

    /**
     * 是否校验 nonce 随机字符串
     */
    private Boolean isCheckNonce = true;


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
     * 获取 接口调用时的时间戳允许的差距（单位：ms），-1代表不校验差距，默认15分钟
     *
     * @return /
     */
    public long getTimestampDisparity() {
        return this.timestampDisparity;
    }

    /**
     * 设置 接口调用时的时间戳允许的差距（单位：ms），-1代表不校验差距，默认15分钟
     *
     * @param timestampDisparity /
     * @return 对象自身
     */
    public SaSignConfig setTimestampDisparity(long timestampDisparity) {
        this.timestampDisparity = timestampDisparity;
        return this;
    }

    /**
     * 获取 是否校验 nonce 随机字符串
     *
     * @return /
     */
    public Boolean getIsCheckNonce() {
        return this.isCheckNonce;
    }

    /**
     * 设置 是否校验 nonce 随机字符串
     *
     * @param isCheckNonce /
     * @return 对象自身
     */
    public SaSignConfig setIsCheckNonce(Boolean isCheckNonce) {
        this.isCheckNonce = isCheckNonce;
        return this;
    }

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

    @Override
    public String toString() {
        return "SaSignConfig ["
                + "secretKey=" + secretKey
                + ", timestampDisparity=" + timestampDisparity
                + ", isCheckNonce=" + isCheckNonce
                + "]";
    }

}