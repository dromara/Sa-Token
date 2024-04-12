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
 * 一个异常：代表框架未能获取有效的上下文
 * <h1>已过期：请更名为 SaTokenContextException 用法不变，未来版本将彻底删除此类</h1>
 * 
 * @author click33
 * @since 1.33.0
 */
@Deprecated
public class InvalidContextException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;

	/**
	 * 一个异常：代表框架未能获取有效的上下文
	 * @param message 异常描述
	 */
	public InvalidContextException(String message) {
		super(message);
	}

}
