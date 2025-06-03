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
package cn.dev33.satoken.context.dubbo.filter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.dubbo.util.SaTokenContextDubboUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * Sa-Token 整合 Dubbo 上下文初始化过滤器
 * 
 * @author click33
 * @since 1.42.0
 */
@Activate(group = {CommonConstants.PROVIDER}, order = SaTokenConsts.RPC_CONTEXT_FILTER_ORDER)
public class SaTokenDubboContextFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) {
		if(SaHolder.getContext().isValid()) {
			return invoker.invoke(invocation);
		}
		try {
			SaTokenContextDubboUtil.setContext(RpcContext.getContext());
			return invoker.invoke(invocation);
		} finally {
			SaManager.getSaTokenContext().clearContext();
		}
	}

}
