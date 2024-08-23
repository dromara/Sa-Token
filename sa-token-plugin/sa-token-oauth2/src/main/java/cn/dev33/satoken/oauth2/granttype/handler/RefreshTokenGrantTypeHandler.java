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
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;

import java.util.List;

/**
 * refresh_token grant_type 处理器
 *
 * @author click33
 * @since 1.39.0
 */
public class RefreshTokenGrantTypeHandler implements SaOAuth2GrantTypeHandlerInterface {

    @Override
    public String getHandlerGrantType() {
        return GrantType.refresh_token;
    }

    @Override
    public AccessTokenModel getAccessToken(SaRequest req, String clientId, List<String> scopes) {
        // 获取参数
        String refreshToken = req.getParamNotNull(SaOAuth2Consts.Param.refresh_token);

        // 校验：Refresh-Token 是否存在
        RefreshTokenModel rt = SaOAuth2Manager.getDao().getRefreshToken(refreshToken);
        SaOAuth2Exception.throwBy(rt == null, "无效refresh_token: " + refreshToken, SaOAuth2ErrorCode.CODE_30121);

        // 校验：Refresh-Token 代表的 ClientId 与提供的 ClientId 是否一致
        SaOAuth2Exception.throwBy( ! rt.clientId.equals(clientId), "无效client_id: " + clientId, SaOAuth2ErrorCode.CODE_30122);

        // 获取新 Access-Token
        AccessTokenModel accessTokenModel = SaOAuth2Manager.getDataGenerate().refreshAccessToken(refreshToken);
        return accessTokenModel;
    }

}