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
 * 一个异常：代表停止匹配，直接退出，向前端输出结果 （框架内部专属异常，一般情况下开发者无需关注）
 * 
 * @author click33
 * @since 1.21.0
 */
public class BackResultException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130143L;

	/**
	 * 要输出的结果 
	 */
	public Object result;
	
	/**
	 * 构造 
	 * @param result 要输出的结果 
	 */
	public BackResultException(Object result) {
		super(String.valueOf(result));
		this.result = result;
	}

}
