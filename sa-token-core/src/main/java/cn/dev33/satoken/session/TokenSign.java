package cn.dev33.satoken.session;

import java.io.Serializable;

/**
 * Token 签名 Model
 * <p>
 * 挂在到SaSession上的token签名
 *
 * @author kong
 */
public class TokenSign implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1406115065849845073L;

    /**
     * token值
     */
    private String value;

    /**
     * 所属设备类型
     */
    private String device;

    /**
     * 构建一个
     */
    public TokenSign() {
    }

    /**
     * 构建一个
     *
     * @param value  token值
     * @param device 所属设备类型
     */
    public TokenSign(String value, String device) {
        this.value = value;
        this.device = device;
    }

    /**
     * @return token值
     */
    public String getValue() {
        return value;
    }

    /**
     * @return 所属设备类型
     */
    public String getDevice() {
        return device;
    }

    /**
     * 反序列化需要
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 反序列化需要
     */
    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "TokenSign [value=" + value + ", device=" + device + "]";
    }

}
