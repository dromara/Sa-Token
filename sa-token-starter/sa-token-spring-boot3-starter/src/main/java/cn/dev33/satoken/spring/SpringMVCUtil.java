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
package cn.dev33.satoken.spring;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.dev33.satoken.error.SaSpringBootErrorCode;
import cn.dev33.satoken.exception.NotWebContextException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * SpringMVC 相关操作工具类，快速获取当前会话的 HttpServletRequest、HttpServletResponse 对象
 *
 * @author click33
 * @since 1.34.0
 */
public class SpringMVCUtil {
	
	private SpringMVCUtil() {
	}
	
	/**
	 * 获取当前会话的 request 
	 * @return request
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(servletRequestAttributes == null) {
			throw new NotWebContextException("非 web 上下文无法获取 HttpServletRequest").setCode(SaSpringBootErrorCode.CODE_20101);
		}
		return servletRequestAttributes.getRequest();
	}
	
	/**
	 * 获取当前会话的 response
	 * @return response
	 */
	public static HttpServletResponse getResponse() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(servletRequestAttributes == null) {
			throw new NotWebContextException("非 web 上下文无法获取 HttpServletRequest").setCode(SaSpringBootErrorCode.CODE_20101);
		}
		return servletRequestAttributes.getResponse();
	}

	/**
	 * 判断当前是否处于 Web 上下文中  
	 * @return request
	 */
	public static boolean isWeb() {
		return RequestContextHolder.getRequestAttributes() != null;
	}
	
}
