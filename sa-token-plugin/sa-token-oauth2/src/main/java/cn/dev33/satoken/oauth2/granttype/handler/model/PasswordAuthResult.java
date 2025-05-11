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
package cn.dev33.satoken.oauth2.granttype.handler.model;

import java.io.Serializable;

/**
 * Model: Password Grant_Type 认证结果
 *
 * @author click33
 * @since 1.43.0
 */
public class PasswordAuthResult implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * 对应账号id
	 */
	public Object loginId;

	/**
	 * 构建一个
	 */
	public PasswordAuthResult() {

	}
	/**
	 * 构建一个
	 * @param loginId 对应的账号id
	 */
	public PasswordAuthResult(Object loginId) {
		this();
		this.loginId = loginId;
	}

	/**
	 * 获取 对应账号id
	 * @return /
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * 设置 对应账号id
	 * @param loginId 对应账号id
	 * @return 对象自身
	 */
	public PasswordAuthResult setLoginId(Object loginId) {
		this.loginId = loginId;
		return this;
	}

	@Override
	public String toString() {
		return "PasswordAuthResult{" +
				", loginId=" + loginId +
				'}';
	}

}
