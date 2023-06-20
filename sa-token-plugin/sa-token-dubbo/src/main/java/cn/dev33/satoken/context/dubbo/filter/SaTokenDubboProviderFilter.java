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
import org.apache.dubbo.rpc.RpcException;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.same.SaSameUtil;

/**
 * Sa-Token 整合 Dubbo Provider端（被调用端）过滤器
 * 
 * @author click33
 * @since 1.34.0
 */
@Activate(group = {CommonConstants.PROVIDER}, order = -30000)
public class SaTokenDubboProviderFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		// RPC 调用鉴权 
		if(SaManager.getConfig().getCheckSameToken()) {
			String idToken = invocation.getAttachment(SaSameUtil.SAME_TOKEN);

			// dubbo部分协议会将参数变为小写，此处需要额外处理一下，详细参考：https://gitee.com/dromara/sa-token/issues/I4WXQG
			if(idToken == null) {
				idToken = invocation.getAttachment(SaSameUtil.SAME_TOKEN.toLowerCase());
			}
			SaSameUtil.checkToken(idToken);
		}
		
		// 开始调用
		return invoker.invoke(invocation);
	}

}
