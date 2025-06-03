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

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenContextUtil;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenOperateUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Order;
import com.kfyty.loveqq.framework.web.core.filter.Filter;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

/**
 * CORS 跨域策略过滤器
 *
 * @author click33
 * @since 1.42.0
 */
@Component
@Order(SaTokenConsts.CORS_FILTER_ORDER)
public class SaTokenCorsFilter implements Filter {

    @Override
    public Continue doFilter(ServerRequest request, ServerResponse response) {
        SaTokenContextModelBox prev = SaTokenContextUtil.setContext(request, response);
        try {
            SaTokenContextModelBox box = SaHolder.getContext().getModelBox();
            SaStrategy.instance.corsHandle.execute(box.getRequest(), box.getResponse(), box.getStorage());
        } catch (StopMatchException ignored) {
            // ignored
        } catch (BackResultException e) {
            SaTokenOperateUtil.writeResult(response, e.getMessage());
            return Continue.FALSE;
        } finally {
            SaTokenContextUtil.clearContext(prev);
        }
        return Continue.TRUE;
    }
}
