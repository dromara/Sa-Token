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

import cn.dev33.satoken.stp.StpUtil;

/**
 * 一个异常：代表会话未能通过角色认证校验
 * 
 * @author click33
 * @since 1.10.0
 */
public class NotRoleException extends SaTokenException {

	/**
	 * 序列化版本号 
	 */
	private static final long serialVersionUID = 8243974276159004739L;

	/** 角色标识 */
	private final String role;

	/**
	 * @return 获得角色标识
	 */
	public String getRole() {
		return role;
	}

	/**
	 * 账号类型
	 */
	private final String loginType;

	/**
	 * 获得账号类型
	 * 
	 * @return 账号类型
	 */
	public String getLoginType() {
		return loginType;
	}

	public NotRoleException(String role) {
		this(role, StpUtil.stpLogic.loginType);
	}

	public NotRoleException(String role, String loginType) {
		super("无此角色：" + role);
		this.role = role;
		this.loginType = loginType;
	}

}
