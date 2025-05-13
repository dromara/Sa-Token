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
package cn.dev33.satoken.oauth2.data.convert;

import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTtlMethods;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Sa-Token OAuth2 数据格式转换器，默认实现类
 *
 * @author click33
 * @since 1.39.0
 */
public class SaOAuth2DataConverterDefaultImpl implements SaOAuth2DataConverter, SaTtlMethods {

    /**
     * 转换 scope 数据格式：String -> List
     */
    @Override
    public List<String> convertScopeStringToList(String scopeString) {
        if(SaFoxUtil.isEmpty(scopeString)) {
            return new ArrayList<>();
        }
        // 兼容以下三种分隔符：空格、逗号、%20、加号
        scopeString = scopeString.replace(" ", ",");
        scopeString = scopeString.replace("%20", ",");
        scopeString = scopeString.replace("+", ",");
        return SaFoxUtil.convertStringToList(scopeString);
    }

    /**
     * 转换 scope 数据格式：List -> String
     */
    @Override
    public String convertScopeListToString(List<String> scopeList) {
        return SaFoxUtil.convertListToString(scopeList);
    }

    /**
     * 转换 redirect_uri 数据格式：String -> List
     */
    @Override
    public List<String> convertRedirectUriStringToList(String redirectUris) {
        if(SaFoxUtil.isEmpty(redirectUris)) {
            return new ArrayList<>();
        }
        return SaFoxUtil.convertStringToList(redirectUris);
    }

    /**
     * 根据 RequestAuthModel 构建一个 CodeModel
     * @param ra RequestAuthModel
     * @return CodeModel 对象
     */
    @Override
    public CodeModel convertRequestAuthToCode(RequestAuthModel ra){
        String codeValue = SaOAuth2Strategy.instance.createCodeValue.execute(ra.clientId, ra.loginId, ra.scopes);
        CodeModel cm = new CodeModel();
        cm.code = codeValue;
        cm.clientId = ra.clientId;
        cm.scopes = ra.scopes;
        cm.loginId = ra.loginId;
        cm.redirectUri = ra.redirectUri;
        cm.nonce = ra.getNonce();
        return cm;
    }

    /**
     * 根据 RequestAuthModel 构建一个 AccessTokenModel
     * @param ra RequestAuthModel
     * @return AccessTokenModel 对象
     */
    @Override
    public AccessTokenModel convertRequestAuthToAccessToken(RequestAuthModel ra, long accessTokenTimeout) {
        String newAtValue = SaOAuth2Strategy.instance.createAccessToken.execute(ra.clientId, ra.loginId, ra.scopes);
        AccessTokenModel at = new AccessTokenModel();
        at.accessToken = newAtValue;
        at.clientId = ra.clientId;
        at.loginId = ra.loginId;
        at.scopes = ra.scopes;
        at.tokenType = SaOAuth2Consts.TokenType.bearer;
        at.expiresTime = ttlToExpireTime(accessTokenTimeout);
        at.extraData = new LinkedHashMap<>();
        return at;
    }

    /**
     * 根据 Code 构建一个 Access-Token
     */
    @Override
    public AccessTokenModel convertCodeToAccessToken(CodeModel cm, long accessTokenTimeout) {
        AccessTokenModel at = new AccessTokenModel();
        at.accessToken = SaOAuth2Strategy.instance.createAccessToken.execute(cm.clientId, cm.loginId, cm.scopes);
        at.clientId = cm.clientId;
        at.loginId = cm.loginId;
        at.scopes = cm.scopes;
        at.tokenType = SaOAuth2Consts.TokenType.bearer;
        at.grantType = GrantType.authorization_code;
        at.expiresTime = ttlToExpireTime(accessTokenTimeout);
        at.extraData = new LinkedHashMap<>();
        return at;
    }

    /**
     * 根据 Access-Token 构建一个 Refresh-Token
     */
    @Override
    public RefreshTokenModel convertAccessTokenToRefreshToken(AccessTokenModel at, long refreshTokenTimeout) {
        RefreshTokenModel rt = new RefreshTokenModel();
        rt.refreshToken = SaOAuth2Strategy.instance.createRefreshToken.execute(at.clientId, at.loginId, at.scopes);
        rt.clientId = at.clientId;
        rt.loginId = at.loginId;
        rt.scopes = at.scopes;
        rt.expiresTime = ttlToExpireTime(refreshTokenTimeout);
        rt.extraData = new LinkedHashMap<>(at.extraData);
        return rt;
    }

    /**
     * 根据 Refresh-Token 构建一个 Access-Token
     */
    @Override
    public AccessTokenModel convertRefreshTokenToAccessToken(RefreshTokenModel rt, long accessTokenTimeout) {
        AccessTokenModel at = new AccessTokenModel();
        at.accessToken = SaOAuth2Strategy.instance.createAccessToken.execute(rt.clientId, rt.loginId, rt.scopes);
        at.refreshToken = rt.refreshToken;
        at.clientId = rt.clientId;
        at.loginId = rt.loginId;
        at.scopes = rt.scopes;
        at.tokenType = SaOAuth2Consts.TokenType.bearer;
        at.grantType = GrantType.refresh_token;
        at.extraData = new LinkedHashMap<>(rt.extraData);
        at.expiresTime = ttlToExpireTime(accessTokenTimeout);
        at.refreshExpiresTime = rt.expiresTime;
        return at;
    }

    /**
     * 根据 Refresh-Token 构建一个新的 Refresh-Token
     */
    @Override
    public RefreshTokenModel convertRefreshTokenToRefreshToken(RefreshTokenModel rt, long refreshTokenTimeout) {
        RefreshTokenModel newRt = new RefreshTokenModel();
        newRt.refreshToken = SaOAuth2Strategy.instance.createRefreshToken.execute(rt.clientId, rt.loginId, rt.scopes);
        newRt.expiresTime = ttlToExpireTime(refreshTokenTimeout);
        newRt.clientId = rt.clientId;
        newRt.scopes = rt.scopes;
        newRt.loginId = rt.loginId;
        newRt.extraData = new LinkedHashMap<>(rt.extraData);
        return newRt;
    }

    /**
     * 根据 SaClientModel 构建一个 ClientTokenModel
     * @param clientModel /
     * @param scopes 权限列表
     * @return /
     */
    @Override
    public ClientTokenModel convertSaClientToClientToken(SaClientModel clientModel, List<String> scopes) {
        String clientTokenValue = SaOAuth2Strategy.instance.createClientToken.execute(clientModel.getClientId(), scopes);
        ClientTokenModel ct = new ClientTokenModel(clientTokenValue, clientModel.getClientId(), scopes);
        ct.clientToken = clientTokenValue;
        ct.clientId = clientModel.getClientId();
        ct.scopes = scopes;
        ct.tokenType = SaOAuth2Consts.TokenType.bearer;
        ct.expiresTime = ttlToExpireTime(clientModel.getClientTokenTimeout());
        ct.grantType = GrantType.client_credentials;
        ct.extraData = new LinkedHashMap<>();
        return ct;
    }

}

