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
package cn.dev33.satoken.integrate.configure;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 自定义权限验证接口扩展 
 * 
 * @author click33
 *
 */
@Component
public class StpInterfaceImpl implements StpInterface {

	/**
	 * 返回一个账号所拥有的权限码集合 
	 */
	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		int id = SaFoxUtil.getValueByType(loginId, int.class);
		if(id == 10001) {
			return Arrays.asList("user*", "art-add", "art-delete", "art-update", "art-get");
		} else {
			return null;
		}
	}

	/**
	 * 返回一个账号所拥有的角色标识集合 
	 */
	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		int id = SaFoxUtil.getValueByType(loginId, int.class);
		if(id == 10001) {
			return Arrays.asList("admin", "super-admin");
		} else {
			return null;
		}
	}

}
