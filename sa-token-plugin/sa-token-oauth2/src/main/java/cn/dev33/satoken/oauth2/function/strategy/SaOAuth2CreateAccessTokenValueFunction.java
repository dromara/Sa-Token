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
package cn.dev33.satoken.oauth2.function.strategy;

import java.util.List;

/**
 * 函数式接口：创建一个 AccessToken value
 *
 * @author click33
 * @since 1.39.0
 */
@FunctionalInterface
public interface SaOAuth2CreateAccessTokenValueFunction {

    /**
     * 创建一个 AccessToken value
     * @param clientId 应用id
     * @param loginId 账号id
     * @param scopes 权限
     * @return AccessToken value
     */
    String execute(String clientId, Object loginId, List<String> scopes);

}