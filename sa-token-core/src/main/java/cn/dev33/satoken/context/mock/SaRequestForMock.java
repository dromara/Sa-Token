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
package cn.dev33.satoken.context.mock;

import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.context.model.SaRequest;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 对 SaRequest 包装类的实现（Mock 版）
 *
 * @author click33
 * @since 1.42.0
 */
public class SaRequestForMock implements SaRequest {

	public Map<String, String> parameterMap = new LinkedHashMap<>();
	public Map<String, String> headerMap = new LinkedHashMap<>();
	public Map<String, String> cookieMap = new LinkedHashMap<>();
	public String requestPath;
	public String url;
	public String method;
	public String host;
	public String forwardTo;

	/**
	 * 获取底层源对象 
	 */
	@Override
	public Object getSource() {
		return null;
	}

	/**
	 * 在 [请求体] 里获取一个值 
	 */
	@Override
	public String getParam(String name) {
		return parameterMap.get(name);
	}

	/**
	 * 获取 [请求体] 里提交的所有参数名称
	 * @return 参数名称列表
	 */
	@Override
	public Collection<String> getParamNames(){
		return parameterMap.keySet();
	}

	/**
	 * 获取 [请求体] 里提交的所有参数
	 * @return 参数列表
	 */
	@Override
	public Map<String, String> getParamMap(){
		return parameterMap;
	}

	/**
	 * 在 [请求头] 里获取一个值 
	 */
	@Override
	public String getHeader(String name) {
		return headerMap.get(name);
	}

	/**
	 * 在 [Cookie作用域] 里获取一个值 
	 */
	@Override
	public String getCookieValue(String name) {
		return getCookieLastValue(name);
	}

	/**
	 * 在 [ Cookie作用域 ] 里获取一个值 (第一个此名称的)
	 */
	@Override
	public String getCookieFirstValue(String name){
		return cookieMap.get(name);
	}

	/**
	 * 在 [ Cookie作用域 ] 里获取一个值 (最后一个此名称的)
	 * @param name 键
	 * @return 值
	 */
	@Override
	public String getCookieLastValue(String name){
		return cookieMap.get(name);
	}

	/**
	 * 返回当前请求path (不包括上下文名称) 
	 */
	@Override
	public String getRequestPath() {
		return ApplicationInfo.cutPathPrefix(requestPath);
	}

	/**
	 * 返回当前请求的url，例：http://xxx.com/test
	 * @return see note
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * 返回当前请求的类型 
	 */
	@Override
	public String getMethod() {
		return method;
	}

	/**
	 * 查询请求 host
	 */
	@Override
	public String getHost() {
		return host;
	}

	/**
	 * 转发请求 
	 */
	@Override
	public Object forward(String path) {
		this.forwardTo = path;
		return null;
	}
	
}
