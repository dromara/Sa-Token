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

import cn.dev33.satoken.annotation.SaCheckPermission;

/**
 * 用户项目里那种业务接口：鉴权注解直接写在方法上，给策略扫描当夹具。
 *
 * @author click33
 * @since 1.46.0
 */
public class UserAuthApi {

	/** 用户自己拼的组合注解 */
	@RequireUserAdd
	public void addUser() {
	}

	/** 直接写内置注解，行为应该和 {@link #addUser()} 一样 */
	@SaCheckPermission("user:add")
	public void addUserBuiltin() {
	}

	/** 没有的权限，扫描时应该失败 */
	@SaCheckPermission("user:delete")
	public void deleteUser() {
	}

}
