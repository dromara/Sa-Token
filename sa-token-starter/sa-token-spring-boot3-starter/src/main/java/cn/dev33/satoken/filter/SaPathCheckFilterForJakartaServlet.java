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

import cn.dev33.satoken.exception.RequestPathInvalidException;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
 * 校验请求 path 是否合法
 *
 * @author click33
 * @since 1.37.0
 */
@Order(SaTokenConsts.PATH_CHECK_FILTER_ORDER)
public class SaPathCheckFilterForJakartaServlet implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// 校验本次请求 path 是否合法
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			SaStrategy.instance.checkRequestPath.run(req.getRequestURI(), request, response);
		} catch (RequestPathInvalidException e) {
			if(SaStrategy.instance.requestPathInvalidHandle == null) {
				response.setContentType("text/plain; charset=utf-8");
				response.getWriter().print(e.getMessage());
				response.getWriter().flush();
            } else {
				SaStrategy.instance.requestPathInvalidHandle.run(e, request, response);
            }
            return;
        }
		
		// 向下执行
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}
	
	@Override
	public void destroy() {
	}

	
	
}
