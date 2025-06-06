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
package com.pj.satoken;

import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenContextUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.web.core.filter.Filter;
import com.kfyty.loveqq.framework.web.core.filter.FilterChain;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

/**
 * 自定义过滤器
 */
@Component
public class MyFilter implements Filter {
    /**
     * 实现该方法，可以实现 servlet/reactor 的统一
     * 但是该方法内部是同步方法，若需要异步，可以实现仅 reactor 支持的 {@link Filter#doFilter(ServerRequest, ServerResponse, FilterChain)} 方法
     *
     * @param request  请求
     * @param response 响应
     */
    @Override
    public Continue doFilter(ServerRequest request, ServerResponse response) {
        System.out.println("进入自定义过滤器");

        // 先 set 上下文，再调用 Sa-Token 同步 API，并在 finally 里清除上下文
        SaTokenContextModelBox prev = SaTokenContextUtil.setContext(request, response);
        try {
            System.out.println(StpUtil.isLogin());
        } finally {
            SaTokenContextUtil.clearContext(prev);
        }

        return Continue.TRUE;
    }
}
