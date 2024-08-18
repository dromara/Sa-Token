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
package cn.dev33.satoken.oauth2.data.loader;

import cn.dev33.satoken.oauth2.data.model.SaClientModel;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.List;

/**
 * Sa-Token OAuth2 数据加载器
 *
 * @author click33
 * @since 1.39.0
 */
public interface SaOAuth2DataLoader {

    /**
     * 根据 id 获取 Client 信息
     *
     * @param clientId 应用id
     * @return ClientModel
     */
    default SaClientModel getClientModel(String clientId) {
        return null;
    }

    /**
     * 根据ClientId 和 LoginId 获取openid
     *
     * @param clientId 应用id
     * @param loginId 账号id
     * @return 此账号在此Client下的openid
     */
    default String getOpenid(String clientId, Object loginId) {
        return null;
    }


    /**
     * 根据 id 获取 Client 信息，不允许为 null
     *
     * @param clientId 应用id
     * @return ClientModel
     */
    default SaClientModel getClientModelNotNull(String clientId) {
        SaClientModel clientModel = getClientModel(clientId);
        if(clientModel == null) {
            throw new SaOAuth2Exception("未找到对应的 Client 信息");
        }
        return clientModel;
    }


    // ------------------- 创建对应 token 的算法

    /**
     * 随机一个 Code
     * @param clientId 应用id
     * @param loginId 账号id
     * @param scopes 权限
     * @return Code
     */
    default String randomCode(String clientId, Object loginId, List<String> scopes) {
        return SaFoxUtil.getRandomString(60);
    }

    /**
     * 随机一个 Access-Token
     * @param clientId 应用id
     * @param loginId 账号id
     * @param scopes 权限
     * @return Access-Token
     */
    default String randomAccessToken(String clientId, Object loginId, List<String> scopes) {
        return SaFoxUtil.getRandomString(60);
    }

    /**
     * 随机一个 Refresh-Token
     * @param clientId 应用id
     * @param loginId 账号id
     * @param scopes 权限
     * @return Refresh-Token
     */
    default String randomRefreshToken(String clientId, Object loginId, List<String> scopes) {
        return SaFoxUtil.getRandomString(60);
    }

    /**
     * 随机一个 Client-Token
     * @param clientId 应用id
     * @param scopes 权限
     * @return Client-Token
     */
    default String randomClientToken(String clientId, List<String> scopes) {
        return SaFoxUtil.getRandomString(60);
    }

}
