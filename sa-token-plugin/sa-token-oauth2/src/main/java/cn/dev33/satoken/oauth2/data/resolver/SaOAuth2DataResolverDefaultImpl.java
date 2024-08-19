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
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts.TokenType;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.data.model.request.ClientIdAndSecretModel;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sa-Token OAuth2 数据解析器，负责 Web 交互层面的数据进出：
 *  <p>1、从请求中按照指定格式读取数据</p>
 *  <p>2、构建数据输出格式</p>
 *
 * @author click33
 * @since 1.39.0
 */
public class SaOAuth2DataResolverDefaultImpl implements SaOAuth2DataResolver {

    /**
     * 数据读取：从请求对象中读取 ClientId、Secret，如果获取不到则抛出异常
     *
     * @param request /
     * @return /
     */
    @Override
    public ClientIdAndSecretModel readClientIdAndSecret(SaRequest request) {
        // 优先从请求参数中获取
        String clientId = request.getParam(SaOAuth2Consts.Param.client_id);
        String clientSecret = request.getParam(SaOAuth2Consts.Param.client_secret);
        if(SaFoxUtil.isNotEmpty(clientId)) {
            return new ClientIdAndSecretModel(clientId, clientSecret);
        }

        // 如果请求参数中没有提供 client_id 参数，则尝试从 base auth 中获取
        String authorizationValue = SaHttpBasicUtil.getAuthorizationValue();
        if(SaFoxUtil.isNotEmpty(authorizationValue)) {
            String[] arr = authorizationValue.split(":");
            clientId = arr[0];
            if(arr.length > 1) {
                clientSecret = arr[1];
            }
            return new ClientIdAndSecretModel(clientId, clientSecret);
        }

        // 如果都没有提供，则抛出异常
        throw new SaOAuth2Exception("请提供 client 信息");
    }

    /**
     * 数据读取：从请求对象中构建 RequestAuthModel
     */
    @Override
    public RequestAuthModel readRequestAuthModel(SaRequest req, Object loginId) {
        RequestAuthModel ra = new RequestAuthModel();
        ra.clientId = req.getParamNotNull(SaOAuth2Consts.Param.client_id);
        ra.responseType = req.getParamNotNull(SaOAuth2Consts.Param.response_type);
        ra.redirectUri = req.getParamNotNull(SaOAuth2Consts.Param.redirect_uri);
        ra.state = req.getParam(SaOAuth2Consts.Param.state);
        // 数据解析
        String scope = req.getParam(SaOAuth2Consts.Param.scope, "");
        ra.scopes = SaOAuth2Manager.getDataConverter().convertScopeStringToList(scope);
        ra.loginId = loginId;
        return ra;
    }


    /**
     * 构建返回值: 获取 token
     */
    @Override
    public Map<String, Object> buildTokenReturnValue(AccessTokenModel at) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("token_type", TokenType.bearer);
        map.put("access_token", at.accessToken);
        map.put("refresh_token", at.refreshToken);
        map.put("expires_in", at.getExpiresIn());
        map.put("refresh_expires_in", at.getRefreshExpiresIn());
        map.put("client_id", at.clientId);
        map.put("scope", SaOAuth2Manager.getDataConverter().convertScopeListToString(at.scopes));
        map.putAll(at.extraData);
        return SaResult.ok().setMap(map);
    }

    /**
     * 构建返回值: password 模式认证 获取 token
     */
    @Override
    public Map<String, Object> buildClientTokenReturnValue(ClientTokenModel ct) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("client_token", ct.clientToken);
        // map.put("access_token", ct.clientToken); // 兼容 OAuth2 协议
        map.put("expires_in", ct.getExpiresIn());
        map.put("client_id", ct.clientId);
        map.put("scope", SaOAuth2Manager.getDataConverter().convertScopeListToString(ct.scopes));
        map.putAll(ct.extraData);
        return SaResult.ok().setMap(map);
    }

}

