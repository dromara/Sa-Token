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
package cn.dev33.satoken.router;

import java.util.HashMap;
import java.util.Map;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;

/**
 * Http 请求各种请求类型的枚举表示 
 * 
 * <p> 参考：Spring - HttpMethod 
 * 
 * @author click33
 * @since 1.27.0
 */
public enum SaHttpMethod {
	
	GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE, CONNECT, 
	
	/**
	 * 代表全部请求方式 
	 */
	ALL;
	
	private static final Map<String, SaHttpMethod> map = new HashMap<>();

	static {
		for (SaHttpMethod reqMethod : values()) {
			map.put(reqMethod.name(), reqMethod);
		}
	}

	/**
	 * String 转 enum 
	 * @param method 请求类型 
	 * @return SaHttpMethod 对象
	 */
	public static SaHttpMethod toEnum(String method) {
		if(method == null) {
			throw new SaTokenException("Method 不可以是 null").setCode(SaErrorCode.CODE_10321);
		}
		SaHttpMethod reqMethod = map.get(method.toUpperCase());
		if(reqMethod == null) {
			throw new SaTokenException("无效Method：" + method).setCode(SaErrorCode.CODE_10321);
		}
		return reqMethod;
	}

	/**
	 * String[] 转 enum[]
	 * @param methods 请求类型数组 
	 * @return SaHttpMethod 数组
	 */
	public static SaHttpMethod[] toEnumArray(String... methods) {
		SaHttpMethod [] arr = new SaHttpMethod[methods.length];
		for (int i = 0; i < methods.length; i++) {
			arr[i] = SaHttpMethod.toEnum(methods[i]);
		}
		return arr;
	}

}
