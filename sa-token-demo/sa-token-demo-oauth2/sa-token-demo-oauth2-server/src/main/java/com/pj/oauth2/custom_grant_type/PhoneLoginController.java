//package com.pj.oauth2.custom_grant_type;
//
//import cn.dev33.satoken.SaManager;
//import cn.dev33.satoken.util.SaFoxUtil;
//import cn.dev33.satoken.util.SaResult;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 自定义手机登录接口
// *
// * @author click33
// * @since 2024/8/23
// */
//@RestController
//public class PhoneLoginController {
//
//    @RequestMapping("/oauth2/sendPhoneCode")
//    public SaResult sendCode(String phone) {
//        String code = SaFoxUtil.getRandomNumber(100000, 999999) + "";
//        SaManager.getSaTokenDao().set("phone_code:" + phone, code, 60 * 5);
//        System.out.println("手机号：" + phone + "，验证码：" + code + "，已发送成功");
//        return SaResult.ok("验证码发送成功");
//    }
//
//}