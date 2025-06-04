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
import cn.dev33.satoken.loveqq.boot.utils.SaTokenContextUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Order;
import com.kfyty.loveqq.framework.web.core.filter.Filter;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

/**
 * SaTokenContext 上下文初始化过滤器 (基于 loveqq-framework 统一 Filter，可以统一 servlet 和 reactor 配置)
 *
 * @author click33
 * @since 1.42.0
 */
@Component
@Order(SaTokenConsts.SA_TOKEN_CONTEXT_FILTER_ORDER)
public class SaTokenContextFilter implements Filter {

	@Override
	public Continue doFilter(ServerRequest request, ServerResponse response) {
		SaTokenContextModelBox prev = SaTokenContextUtil.setContext(request, response);
		return Continue.ofTrue(() -> SaTokenContextUtil.clearContext(prev));
	}
}
