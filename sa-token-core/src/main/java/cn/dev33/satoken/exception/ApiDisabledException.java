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
package cn.dev33.satoken.exception;

/**
 * 一个异常：代表 API 已被禁用
 *
 * <p> 一般在 API 不合适调用的时候抛出，例如在集成 jwt 模块后调用数据持久化相关方法 </p>
 *
 * @author click33
 * @since <= 1.34.0
 */
public class ApiDisabledException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130133L;
	
	/** 异常提示语 */
	public static final String BE_MESSAGE = "this api is disabled";

	/**
	 * 一个异常：代表 API 已被禁用  
	 */
	public ApiDisabledException() {
		super(BE_MESSAGE);
	}

	/**
	 * 一个异常：代表 API 已被禁用  
	 * @param message 异常描述 
	 */
	public ApiDisabledException(String message) {
		super(message);
	}
}
