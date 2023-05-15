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
 * 一个异常：代表会话未能通过权限认证校验
 * 
 * @author click33
 * @since <= 1.34.0
 */
public class NotPermissionException extends SaTokenException {

	/**
	 * 序列化版本号 
	 */
	private static final long serialVersionUID = 6806129545290130141L;

	/** 权限码 */
	private String permission;

	/**
	 * @return 获得具体缺少的权限码
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * 账号类型
	 */
	private String loginType;

	/**
	 * 获得账号类型
	 * 
	 * @return 账号类型
	 */
	public String getLoginType() {
		return loginType;
	}

	public NotPermissionException(String permission) {
		this(permission, StpUtil.stpLogic.loginType);
	}

	public NotPermissionException(String permission, String loginType) {
		super("无此权限：" + permission);
		this.permission = permission;
		this.loginType = loginType;
	}

	/**
	 * <h1> 警告：自 v1.30+ 版本起，获取异常权限码由 getCode() 更改为 getPermission()，请及时更换！ </h1> 
	 * @return 获得权限码
	 */
	@Deprecated
	public int getCode() {
		return super.getCode();
	}

}
