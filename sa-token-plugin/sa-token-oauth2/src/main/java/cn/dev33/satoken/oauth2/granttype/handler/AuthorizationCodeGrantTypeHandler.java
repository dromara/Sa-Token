/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.oauth2.granttype.handler;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.request.ClientIdAndSecretModel;

import java.util.List;

/**
 * authorization_code grant_type 处理器
 *
 * @author click33
 * @since 1.39.0
 */
public class AuthorizationCodeGrantTypeHandler implements SaOAuth2GrantTypeHandlerInterface {

    @Override
    public String getHandlerGrantType() {
        return GrantType.authorization_code;
    }

    @Override
    public AccessTokenModel getAccessTokenModel(SaRequest req, String clientId, List<String> scopes) {
        // 获取参数
        ClientIdAndSecretModel clientIdAndSecret = SaOAuth2Manager.getDataResolver().readClientIdAndSecret(req);
//        String clientId = clientIdAndSecret.clientId;
        String clientSecret = clientIdAndSecret.clientSecret;
        String code = req.getParamNotNull(SaOAuth2Consts.Param.code);
        String redirectUri = req.getParam(SaOAuth2Consts.Param.redirect_uri);

        // 校验参数
        SaOAuth2Manager.getTemplate().checkGainTokenParam(code, clientId, clientSecret, redirectUri);

        // 构建 Access-Token、返回
        AccessTokenModel accessTokenModel = SaOAuth2Manager.getDataGenerate().generateAccessToken(code);
        return accessTokenModel;
    }

}