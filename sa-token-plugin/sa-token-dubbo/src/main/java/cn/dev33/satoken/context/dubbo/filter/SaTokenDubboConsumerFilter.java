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

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContextDefaultImpl;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Sa-Token 整合 Dubbo Consumer 端（调用端）过滤器
 * 
 * @author click33
 * @since 1.34.0
 */
@Activate(group = {CommonConstants.CONSUMER}, order = -30000)
public class SaTokenDubboConsumerFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		// 1、追加 Same-Token 参数
		if(SaManager.getConfig().getCheckSameToken()) {
			RpcContext.getContext().setAttachment(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken()); 
		}
		
		// 2、调用前，向下传递会话Token
		if(SaManager.getSaTokenContext() != SaTokenContextDefaultImpl.defaultContext) {
			RpcContext.getContext().setAttachment(SaTokenConsts.JUST_CREATED, StpUtil.getTokenValueNotCut()); 
		}

		// 3、开始调用
		Result invoke = invoker.invoke(invocation);
		
		// 4、调用后，解析回传的Token值
		StpUtil.setTokenValue(invoke.getAttachment(SaTokenConsts.JUST_CREATED_NOT_PREFIX));
		
		// 5、返回结果
		return invoke;
	}

}
