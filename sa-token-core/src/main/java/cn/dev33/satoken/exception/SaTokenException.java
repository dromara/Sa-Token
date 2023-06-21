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

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token 框架内部逻辑发生错误抛出的异常
 *
 * <p> 框架其它异常均继承自此类，开发者可通过捕获此异常来捕获框架内部抛出的所有异常 </p>
 * 
 * @author click33
 * @since 1.10.0
 */
public class SaTokenException extends RuntimeException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130132L;

	/**
	 * 异常细分状态码 
	 */
	private int code = SaErrorCode.CODE_UNDEFINED;

	/**
	 * 构建一个异常
	 * 
	 * @param code 异常细分状态码 
	 */
	public SaTokenException(int code) {
		super();
		this.code = code;
	}


	/**
	 * 构建一个异常
	 * 
	 * @param message 异常描述信息
	 */
	public SaTokenException(String message) {
		super(message);
	}

	/**
	 * 构建一个异常
	 * 
	 * @param code 异常细分状态码 
	 * @param message 异常信息
	 */
	public SaTokenException(int code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * 构建一个异常
	 * 
	 * @param cause 异常对象
	 */
	public SaTokenException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构建一个异常
	 * 
	 * @param message 异常信息
	 * @param cause 异常对象
	 */
	public SaTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 获取异常细分状态码
	 * @return 异常细分状态码
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 写入异常细分状态码 
	 * @param code 异常细分状态码
	 * @return 对象自身 
	 */
	public SaTokenException setCode(int code) {
		this.code = code;
		return this;
	}
	
	/**
	 * 如果flag==true，则抛出message异常 
	 * @param flag 标记
	 * @param message 异常信息 
	 * @param code 异常细分状态码 
	 */
	public static void throwBy(boolean flag, String message, int code) {
		if(flag) {
			throw new SaTokenException(message).setCode(code);
		}
	}

	/**
	 * 如果value==null或者isEmpty，则抛出message异常 
	 * @param value 值 
	 * @param message 异常信息 
	 * @param code 异常细分状态码 
	 */
	public static void throwByNull(Object value, String message, int code) {
		if(SaFoxUtil.isEmpty(value)) {
			throw new SaTokenException(message).setCode(code);
		}
	}
	
}
