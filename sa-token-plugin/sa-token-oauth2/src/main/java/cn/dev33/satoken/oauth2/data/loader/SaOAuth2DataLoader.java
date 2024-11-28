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

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2ClientModelException;
import cn.dev33.satoken.secure.SaSecureUtil;

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
        // 默认从内存配置中读取数据
        return SaOAuth2Manager.getServerConfig().getClients().get(clientId);
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
            throw new SaOAuth2ClientModelException("无效 client_id: " + clientId)
                    .setClientId(clientId)
                    .setCode(SaOAuth2ErrorCode.CODE_30105);
        }
        return clientModel;
    }

    /**
     * 根据 ClientId 和 LoginId 获取openid
     *
     * @param clientId 应用id
     * @param loginId 账号id
     * @return 此账号在此Client下的openid
     */
    default String getOpenid(String clientId, Object loginId) {
        return SaSecureUtil.md5(SaOAuth2Manager.getServerConfig().getOpenidDigestPrefix() + "_" + clientId + "_" + loginId);
    }

    /**
     * 根据 SubjectId 和 LoginId 获取 unionid
     *
     * @param subjectId 应用主体id
     * @param loginId 账号id
     * @return 此账号在此Client下的openid
     */
    default String getUnionid(String subjectId, Object loginId) {
        return SaSecureUtil.md5(SaOAuth2Manager.getServerConfig().getUnionidDigestPrefix() + "_" + subjectId + "_" + loginId);
    }

}
