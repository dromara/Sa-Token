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
package cn.dev33.satoken.quick.web;

import cn.dev33.satoken.quick.SaQuickManager;
import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 登录Controller，处理登录相关请求
 *
 * @author click33
 * @since 1.19.0
 */
@Controller
public class SaQuickController {

	/**
	 * 进入登录页面
	 * @param model /
	 * @return /
	 */
	@GetMapping("/saLogin")
	public String saLogin(Model model) {
		model.addAttribute("cfg", SaQuickManager.getConfig());
		return "sa-login.html";
	}

	/**
	 * 登录接口
	 * @param name 账号
	 * @param pwd 密码
	 * @return 是否登录成功 
	 */
	@PostMapping("/doLogin")
	@ResponseBody
	public SaResult doLogin(@RequestParam("name") String name, @RequestParam("pwd") String pwd) {
		return SaQuickManager.getConfig().doLoginHandle.apply(name, pwd);
	}
	
}
