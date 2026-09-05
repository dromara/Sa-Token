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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.spring.SaTokenContextForSpring;
import cn.dev33.satoken.spring.SpringMVCUtil;

/**
 * MockMvc 环境下 {@code servletPath} 为空导致路由匹配异常的兼容处理。
 */
public final class ServletPathContextSupport {

    private ServletPathContextSupport() {
    }

    /** 将当前请求路径回退为 requestURI，修复 SaRouter 在 MockMvc 下的路径判定 */
    public static void applyServletPathWorkaround() {
        SaManager.setSaTokenContext(new SaTokenContextForSpring() {
            @Override
            public SaRequest getRequest() {
                return new SaRequestForServlet(SpringMVCUtil.getRequest()) {
                    @Override
                    public String getRequestPath() {
                        return request.getRequestURI();
                    }
                };
            }
        });
    }

}
