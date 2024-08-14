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

import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts.TokenType;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
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
     * 构建返回值: 获取 token
     */
    public Map<String, Object> buildTokenReturnValue(AccessTokenModel at) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("token_type", TokenType.bearer);
        map.put("access_token", at.accessToken);
        map.put("refresh_token", at.refreshToken);
        map.put("expires_in", at.getExpiresIn());
        map.put("refresh_expires_in", at.getRefreshExpiresIn());
        map.put("client_id", at.clientId);
        map.put("scope", at.scope);
        map.put("openid", at.openid);
        return SaResult.ok().setMap(map);
    }

    /**
     * 构建返回值: password 模式认证 获取 token
     */
    @Override
    public Map<String, Object> buildClientTokenReturnValue(ClientTokenModel ct) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("client_token", ct.clientToken);
        map.put("access_token", ct.clientToken); // 兼容 OAuth2 协议
        map.put("expires_in", ct.getExpiresIn());
        map.put("client_id", ct.clientId);
        map.put("scope", ct.scope);
        return SaResult.ok().setMap(map);
    }


}

