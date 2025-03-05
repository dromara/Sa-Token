package com.pj.util;//package com.pj.oauth2.custom;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 手机验证码工具类 （仅做逻辑模拟，不做真实发送）
 *
 * @author click33
 * @since 2024/8/23
 */
public class PhoneCodeUtil {

    // 指定手机号发送验证码
    public static void sendCode(String phone) {
        String code = SaFoxUtil.getRandomNumber(100000, 999999) + "";
        SaManager.getSaTokenDao().set("phone_code:" + phone, code, 60 * 5);
        System.out.println("手机号：" + phone + "，验证码：" + code + "，已发送成功");
    }

    // 校验验证码是否正确，不正确则抛出异常
    public static void checkCode(String phone, String code) {
        String oldCode = SaManager.getSaTokenDao().get("phone_code:" + phone);
        if( ! code.equals(oldCode) ) {
            throw new RuntimeException("验证码错误");
        }
        // 验证通过后，立即删除验证码
        SaManager.getSaTokenDao().delete("phone_code:" + phone);
    }

}