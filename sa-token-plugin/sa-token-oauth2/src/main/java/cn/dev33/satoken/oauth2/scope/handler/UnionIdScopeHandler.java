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

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.scope.CommonScope;

/**
 * UnionId Scope 处理器，在返回的 AccessToken 中增加 unionid 字段
 *
 * @author click33
 * @since 1.40.0
 */
public class UnionIdScopeHandler implements SaOAuth2ScopeHandlerInterface {

    @Override
    public String getHandlerScope() {
        return CommonScope.UNIONID;
    }

    @Override
    public void workAccessToken(AccessTokenModel at) {
        SaOAuth2DataLoader dataLoader = SaOAuth2Manager.getDataLoader();
        SaClientModel cm = dataLoader.getClientModelNotNull(at.clientId);
        at.extraData.put(SaOAuth2Consts.ExtraField.unionid, dataLoader.getUnionid(cm.subjectId, at.loginId));
    }

    @Override
    public void workClientToken(ClientTokenModel ct) {

    }

}