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
import cn.dev33.satoken.context.SaTokenContextDefaultImpl;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * Sa-Token 整合 Dubbo Consumer 端（调用端）过滤器
 * 
 * @author click33
 * @since 1.34.0
 */
@Activate(group = {CommonConstants.CONSUMER}, order = SaTokenConsts.RPC_PERMISSION_FILTER_ORDER)
public class SaTokenDubboConsumerFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		// 追加 Same-Token 参数
		if(SaManager.getConfig().getCheckSameToken()) {
			RpcContext.getContext().setAttachment(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken()); 
		}

		// 无上下文时只做简单调用，不传递会话 token
		if( ! SaHolder.getContext().isValid()) {
			return invoker.invoke(invocation);
		}

		// 1、调用前，向下传递会话Token
		if(SaManager.getSaTokenContext() != SaTokenContextDefaultImpl.defaultContext) {
			RpcContext.getContext().setAttachment(SaTokenConsts.JUST_CREATED, StpUtil.getTokenValueNotCut()); 
		}

		// 2、开始调用
		Result invoke = invoker.invoke(invocation);
		
		// 3、调用后，解析回传的Token值
		StpUtil.setTokenValue(invoke.getAttachment(SaTokenConsts.JUST_CREATED_NOT_PREFIX));
		
		// note
		return invoke;
	}

}
