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
package cn.dev33.satoken.context.dubbo3;

import cn.dev33.satoken.context.dubbo3.model.SaRequestForDubbo3;
import cn.dev33.satoken.context.dubbo3.model.SaResponseForDubbo3;
import cn.dev33.satoken.context.dubbo3.model.SaStorageForDubbo3;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.second.SaTokenSecondContext;
import cn.dev33.satoken.exception.ApiDisabledException;
import org.apache.dubbo.rpc.RpcContext;

/**
 * Sa-Token 二级上下文 [ Dubbo3版本 ]
 * 
 * @author click33
 * @since <= 1.34.0
 */
public class SaTokenSecondContextForDubbo3 implements SaTokenSecondContext {

	@Override
	public SaRequest getRequest() {
		return new SaRequestForDubbo3(RpcContext.getServiceContext());
	}

	@Override
	public SaResponse getResponse() {
		return new SaResponseForDubbo3(RpcContext.getServiceContext());
	}

	@Override
	public SaStorage getStorage() {
		return new SaStorageForDubbo3(RpcContext.getServiceContext());
	}

	@Override
	public boolean matchPath(String pattern, String path) {
		throw new ApiDisabledException();
	}

	@Override
	public boolean isValid() {
		return RpcContext.getServiceContext() != null;
	}
	
}
