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

import cn.dev33.satoken.context.model.SaRequest;
import org.apache.dubbo.rpc.RpcContext;

import java.util.List;
import java.util.Map;

/**
 * 对 SaRequest 包装类的实现（Dubbo 版）
 *
 * @author click33
 * @since 1.34.0
 */
public class SaRequestForDubbo implements SaRequest {

	/**
	 * 底层对象 
	 */
	protected RpcContext rpcContext;
	
	/**
	 * 实例化
	 * @param rpcContext rpcContext对象 
	 */
	public SaRequestForDubbo(RpcContext rpcContext) {
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
	 * 在 [请求体] 里获取一个值 
	 */
	@Override
	public String getParam(String name) {
		// 不传播 url 参数 
		return null;
	}

	/**
	 * 获取 [请求体] 里提交的所有参数名称
	 * @return 参数名称列表
	 */
	@Override
	public List<String> getParamNames(){
		return null;
	}

	/**
	 * 获取 [请求体] 里提交的所有参数
	 * @return 参数列表
	 */
	@Override
	public Map<String, String> getParamMap(){
		return null;
	}

	/**
	 * 在 [请求头] 里获取一个值 
	 */
	@Override
	public String getHeader(String name) {
		// 不传播 header 参数 
		return null;
	}

	/**
	 * 在 [Cookie作用域] 里获取一个值 
	 */
	@Override
	public String getCookieValue(String name) {
		// 不传播 cookie 参数 
		return null;
	}

	/**
	 * 返回当前请求path (不包括上下文名称)  
	 */
	@Override
	public String getRequestPath() {
		// 不传播 requestPath 
		return null;
	}

	/**
	 * 返回当前请求的url，例：http://xxx.com/test
	 * @return see note
	 */
	public String getUrl() {
		// 不传播 url 
		return null;
	}
	
	/**
	 * 返回当前请求的类型 
	 */
	@Override
	public String getMethod() {
		// 不传播 method 
		return null;
	}

	/**
	 * 转发请求 
	 */
	@Override
	public Object forward(String path) {
		// 不传播 forward 动作 
		return null;
	}

}
