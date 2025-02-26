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

import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
 * 防火墙校验过滤器 (基于 Jakarta-Servlet)
 *
 * @author click33
 * @since 1.37.0
 */
@Order(SaTokenConsts.FIREWALL_CHECK_FILTER_ORDER)
public class SaFirewallCheckFilterForJakartaServlet implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		SaRequestForServlet saRequest = new SaRequestForServlet(req);
		SaResponseForServlet saResponse = new SaResponseForServlet(res);

		try {
			SaFirewallStrategy.instance.check.execute(saRequest, saResponse, null);
		}
		catch (StopMatchException e) {
			// 如果是 StopMatchException 异常，代表通过了防火墙验证，进入 Controller
		}
		catch (FirewallCheckException e) {
			// FirewallCheckException 异常则交由异常处理策略处理
			if(SaFirewallStrategy.instance.checkFailHandle == null) {
				response.setContentType("text/plain; charset=utf-8");
				response.getWriter().print(e.getMessage());
				response.getWriter().flush();
			} else {
				SaFirewallStrategy.instance.checkFailHandle.run(e, saRequest, saResponse, null);
			}
			return;
		}
		// 更多异常则不处理，交由 Web 框架处理

		// 向内执行
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}
	
	@Override
	public void destroy() {
	}

	
	
}
