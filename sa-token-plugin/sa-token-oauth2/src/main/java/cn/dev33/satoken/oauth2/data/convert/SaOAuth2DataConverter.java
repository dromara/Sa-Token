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

import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;

import java.util.List;

/**
 * Sa-Token OAuth2 数据格式转换器
 *
 * @author click33
 * @since 1.39.0
 */
public interface SaOAuth2DataConverter {

    /**
     * 转换 scope 数据格式：String -> List
     * @param scopeString /
     * @return /
     */
    List<String> convertScopeStringToList(String scopeString);

    /**
     * 转换 scope 数据格式：List -> String
     * @param scopeList /
     * @return /
     */
    String convertScopeListToString(List<String> scopeList);

    /**
     * 转换 redirect_uri 数据格式：String -> List
     * @param redirectUris /
     * @return /
     */
    List<String> convertRedirectUriStringToList(String redirectUris);

    /**
     * 根据 RequestAuthModel 构建一个 CodeModel
     * @param ra RequestAuthModel
     * @return CodeModel 对象
     */
    CodeModel convertRequestAuthToCode(RequestAuthModel ra);

    /**
     * 根据 RequestAuthModel 构建一个 AccessTokenModel
     * @param ra RequestAuthModel
     * @param accessTokenTimeout Access-Token 有效期 (单位：秒)
     * @return AccessTokenModel 对象
     */
    AccessTokenModel convertRequestAuthToAccessToken(RequestAuthModel ra, long accessTokenTimeout);

    /**
     * 根据 Code 构建一个 Access-Token
     * @param cm CodeModel对象
     * @param accessTokenTimeout Access-Token 有效期 (单位：秒)
     * @return AccessToken对象
     */
    AccessTokenModel convertCodeToAccessToken(CodeModel cm, long accessTokenTimeout);

    /**
     * 根据 Access-Token 构建一个 Refresh-Token
     * @param at /
     * @param refreshTokenTimeout Refresh-Token 有效期 (单位：秒)
     * @return /
     */
    RefreshTokenModel convertAccessTokenToRefreshToken(AccessTokenModel at, long refreshTokenTimeout);

    /**
     * 根据 Refresh-Token 构建一个 Access-Token
     * @param rt /
     * @param accessTokenTimeout Access-Token 有效期 (单位：秒)
     * @return /
     */
    AccessTokenModel convertRefreshTokenToAccessToken(RefreshTokenModel rt, long accessTokenTimeout);

    /**
     * 根据 Refresh-Token 构建一个新的 Refresh-Token
     * @param rt /
     * @param refreshTokenTimeout Refresh-Token 有效期 (单位：秒)
     * @return /
     */
    RefreshTokenModel convertRefreshTokenToRefreshToken(RefreshTokenModel rt, long refreshTokenTimeout);

    /**
     * 根据 SaClientModel 构建一个 ClientTokenModel
     * @param clientModel /
     * @param scopes 权限列表
     * @return /
     */
    ClientTokenModel convertSaClientToClientToken(SaClientModel clientModel, List<String> scopes);

}
