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
package cn.dev33.satoken.loveqq.boot.support;

import com.kfyty.loveqq.framework.core.support.AntPathMatcher;
import com.kfyty.loveqq.framework.core.support.PatternMatcher;

/**
 * 路由匹配工具类：持有 PathMatcher 全局引用，方便快捷的调用 PathMatcher 相关方法
 *
 * @author click33
 * @since 1.34.0
 */
public class SaPathMatcherHolder {

    private SaPathMatcherHolder() {
    }

    /**
     * 路由匹配器
     */
    public static PatternMatcher pathMatcher;

    /**
     * 获取路由匹配器
     *
     * @return 路由匹配器
     */
    public static PatternMatcher getPathMatcher() {
        if (pathMatcher == null) {
            pathMatcher = new AntPathMatcher();
        }
        return pathMatcher;
    }

    /**
     * 写入路由匹配器
     *
     * @param pathMatcher 路由匹配器
     */
    public static void setPathMatcher(PatternMatcher pathMatcher) {
        SaPathMatcherHolder.pathMatcher = pathMatcher;
    }

    /**
     * 判断：指定路由匹配符是否可以匹配成功指定路径
     *
     * @param pattern 路由匹配符
     * @param path    要匹配的路径
     * @return 是否匹配成功
     */
    public static boolean match(String pattern, String path) {
        return getPathMatcher().matches(pattern, path);
    }
}
