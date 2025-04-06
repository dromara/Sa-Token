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
package cn.dev33.satoken.filter;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.util.SaServletOperateUtil;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 防火墙校验过滤器 (基于 Servlet)
 *
 * @author click33
 * @since 1.37.0
 */
@Order(SaTokenConsts.FIREWALL_CHECK_FILTER_ORDER)
public class SaFirewallCheckFilterForServlet implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		SaRequestForServlet saRequest = new SaRequestForServlet(req);
		SaResponseForServlet saResponse = new SaResponseForServlet(res);

		try {
			SaFirewallStrategy.instance.check.execute(saRequest, saResponse, null);
		}
		catch (StopMatchException ignored) {}
		catch (BackResultException e) {
			SaServletOperateUtil.writeResult(response, e.getMessage());
			return;
		}
		catch (FirewallCheckException e) {
			if(SaFirewallStrategy.instance.checkFailHandle == null) {
				SaServletOperateUtil.writeResult(response, e.getMessage());
            } else {
				SaFirewallStrategy.instance.checkFailHandle.run(e, saRequest, saResponse, null);
            }
            return;
        }
		// 更多异常则不处理，交由 Web 框架处理
		
		// 向内执行
		chain.doFilter(request, response);
	}

}
