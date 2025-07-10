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
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.loveqq.boot.model.LoveqqSaRequest;
import cn.dev33.satoken.loveqq.boot.model.LoveqqSaResponse;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenContextUtil;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenOperateUtil;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Order;
import com.kfyty.loveqq.framework.web.core.filter.Filter;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

/**
 * 防火墙校验过滤器 (基于 loveqq-framework 统一 Filter，可以统一 servlet 和 reactor 配置)
 *
 * @author click33
 * @since 1.37.0
 */
@Component
@Order(SaTokenConsts.FIREWALL_CHECK_FILTER_ORDER)
public class SaFirewallCheckFilter implements Filter {

    @Override
    public Continue doFilter(ServerRequest request, ServerResponse response) {
        LoveqqSaRequest saRequest = new LoveqqSaRequest(request);
        LoveqqSaResponse saResponse = new LoveqqSaResponse(response);
        SaTokenContextModelBox prev = SaTokenContextUtil.setContext(request, response);
        try {
            SaFirewallStrategy.instance.check.execute(saRequest, saResponse, null);
        } catch (StopMatchException ignored) {
            // ignored
        } catch (BackResultException e) {
            SaTokenOperateUtil.writeResult(response, e.getMessage());
            return Continue.FALSE;
        } catch (FirewallCheckException e) {
            if (SaFirewallStrategy.instance.checkFailHandle == null) {
                SaTokenOperateUtil.writeResult(response, e.getMessage());
            } else {
                SaFirewallStrategy.instance.checkFailHandle.run(e, saRequest, saResponse, null);
            }
            return Continue.FALSE;
        } finally {
            SaTokenContextUtil.clearContext(prev);
        }

        // 更多异常则不处理，交由 Web 框架处理

        // 向内执行
        return Continue.TRUE;
    }
}
