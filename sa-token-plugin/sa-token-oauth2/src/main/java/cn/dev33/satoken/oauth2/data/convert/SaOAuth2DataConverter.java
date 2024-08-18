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
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;

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
     * 转换 AllowUrl 数据格式：String -> List
     * @param allowUrl /
     * @return /
     */
    List<String> convertAllowUrlStringToList(String allowUrl);


    /**
     * 将 Code 转换为 Access-Token
     * @param cm CodeModel对象
     * @return AccessToken对象
     */
    AccessTokenModel convertCodeToAccessToken(CodeModel cm);

    /**
     * 将 Access-Token 转换为 Refresh-Token
     * @param at /
     * @return /
     */
    RefreshTokenModel convertAccessTokenToRefreshToken(AccessTokenModel at);

    /**
     * 将 Refresh-Token 转换为 Access-Token
     * @param rt /
     * @return /
     */
    AccessTokenModel convertRefreshTokenToAccessToken(RefreshTokenModel rt);

    /**
     * 根据 Refresh-Token 创建一个新的 Refresh-Token
     * @param rt /
     * @return /
     */
    RefreshTokenModel convertRefreshTokenToRefreshToken(RefreshTokenModel rt);

}
