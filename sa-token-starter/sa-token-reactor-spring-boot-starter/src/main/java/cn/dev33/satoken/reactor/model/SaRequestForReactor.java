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
package cn.dev33.satoken.reactor.model;


import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import java.util.Collection;
import java.util.Map;

/**
 * 对 SaRequest 包装类的实现（Reactor 响应式编程版）
 *
 * @author click33
 * @since 1.19.0
 */
public class SaRequestForReactor implements SaRequest {

	/**
	 * 底层Request对象
	 */
	protected ServerHttpRequest request;
	
	/**
	 * 实例化
	 * @param request request对象 
	 */
	public SaRequestForReactor(ServerHttpRequest request) {
		this.request = request;
	}
	
	/**
	 * 获取底层源对象 
	 */
	@Override
	public Object getSource() {
		return request;
	}

	/**
	 * 在 [请求体] 里获取一个值 
	 */
	@Override
	public String getParam(String name) {
		return request.getQueryParams().getFirst(name);
	}

	/**
	 * 获取 [请求体] 里提交的所有参数名称
	 * @return 参数名称列表
	 */
	@Override
	public Collection<String> getParamNames(){
		return request.getQueryParams().keySet();
	}

	/**
	 * 获取 [请求体] 里提交的所有参数
	 * @return 参数列表
	 */
	@Override
	public Map<String, String> getParamMap(){
		return request.getQueryParams().toSingleValueMap();
	}

	/**
	 * 在 [请求头] 里获取一个值 
	 */
	@Override
	public String getHeader(String name) {
		return request.getHeaders().getFirst(name);
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
		HttpCookie cookie = request.getCookies().getFirst(name);
		if(cookie == null) {
			return null;
		}
		return cookie.getValue();
	}

	/**
	 * 在 [ Cookie作用域 ] 里获取一个值 (最后一个此名称的)
	 * @param name 键
	 * @return 值
	 */
	@Override
	public String getCookieLastValue(String name){
		String value = null;
		String cookieStr = getHeader("Cookie");
		if(SaFoxUtil.isNotEmpty(cookieStr)) {
			String[] cookieItems = cookieStr.split(";");
			for (String item : cookieItems) {
				String[] kv = item.split("=");
				if (kv.length == 2) {
					if (kv[0].trim().equals(name)) {
						value = kv[1].trim();
					}
				}
			}
		}
		return value;

		// 此种写法无法获取到最后一个 Cookie，WebFlux 底层代码应该是有bug，前端提交多个同名Cookie时只能解析出第一个来
//		List<HttpCookie> cookies = request.getCookies().get(name);
//		if(cookies.isEmpty()) {
//			return null;
//		}
//		return cookies.get(cookies.size() - 1).getValue();
	}

	/**
	 * 返回当前请求path (不包括上下文名称)  
	 */
	@Override
	public String getRequestPath() {
		return ApplicationInfo.cutPathPrefix(request.getPath().toString());
	}

	/**
	 * 返回当前请求的url，例：http://xxx.com/test
	 * @return see note
	 */
	public String getUrl() {
		String currDomain = SaManager.getConfig().getCurrDomain();
		if( ! SaFoxUtil.isEmpty(currDomain)) {
			return currDomain + this.getRequestPath();
		}
		return request.getURI().toString();
	}
	
	/**
	 * 返回当前请求的类型 
	 */
	@Override
	public String getMethod() {
		return request.getMethod().name();
	}

	/**
	 * 查询请求 host
	 */
	@Override
	public String getHost() {
		return request.getURI().getHost();
	}

	/**
	 * 转发请求 
	 */
	@Override
	public Object forward(String path) {
		ServerWebExchange exchange = SaReactorSyncHolder.getExchange();
		WebFilterChain chain = exchange.getAttribute(SaReactorHolder.CHAIN_KEY);
		
		ServerHttpRequest newRequest = request.mutate().path(path).build();
		ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
		
		return chain.filter(newExchange); 
	}
	
}
