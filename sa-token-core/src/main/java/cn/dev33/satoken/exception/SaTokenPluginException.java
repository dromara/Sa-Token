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
 * 一个异常：代表插件安装过程中出现异常
 *
 * @author click33
 * @since 1.28.0
 */
public class SaTokenPluginException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130131L;

	/**
	 * 一个异常：代表插件安装过程中出现异常
	 * @param message 异常描述
	 */
	public SaTokenPluginException(String message) {
		super(message);
	}

	/**
	 * 一个异常：代表插件安装过程中出现异常
	 *
	 * @param cause 异常对象
	 */
	public SaTokenPluginException(Throwable cause) {
		super(cause);
	}

	/**
	 * 一个异常：代表插件安装过程中出现异常
	 *
	 * @param message 异常描述
	 * @param cause 异常对象
	 */
	public SaTokenPluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
