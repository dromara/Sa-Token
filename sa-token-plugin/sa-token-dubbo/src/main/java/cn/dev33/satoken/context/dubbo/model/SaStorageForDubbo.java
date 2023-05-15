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
package cn.dev33.satoken.context.dubbo.model;

import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.util.SaTokenConsts;
import org.apache.dubbo.rpc.RpcContext;

/**
 * 对 SaStorage 包装类的实现（Dubbo 版）
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaStorageForDubbo implements SaStorage {

	/**
	 * 底层对象 
	 */
	protected RpcContext rpcContext;
	
	/**
	 * 实例化
	 * @param rpcContext rpcContext对象 
	 */
	public SaStorageForDubbo(RpcContext rpcContext) {
		this.rpcContext = rpcContext;
	}
	
	/**
	 * 获取底层源对象 
	 */
	@Override
	public Object getSource() {
		return rpcContext;
	}

	/**
	 * 在 [Request作用域] 里写入一个值 
	 */
	@Override
	public SaStorageForDubbo set(String key, Object value) {
		rpcContext.setObjectAttachment(key, value);
		// 如果是token写入，则回传到Consumer端  
		if(key.equals(SaTokenConsts.JUST_CREATED_NOT_PREFIX)) {
			RpcContext.getServerContext().setAttachment(key, value);
		}
		return this;
	}

	/**
	 * 在 [Request作用域] 里获取一个值 
	 */
	@Override
	public Object get(String key) {
		return rpcContext.getObjectAttachment(key);
	}

	/**
	 * 在 [Request作用域] 里删除一个值 
	 */
	@Override
	public SaStorageForDubbo delete(String key) {
		rpcContext.removeAttachment(key);
		return this;
	}

}
