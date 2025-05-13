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
package cn.dev33.satoken.oauth2.scope;

/**
 * OAuth2 常见 Scope 定义
 *
 * @author click33
 * @since 1.39.0
 */
public final class CommonScope {

    private CommonScope() {
    }

    /**
     * 获取 openid
     */
    public static final String OPENID = "openid";

    /**
     * 获取 unionid
     */
    public static final String UNIONID = "unionid";

    /**
     * 获取 userid
     */
    public static final String USERID = "userid";

    /**
     * 获取 id_token
     */
    public static final String OIDC = "oidc";

}