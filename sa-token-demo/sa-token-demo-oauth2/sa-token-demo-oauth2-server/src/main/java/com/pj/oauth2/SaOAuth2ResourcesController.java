package com.pj.oauth2;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sa-Token OAuth2 Resources 端 Controller
 *
 * <p> Resources 端：OAuth2 资源端，允许 Client 端根据 Access-Token 置换相关资源 </p>
 *
 * <p> 在 OAuth2 中，认证端和资源端：
 *  1、可以在一个 Controller 中，也可以在不同的 Controller 中
 *  2、可以在同一个项目中，也可以在不同的项目中（在不同项目中时需要两端连同一个 Redis ）
 * </p>
 *
 * @author click33
 * @since 2024/12/6
 */
@RestController
public class SaOAuth2ResourcesController {

    // 示例：获取 userinfo 信息：昵称、头像、性别等等
    @RequestMapping("/oauth2/userinfo")
    public SaResult userinfo() {
        // 获取 Access-Token 对应的账号id
        String accessToken = SaOAuth2Manager.getDataResolver().readAccessToken(SaHolder.getRequest());
        Object loginId = SaOAuth2Util.getLoginIdByAccessToken(accessToken);
        System.out.println("-------- 此Access-Token对应的账号id: " + loginId);

        // 校验 Access-Token 是否具有权限: userinfo
        SaOAuth2Util.checkAccessTokenScope(accessToken, "userinfo");

        // 模拟账号信息 （真实环境需要查询数据库获取信息）
        Map<String, Object> map = new LinkedHashMap<>();
        // map.put("userId", loginId);  一般原则下，oauth2-server 不能把 userId 返回给 oauth2-client
        map.put("nickname", "林小林");
        map.put("avatar", "http://xxx.com/1.jpg");
        map.put("age", "18");
        map.put("sex", "男");
        map.put("address", "山东省 青岛市 城阳区");
        return SaResult.ok().setMap(map);
    }

}