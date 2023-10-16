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
package cn.dev33.satoken.application;

import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 应用全局信息
 *
 * @author click33
 * @since 1.31.0
 */
public class ApplicationInfo {

    /**
     * 应用前缀
     */
    public static String routePrefix;

    /**
     * 为指定 path 裁剪掉 routePrefix 前缀
     * @param path 指定 path
     * @return /
     */
    public static String cutPathPrefix(String path) {
        if(! SaFoxUtil.isEmpty(routePrefix) && ! routePrefix.equals("/") && path.startsWith(routePrefix)){
            path = path.substring(routePrefix.length());
        }
        return path;
    }

}