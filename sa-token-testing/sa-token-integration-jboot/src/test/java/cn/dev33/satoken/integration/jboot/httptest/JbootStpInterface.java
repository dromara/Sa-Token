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
package cn.dev33.satoken.integration.jboot.httptest;

import cn.dev33.satoken.stp.StpInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 集成测用的权限数据：10001 带超管角色，其它账号没有角色。
 */
public class JbootStpInterface implements StpInterface {

	/** 这个集成测不验权限码 */
	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		return Collections.emptyList();
	}

	/** 10001 当成超管，用来打 @SaCheckRole */
	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		if ("10001".equals(String.valueOf(loginId))) {
			List<String> roles = new ArrayList<String>();
			roles.add("admin");
			roles.add("super-admin");
			return roles;
		}
		return Collections.emptyList();
	}
}
