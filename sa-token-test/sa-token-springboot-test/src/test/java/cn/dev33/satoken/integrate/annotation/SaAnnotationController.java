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
package cn.dev33.satoken.integrate.annotation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 测试注解用的Controller 
 * 
 * @author click33
 * @since 2022-9-2
 */
@RestController
@RequestMapping("/at/")
public class SaAnnotationController {

	// 登录 
	@RequestMapping("login")
	public SaResult login(long id) {
		StpUtil.login(id);
		return SaResult.ok().set("token", StpUtil.getTokenValue());
	}

	// 登录校验 
	@SaCheckLogin
	@RequestMapping("checkLogin")
	public SaResult checkLogin() {
		return SaResult.ok();
	}

	// 角色校验 
	@SaCheckRole("admin")
	@RequestMapping("checkRole")
	public SaResult checkRole() {
		return SaResult.ok();
	}

	// 权限校验 
	@SaCheckPermission("art-add")
	@RequestMapping("checkPermission")
	public SaResult checkPermission() {
		return SaResult.ok();
	}

	// 权限校验 or 角色校验 
	@SaCheckPermission(value = "art-add2", orRole = "admin")
	@RequestMapping("checkPermission2")
	public SaResult checkPermission2() {
		return SaResult.ok();
	}

	// 开启二级认证 
	@RequestMapping("openSafe")
	public SaResult openSafe() {
		StpUtil.openSafe(120);
		return SaResult.ok();
	}

	// 二级认证校验
	@SaCheckSafe
	@RequestMapping("checkSafe")
	public SaResult checkSafe() {
		return SaResult.ok();
	}

	// 封禁账号 
	@RequestMapping("disable")
	public SaResult disable(long id) {
		StpUtil.disable(id, "comment", 200);
		return SaResult.ok();
	}

	// 服务封禁校验 
	@SaCheckDisable("comment")
	@RequestMapping("checkDisable")
	public SaResult checkDisable() {
		return SaResult.ok();
	}

	// 解封账号 
	@RequestMapping("untieDisable")
	public SaResult untieDisable(long id) {
		StpUtil.untieDisable(id, "comment");
		return SaResult.ok();
	}

}
