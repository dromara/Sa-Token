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

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Sa-Token OAuth2 数据格式转换器，默认实现类
 *
 * @author click33
 * @since 1.39.0
 */
public class SaOAuth2DataConverterDefaultImpl implements SaOAuth2DataConverter {

    /**
     * 转换 scope 数据格式：String -> List
     */
    @Override
    public List<String> convertScopeStringToList(String scopeString) {
        if(SaFoxUtil.isEmpty(scopeString)) {
            return new ArrayList<>();
        }
        // 兼容以下三种分隔符：空格、逗号、%20
        scopeString = scopeString.replaceAll(" ", ",");
        scopeString = scopeString.replaceAll("%20", ",");
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
     * 转换 AllowUrl 数据格式：String -> List
     */
    @Override
    public List<String> convertAllowUrlStringToList(String allowUrl) {
        if(SaFoxUtil.isEmpty(allowUrl)) {
            return new ArrayList<>();
        }
        return SaFoxUtil.convertStringToList(allowUrl);
    }

    /**
     * 将 Code 转换为 Access-Token
     */
    @Override
    public AccessTokenModel convertCodeToAccessToken(CodeModel cm) {
        AccessTokenModel at = new AccessTokenModel();
        at.accessToken = SaOAuth2Strategy.instance.createAccessToken.execute(cm.clientId, cm.loginId, cm.scopes);
        at.clientId = cm.clientId;
        at.loginId = cm.loginId;
        at.scopes = cm.scopes;
        SaClientModel clientModel = SaOAuth2Manager.getDataLoader().getClientModelNotNull(cm.clientId);
        at.expiresTime = System.currentTimeMillis() + (clientModel.getAccessTokenTimeout() * 1000);
        at.extraData = new LinkedHashMap<>();
        return at;
    }

    /**
     * 将 Access-Token 转换为 Refresh-Token
     * @param at .
     * @return .
     */
    @Override
    public RefreshTokenModel convertAccessTokenToRefreshToken(AccessTokenModel at) {
        RefreshTokenModel rt = new RefreshTokenModel();
        rt.refreshToken = SaOAuth2Strategy.instance.createRefreshToken.execute(at.clientId, at.loginId, at.scopes);
        rt.clientId = at.clientId;
        rt.loginId = at.loginId;
        rt.scopes = at.scopes;
        SaClientModel clientModel = SaOAuth2Manager.getDataLoader().getClientModelNotNull(at.clientId);
        rt.expiresTime = System.currentTimeMillis() + (clientModel.getRefreshTokenTimeout() * 1000);
        rt.extraData = new LinkedHashMap<>(at.extraData);
        // 改变 at 属性
//        at.refreshToken = rt.refreshToken;
//        at.refreshExpiresTime = rt.expiresTime;
        return rt;
    }

    /**
     * 将 Refresh-Token 转换为 Access-Token
     * @param rt .
     * @return .
     */
    @Override
    public AccessTokenModel convertRefreshTokenToAccessToken(RefreshTokenModel rt) {
        AccessTokenModel at = new AccessTokenModel();
        at.accessToken = SaOAuth2Strategy.instance.createAccessToken.execute(rt.clientId, rt.loginId, rt.scopes);
        at.refreshToken = rt.refreshToken;
        at.clientId = rt.clientId;
        at.loginId = rt.loginId;
        at.scopes = rt.scopes;
        at.extraData = new LinkedHashMap<>(rt.extraData);
        SaClientModel clientModel = SaOAuth2Manager.getDataLoader().getClientModelNotNull(rt.clientId);
        at.expiresTime = System.currentTimeMillis() + (clientModel.getAccessTokenTimeout() * 1000);
        at.refreshExpiresTime = rt.expiresTime;
        return at;
    }

    /**
     * 根据 Refresh-Token 创建一个新的 Refresh-Token
     * @param rt .
     * @return .
     */
    @Override
    public RefreshTokenModel convertRefreshTokenToRefreshToken(RefreshTokenModel rt) {
        RefreshTokenModel newRt = new RefreshTokenModel();
        newRt.refreshToken = SaOAuth2Strategy.instance.createRefreshToken.execute(rt.clientId, rt.loginId, rt.scopes);
        SaClientModel clientModel = SaOAuth2Manager.getDataLoader().getClientModelNotNull(rt.clientId);
        newRt.expiresTime = System.currentTimeMillis() + (clientModel.getRefreshTokenTimeout() * 1000);
        newRt.clientId = rt.clientId;
        newRt.scopes = rt.scopes;
        newRt.loginId = rt.loginId;
        newRt.extraData = new LinkedHashMap<>(rt.extraData);
        return newRt;
    }

}

