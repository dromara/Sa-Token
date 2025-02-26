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
 * 一个异常：代表防火墙检验未通过
 * 
 * @author click33
 * @since 1.41.0
 */
public class FirewallCheckException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 8243974276159004739L;

	public FirewallCheckException(String message) {
		super(message);
	}

	public FirewallCheckException(Throwable e) {
		super(e);
	}

	public FirewallCheckException(String message, Throwable e) {
		super(message, e);
	}

}
