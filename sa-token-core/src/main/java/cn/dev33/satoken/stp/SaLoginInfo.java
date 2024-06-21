package cn.dev33.satoken.stp;

/**
 * 登录信息 model
 *
 *<P>
 *  {
 *   "tokenName": "satoekn", //request Header Name
 *   "tokenValue": "eef25f7b6c6048d093fc4427aa62f36c", //token
 *   "loginId": 10002, //login id
 *   "tokenTimeout": -1, //token 剩余有效期（单位: 秒）
 *   "sessionTimeout": -1, //Session 剩余有效时间（单位: 秒）
 *   "tokenSessionTimeout": -1, //Token-Session 剩余有效时间（单位: 秒）
 *   "tokenActiveTimeout": -1, //token 距离被冻结还剩多少时间（单位: 秒）
 *   "loginDevice": "Android-Readmi K50" //登录设备
 * }
 *</P>
 *
 * @author suzhelan
 * @version 1.0
 */
public class SaLoginInfo {
    /**
     * token name
     */
    private String tokenName;

    /**
     * token
     */
    private String tokenValue;

    /**
     * 此 token 对应的 LoginId
     */
    private Object loginId;

    /**
     * token 剩余有效期（单位: 秒）
     */
    private long tokenTimeout;

    /**
     * Account-Session 剩余有效时间（单位: 秒）
     */
    private long sessionTimeout;

    /**
     * Token-Session 剩余有效时间（单位: 秒）
     */
    private long tokenSessionTimeout;

    /**
     * token 距离被冻结还剩多少时间（单位: 秒）
     */
    private long tokenActiveTimeout;

    /**
     * 登录设备类型
     */
    private String loginDevice;

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Object getLoginId() {
        return loginId;
    }

    public void setLoginId(Object loginId) {
        this.loginId = loginId;
    }

    public long getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(long tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public long getTokenSessionTimeout() {
        return tokenSessionTimeout;
    }

    public void setTokenSessionTimeout(long tokenSessionTimeout) {
        this.tokenSessionTimeout = tokenSessionTimeout;
    }

    public long getTokenActiveTimeout() {
        return tokenActiveTimeout;
    }

    public void setTokenActiveTimeout(long tokenActiveTimeout) {
        this.tokenActiveTimeout = tokenActiveTimeout;
    }

    public String getLoginDevice() {
        return loginDevice;
    }

    public void setLoginDevice(String loginDevice) {
        this.loginDevice = loginDevice;
    }
}
