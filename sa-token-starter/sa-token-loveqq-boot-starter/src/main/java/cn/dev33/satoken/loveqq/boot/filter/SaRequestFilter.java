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
package cn.dev33.satoken.loveqq.boot.filter;

import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.filter.SaFilter;
import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenContextUtil;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenOperateUtil;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.util.SaTokenConsts;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Order;
import com.kfyty.loveqq.framework.web.core.filter.Filter;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 全局鉴权过滤器
 * <p>
 * 默认优先级为 -100，尽量保证在其它过滤器之前执行
 * </p>
 *
 * @author click33
 * @since 1.19.0
 */
@Order(SaTokenConsts.ASSEMBLY_ORDER)
public class SaRequestFilter implements SaFilter, Filter {

    // ------------------------ 设置此过滤器 拦截 & 放行 的路由

    /**
     * 拦截路由
     */
    public List<String> includeList = new ArrayList<>();

    /**
     * 放行路由
     */
    public List<String> excludeList = new ArrayList<>();

    @Override
    public SaRequestFilter addInclude(String... paths) {
        includeList.addAll(Arrays.asList(paths));
        return this;
    }

    @Override
    public SaRequestFilter addExclude(String... paths) {
        excludeList.addAll(Arrays.asList(paths));
        return this;
    }

    @Override
    public SaRequestFilter setIncludeList(List<String> pathList) {
        includeList = pathList;
        return this;
    }

    @Override
    public SaRequestFilter setExcludeList(List<String> pathList) {
        excludeList = pathList;
        return this;
    }


    // ------------------------ 钩子函数

    /**
     * 认证函数：每次请求执行
     */
    public SaFilterAuthStrategy auth = r -> {
    };

    /**
     * 异常处理函数：每次[认证函数]发生异常时执行此函数
     */
    public SaFilterErrorStrategy error = e -> {
        throw new SaTokenException(e);
    };

    /**
     * 前置函数：在每次[认证函数]之前执行
     * <b>注意点：前置认证函数将不受 includeList 与 excludeList 的限制，所有路由的请求都会进入 beforeAuth</b>
     */
    public SaFilterAuthStrategy beforeAuth = r -> {
    };

    @Override
    public SaRequestFilter setAuth(SaFilterAuthStrategy auth) {
        this.auth = auth;
        return this;
    }

    @Override
    public SaRequestFilter setError(SaFilterErrorStrategy error) {
        this.error = error;
        return this;
    }

    @Override
    public SaRequestFilter setBeforeAuth(SaFilterAuthStrategy beforeAuth) {
        this.beforeAuth = beforeAuth;
        return this;
    }


    // ------------------------ doFilter

    @Override
    public Continue doFilter(ServerRequest request, ServerResponse response) {
        SaTokenContextModelBox prev = SaTokenContextUtil.setContext(request, response);
        try {
            beforeAuth.run(null);
            SaRouter.match(includeList).notMatch(excludeList).check(r -> auth.run(null));
        } catch (StopMatchException ignored) {
            // ignored
        } catch (BackResultException e) {
            SaTokenOperateUtil.writeResult(response, e.getMessage());
            return Continue.FALSE;
        } catch (Throwable e) {
            SaTokenOperateUtil.writeResult(response, String.valueOf(error.run(e)));
            return Continue.FALSE;
        } finally {
            SaTokenContextUtil.clearContext(prev);
        }

        // 执行
        return Continue.TRUE;
    }
}
