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

import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 一个异常：代表 API 参数签名校验失败
 * 
 * @author click33
 * @since 2023-5-3
 */
public class SaSignException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;

	/**
	 * 一个异常：代表 API 参数签名校验失败
	 * @param message 异常描述
	 */
	public SaSignException(String message) {
		super(message);
	}

	/**
	 * 如果flag==true，则抛出message异常
	 * @param flag 标记
	 * @param message 异常信息
	 */
	public static void throwBy(boolean flag, String message) {
		if(flag) {
			throw new SaSignException(message);
		}
	}

	/**
	 * 如果 value isEmpty，则抛出 message 异常
	 * @param value 值
	 * @param message 异常信息
	 */
	public static void throwByNull(Object value, String message) {
		if(SaFoxUtil.isEmpty(value)) {
			throw new SaSignException(message);
		}
	}

}
