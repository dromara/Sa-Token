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
package cn.dev33.satoken.integration.jfinal.httptest;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.jfinal.core.Controller;

/**
 * 集成测用的登录和鉴权接口。
 */
public class JfinalHttpController extends Controller {

	/** 首页，未登录也能看 */
	public void index() {
		renderText("index-ok");
	}

	/** 登录 10001（带超管角色）并回写 token */
	public void login() {
		StpUtil.login(10001);
		renderText(StpUtil.getTokenValue());
	}

	/** 登录 10002（没有角色），用来打角色校验失败 */
	public void loginPlain() {
		StpUtil.login(10002);
		renderText(StpUtil.getTokenValue());
	}

	/** 已登录才能看的业务接口 */
	@SaCheckLogin
	public void user() {
		renderText(String.valueOf(StpUtil.getLoginId()));
	}

	/** 公开接口 */
	public void open() {
		renderText("open");
	}

	/** 方法上的登录注解 */
	@SaCheckLogin
	public void anno() {
		renderText("anno-ok");
	}

	/** @SaIgnore 应该跳过注解鉴权 */
	@SaIgnore
	@SaCheckLogin
	public void ignored() {
		renderText("ignored");
	}

	/** 超管角色才能进 */
	@SaCheckRole("super-admin")
	public void role() {
		renderText("role-ok");
	}
}
