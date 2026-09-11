/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.test.fixture;

import cn.dev33.satoken.stp.StpInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户项目里那种权限数据源：固定权限/角色，并记下最近一次被问到的账号。
 *
 * @author click33
 * @since 1.46.0
 */
public class RecordingStpInterface implements StpInterface {

	private final List<String> permissions;
	private final List<String> roles;

	/** 最近一次 getPermissionList 问到的 loginId */
	public Object lastPermissionLoginId;

	/** 最近一次 getRoleList 问到的 loginId */
	public Object lastRoleLoginId;

	/** 按用户项目写法准备一份权限和角色 */
	public RecordingStpInterface(List<String> permissions, List<String> roles) {
		this.permissions = new ArrayList<>(permissions);
		this.roles = new ArrayList<>(roles);
	}

	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		lastPermissionLoginId = loginId;
		return permissions;
	}

	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		lastRoleLoginId = loginId;
		return roles;
	}

}
