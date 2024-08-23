package com.pj.oauth2.custom;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.granttype.handler.SaOAuth2GrantTypeHandlerInterface;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义 phone_code 授权模式处理器
 *
 * @author click33
 * @since 2024/8/23
 */
@Component
public class PhoneCodeGrantTypeHandler implements SaOAuth2GrantTypeHandlerInterface {

    @Override
    public String getHandlerGrantType() {
        return "phone_code";
    }

    @Override
    public AccessTokenModel getAccessToken(SaRequest req, String clientId, List<String> scopes) {

        // 获取前端提交的参数
        String phone = req.getParamNotNull("phone");
        String code = req.getParamNotNull("code");
        String realCode = SaManager.getSaTokenDao().get("phone_code:" + phone);

        // 1、校验验证码是否正确
        if(!code.equals(realCode)) {
            throw new SaOAuth2Exception("验证码错误");
        }

        // 2、校验通过，删除验证码
        SaManager.getSaTokenDao().delete("phone_code:" + phone);

        // 3、登录
        long userId = 10001; // 模拟 userId，真实项目应该根据手机号从数据库查询

        // 4、构建 ra 对象
        RequestAuthModel ra = new RequestAuthModel();
        ra.clientId = clientId;
        ra.loginId = userId;
        ra.scopes = scopes;

        // 5、生成 Access-Token
        AccessTokenModel at = SaOAuth2Manager.getDataGenerate().generateAccessToken(ra, true);
        return at;
    }
}