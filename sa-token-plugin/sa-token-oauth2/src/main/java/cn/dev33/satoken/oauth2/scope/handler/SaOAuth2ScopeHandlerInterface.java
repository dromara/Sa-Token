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
package cn.dev33.satoken.oauth2.scope.handler;

import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;

/**
 * 所有 OAuth2 权限处理器的父接口
 *
 * @author click33
 * @since 1.39.0
 */
public interface SaOAuth2ScopeHandlerInterface {

    /**
     * 获取所要处理的权限
     *
     * @return /
     */
    String getHandlerScope();

    /**
     * 当构建的 AccessToken 具有此权限时，所需要执行的方法
     *
     * @param at /
     */
    void workAccessToken(AccessTokenModel at);

    /**
     * 当构建的 ClientToken 具有此权限时，所需要执行的方法
     *
     * @param ct /
     */
    void workClientToken(ClientTokenModel ct);

    /**
     * 当使用 RefreshToken 刷新 AccessToken 时，是否重新执行 workAccessToken 构建方法
     *
     * @return /
     */
    default boolean refreshAccessTokenIsWork() {
        return false;
    }

}