/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.integration.boot2.support;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.servlet.util.SaTokenContextServletUtil;
import cn.dev33.satoken.spring.SpringMVCUtil;

/**
 * 集成测试拦截器辅助：在 MockMvc 场景下手动桥接 Servlet 上下文。
 */
public final class IntegrationInterceptorSupport {

    private IntegrationInterceptorSupport() {
    }

    /** 构造一个只负责写入 Sa-Token 上下文的拦截器（关闭注解鉴权） */
    public static SaInterceptor contextBridgeInterceptor() {
        return new SaInterceptor(handle -> {
            SaTokenContextServletUtil.setContext(SpringMVCUtil.getRequest(), SpringMVCUtil.getResponse());
        }).isAnnotation(false);
    }

}
