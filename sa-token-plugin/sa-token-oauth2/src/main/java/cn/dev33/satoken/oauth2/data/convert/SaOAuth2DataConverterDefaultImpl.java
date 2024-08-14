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
package cn.dev33.satoken.oauth2.data.convert;

import cn.dev33.satoken.util.SaFoxUtil;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token OAuth2 数据格式转换器，默认实现类
 *
 * @author click33
 * @since 1.39.0
 */
public class SaOAuth2DataConverterDefaultImpl implements SaOAuth2DataConverter {

    /**
     * 转换 scope 数据格式：String -> List
     */
    @Override
    public List<String> convertScopeStringToList(String scopeString) {
        if(SaFoxUtil.isEmpty(scopeString)) {
            return Collections.emptyList();
        }
        // 兼容以下三种分隔符：空格、逗号、%20
        scopeString = scopeString.replaceAll(" ", ",");
        scopeString = scopeString.replaceAll("%20", ",");
        return SaFoxUtil.convertStringToList(scopeString);
    }

    /**
     * 转换 scope 数据格式：List -> String
     */
    @Override
    public String convertScopeListToString(List<String> scopeList) {
        return SaFoxUtil.convertListToString(scopeList);
    }

    /**
     * 转换 AllowUrl 数据格式：String -> List
     */
    @Override
    public List<String> convertAllowUrlStringToList(String allowUrl) {
        if(SaFoxUtil.isEmpty(allowUrl)) {
            return Collections.emptyList();
        }
        return SaFoxUtil.convertStringToList(allowUrl);
    }
}

