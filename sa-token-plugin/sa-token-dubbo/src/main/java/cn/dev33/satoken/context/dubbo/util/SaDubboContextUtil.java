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
package cn.dev33.satoken.context.dubbo.util;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.dubbo.model.SaRequestForDubbo;
import cn.dev33.satoken.context.dubbo.model.SaResponseForDubbo;
import cn.dev33.satoken.context.dubbo.model.SaStorageForDubbo;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import org.apache.dubbo.rpc.RpcContext;


/**
 * SaTokenContext 上下文读写工具类
 *
 * @author click33
 * @since 1.42.0
 */
public class SaDubboContextUtil {

	/**
	 * 写入当前上下文
	 * @param rpcContext /
	 */
	public static void setContext(RpcContext rpcContext) {
		SaRequest saRequest = new SaRequestForDubbo(RpcContext.getContext());
		SaResponse saResponse = new SaResponseForDubbo(RpcContext.getContext());
		SaStorage saStorage = new SaStorageForDubbo(RpcContext.getContext());
		SaManager.getSaTokenContext().setContext(saRequest, saResponse, saStorage);
	}

	/**
	 * 清除当前上下文
	 */
	public static void clearContext() {
		SaManager.getSaTokenContext().clearContext();
	}

}
