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

import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.scope.CommonScope;

/**
 * UserId 权限处理器：在 AccessToken 扩展参数中追加 userid 字段
 *
 * @author click33
 * @since 1.39.0
 */
public class UserIdScopeHandler implements SaOAuth2ScopeAbstractHandler {

    public String getHandlerScope() {
        return CommonScope.USERID;
    }

    @Override
    public void workAccessToken(AccessTokenModel at) {
        at.extraData.put(SaOAuth2Consts.ExtraField.userid, at.loginId);
    }

    @Override
    public void workClientToken(ClientTokenModel ct) {

    }

}