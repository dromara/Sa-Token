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
package cn.dev33.satoken.oauth2.data.resolver;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.request.ClientIdAndSecretModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.util.SaResult;

import java.util.Map;

/**
 * Sa-Token OAuth2 数据解析器，负责 Web 交互层面的数据进出：
 *  <p>1、从请求中按照指定格式读取数据</p>
 *  <p>2、构建数据输出格式</p>
 *
 * @author click33
 * @since 1.39.0
 */
public interface SaOAuth2DataResolver {

    /**
     * 数据读取：从请求对象中读取 ClientId、Secret
     *
     * @param request /
     * @return /
     */
    ClientIdAndSecretModel readClientIdAndSecret(SaRequest request);

    /**
     * 数据读取：从请求对象中读取 AccessToken
     *
     * @param request /
     * @return /
     */
    String readAccessToken(SaRequest request);

    /**
     * 数据读取：从请求对象中读取 ClientToken
     *
     * @param request /
     * @return /
     */
    String readClientToken(SaRequest request);

    /**
     * 数据读取：从请求对象中构建 RequestAuthModel
     * @param req SaRequest对象
     * @param loginId 账号id
     * @return RequestAuthModel对象
     */
    RequestAuthModel readRequestAuthModel(SaRequest req, Object loginId);

    /**
     * 构建返回值: 获取 token
     * @param at token信息
     * @return /
     */
    Map<String, Object> buildAccessTokenReturnValue(AccessTokenModel at);

    /**
     * 构建返回值: RefreshToken 刷新 Access-Token
     * @param at token信息
     * @return /
     */
    default Map<String, Object> buildRefreshTokenReturnValue(AccessTokenModel at) {
        return buildAccessTokenReturnValue(at);
    }

    /**
     * 构建返回值: 回收 Access-Token
     * @return /
     */
    default Map<String, Object> buildRevokeTokenReturnValue() {
        return SaResult.ok();
    }

    /**
     * 构建返回值: 凭证式 模式认证 获取 token
     * @param ct token信息
     */
    Map<String, Object> buildClientTokenReturnValue(ClientTokenModel ct);

}
