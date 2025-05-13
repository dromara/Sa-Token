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
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts.Param;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts.TokenType;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.request.ClientIdAndSecretModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
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
        String clientId = request.getParam(Param.client_id);
        String clientSecret = request.getParam(Param.client_secret);
        if(SaFoxUtil.isNotEmpty(clientId)) {
            return new ClientIdAndSecretModel(clientId, clientSecret);
        }

        // 如果请求参数中没有提供 client_id 参数，则尝试从 Authorization 中获取
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
        throw new SaOAuth2Exception("请提供 client 信息").setCode(SaOAuth2ErrorCode.CODE_30191);
    }

    /**
     * 数据读取：从请求对象中读取 AccessToken，获取不到返回 null
     */
    @Override
    public String readAccessToken(SaRequest request) {
        // 优先从请求参数中获取，可以读取到的话直接返回
        String accessToken = request.getParam(Param.access_token);
        if(SaFoxUtil.isNotEmpty(accessToken)) {
            return accessToken;
        }

        // 如果请求参数中没有提供 access_token 参数，则尝试从 Authorization 中获取
        String authorizationValue = request.getHeader(Param.Authorization);
        if(SaFoxUtil.isEmpty(authorizationValue)) {
            return null;
        }

        // 判断前缀，裁剪
        String prefix = TokenType.Bearer + " ";
        if(authorizationValue.startsWith(prefix)) {
            return authorizationValue.substring(prefix.length());
        }

        // 前缀不符合，返回 null
        return null;
    }

    /**
     * 数据读取：从请求对象中读取 ClientToken，获取不到返回 null
     */
    @Override
    public String readClientToken(SaRequest request) {
        // 优先从请求参数中获取，可以读取到的话直接返回
        String clientToken = request.getParam(Param.client_token);
        if(SaFoxUtil.isNotEmpty(clientToken)) {
            return clientToken;
        }

        // 如果请求参数中没有提供 client_token 参数，则尝试从 Authorization 中获取
        String authorizationValue = request.getHeader(Param.Authorization);
        if(SaFoxUtil.isEmpty(authorizationValue)) {
            return null;
        }

        // 判断前缀，裁剪
        String prefix = TokenType.Bearer + " ";
        if(authorizationValue.startsWith(prefix)) {
            return authorizationValue.substring(prefix.length());
        }

        // 前缀不符合，返回 null
        return null;
    }

    /**
     * 数据读取：从请求对象中构建 RequestAuthModel
     */
    @Override
    public RequestAuthModel readRequestAuthModel(SaRequest req, Object loginId) {
        RequestAuthModel ra = new RequestAuthModel();
        ra.clientId = req.getParamNotNull(Param.client_id);
        ra.responseType = req.getParamNotNull(Param.response_type);
        ra.redirectUri = req.getParamNotNull(Param.redirect_uri);
        ra.state = req.getParam(Param.state);
        ra.nonce = req.getParam(Param.nonce);
        ra.scopes = SaOAuth2Manager.getDataConverter().convertScopeStringToList(req.getParam(Param.scope));
        ra.loginId = loginId;
        return ra;
    }


    /**
     * 构建返回值: 获取 token
     */
    @Override
    public Map<String, Object> buildAccessTokenReturnValue(AccessTokenModel at) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("token_type", at.tokenType);
        map.put("access_token", at.accessToken);
        map.put("refresh_token", at.refreshToken);
        map.put("expires_in", at.getExpiresIn());
        map.put("refresh_expires_in", at.getRefreshExpiresIn());
        map.put("client_id", at.clientId);
        map.put("scope", SaOAuth2Manager.getDataConverter().convertScopeListToString(at.scopes));
        map.putAll(at.extraData);
        SaResult result = SaResult.ok().setMap(map);
        if(SaOAuth2Manager.getServerConfig().hideStatusField) {
            result.removeDefaultFields();
        }
        return result;
    }

    /**
     * 构建返回值: 凭证式 模式认证 获取 token
     */
    @Override
    public Map<String, Object> buildClientTokenReturnValue(ClientTokenModel ct) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("token_type", ct.tokenType);
        map.put("client_token", ct.clientToken);
        if(SaOAuth2Manager.getServerConfig().getMode4ReturnAccessToken()) {
             map.put("access_token", ct.clientToken);
        }
        map.put("expires_in", ct.getExpiresIn());
        map.put("client_id", ct.clientId);
        map.put("scope", SaOAuth2Manager.getDataConverter().convertScopeListToString(ct.scopes));
        map.putAll(ct.extraData);

        SaResult result = SaResult.ok().setMap(map);
        if(SaOAuth2Manager.getServerConfig().hideStatusField) {
            result.removeDefaultFields();
        }
        return result;
    }

}

