//package com.pj.oauth2.custom;
//
//import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
//import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
//import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
//import org.springframework.stereotype.Component;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
///**
// * @author click33
// * @since 2024/8/20
// */
//@Component
//public class UserinfoScopeHandler implements SaOAuth2ScopeHandlerInterface {
//
//    @Override
//    public String getHandlerScope() {
//        return "userinfo";
//    }
//
//    @Override
//    public void workAccessToken(AccessTokenModel at) {
//        System.out.println("--------- userinfo 权限，加工 AccessTokenModel --------- ");
//        // 模拟账号信息 （真实环境需要查询数据库获取信息）
//        Map<String, Object> map = new LinkedHashMap<String, Object>();
//        map.put("userId", "10008");
//        map.put("nickname", "shengzhang_");
//        map.put("avatar", "http://xxx.com/1.jpg");
//        map.put("age", "18");
//        map.put("sex", "男");
//        map.put("address", "山东省 青岛市 城阳区");
//        at.extraData.put("userinfo", map);
//    }
//
//    @Override
//    public void workClientToken(ClientTokenModel ct) {
//    }
//
//}