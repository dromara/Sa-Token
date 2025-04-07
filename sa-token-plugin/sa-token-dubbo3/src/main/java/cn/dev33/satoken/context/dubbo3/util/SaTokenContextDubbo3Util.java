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
package cn.dev33.satoken.context.dubbo3.util;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.dubbo3.model.SaRequestForDubbo3;
import cn.dev33.satoken.context.dubbo3.model.SaResponseForDubbo3;
import cn.dev33.satoken.context.dubbo3.model.SaStorageForDubbo3;
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
public class SaTokenContextDubbo3Util {

	/**
	 * 写入当前上下文
	 * @param rpcContext /
	 */
	public static void setContext(RpcContext rpcContext) {
		SaRequest saRequest = new SaRequestForDubbo3(RpcContext.getServiceContext());
		SaResponse saResponse = new SaResponseForDubbo3(RpcContext.getServiceContext());
		SaStorage saStorage = new SaStorageForDubbo3(RpcContext.getServiceContext());
		SaManager.getSaTokenContext().setContext(saRequest, saResponse, saStorage);
	}

	/**
	 * 清除当前上下文
	 */
	public static void clearContext() {
		SaManager.getSaTokenContext().clearContext();
	}

}
