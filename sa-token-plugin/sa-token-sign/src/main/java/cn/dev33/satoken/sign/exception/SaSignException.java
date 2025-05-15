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
package cn.dev33.satoken.sign.exception;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 一个异常：代表 API 参数签名校验失败
 * 
 * @author click33
 * @since 1.34.0
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
	 * 断言 flag 不为 true，否则抛出 message 异常
	 * @param flag 表达式
	 * @param message 异常信息
	 */
	public static void notTrue(boolean flag, String message) {
		// notTrue
		if(flag) {
			throw new SaSignException(message);
		}
	}

	/**
	 * 断言 value 不为空，否则抛出 message 异常
	 * @param value 值
	 * @param message 异常信息
	 */
	public static void notEmpty(Object value, String message) {
		if(SaFoxUtil.isEmpty(value)) {
			throw new SaSignException(message);
		}
	}


	// ------------------- 已过期 -------------------

	/**
	 * 如果flag==true，则抛出message异常
	 * <h2>已过期：请使用 notTrue 代替，用法不变</h2>
	 *
	 * @param flag 标记
	 * @param message 异常信息
	 */
	@Deprecated
	public static void throwBy(boolean flag, String message) {
		if(flag) {
			throw new SaSignException(message);
		}
	}

	/**
	 * 如果 value isEmpty，则抛出 message 异常
	 * <h2>已过期：请使用 notEmpty 代替，用法不变</h2>
	 *
	 * @param value 值
	 * @param message 异常信息
	 */
	@Deprecated
	public static void throwByNull(Object value, String message) {
		if(SaFoxUtil.isEmpty(value)) {
			throw new SaSignException(message);
		}
	}


}
