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
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;

import java.util.List;

/**
 * 所有 OAuth2 GrantType 处理器的父接口
 *
 * @author click33
 * @since 1.39.0
 */
public interface SaOAuth2GrantTypeHandlerInterface {

    /**
     * 获取所要处理的 GrantType
     *
     * @return /
     */
    String getHandlerGrantType();

    /**
     * 获取 AccessTokenModel 对象
     *
     * @param req /
     * @return /
     */
    AccessTokenModel getAccessTokenModel(SaRequest req, String clientId, List<String> scopes);

}