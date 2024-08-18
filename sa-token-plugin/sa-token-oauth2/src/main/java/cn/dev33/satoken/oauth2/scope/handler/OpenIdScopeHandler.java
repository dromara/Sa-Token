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
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.scope.CommonScope;

/**
 * 所有OAuth2 权限处理器的父接口
 *
 * @author click33
 * @since 1.39.0
 */
public class OpenIdScopeHandler implements SaOAuth2ScopeAbstractHandler {

    /**
     * 获取所要处理的权限
     */
    public String getHandlerScope() {
        return CommonScope.OPENID;
    }

    /**
     * 所需要执行的方法
     */
    public void work(AccessTokenModel at) {
        System.out.println("追加 openid " + at.accessToken);
        at.openid = SaOAuth2Manager.getDataLoader().getOpenid(at.clientId, at.loginId);
    }

}