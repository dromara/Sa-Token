package cn.dev33.satoken.stp.parameter.enums;

/**
 * SaRepeatLoginsMode: 重复登录模式
 * @author 石泽旭
 * @since 1.44.0
 */
public enum SaRepeatLoginsMode {

    /**
     * 将旧会话踢出
     */
    KICKOUT,

    /**
     * 拦截新的登录会话
     */
    INTERCEPT


}
