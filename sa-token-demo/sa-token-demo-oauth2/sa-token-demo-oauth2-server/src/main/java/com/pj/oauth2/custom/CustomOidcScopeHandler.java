package com.pj.oauth2.custom;

import cn.dev33.satoken.oauth2.data.model.oidc.IdTokenModel;
import cn.dev33.satoken.oauth2.scope.handler.OidcScopeHandler;
import org.springframework.stereotype.Component;

/**
 * 扩展 OIDC 权限处理器，返回更多字段
 *
 * @author click33
 * @since 2024/8/24
 */
@Component
public class CustomOidcScopeHandler extends OidcScopeHandler {

    @Override
    public IdTokenModel workExtraData(IdTokenModel idToken) {
        Object userId = idToken.sub;
        System.out.println("----- 为 idToken 追加扩展字段 ----- ");

        idToken.extraData.put("uid", userId); // 用户id
        idToken.extraData.put("nickname", "lin_xiao_lin"); // 昵称
        idToken.extraData.put("picture", "https://sa-token.cc/logo.png"); // 头像
        idToken.extraData.put("email", "456456@xx.com"); // 邮箱
        idToken.extraData.put("phone_number", "13144556677"); // 手机号
        // 更多字段 ...
        // 可参考：https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims

        return idToken;
    }

}