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

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;

/**
 * 测试注解用的Controller 
 * 
 * @author click33
 * @since 2022-9-2
 */
@SaCheckLogin
@RestController
@RequestMapping("/ig/")
public class SaAnnotationIgnoreController {

	// 需要登录后访问  
	@RequestMapping("show1")
	public SaResult show1() {
		return SaResult.ok();
	}

	// 不登录也可访问  
	@SaIgnore
	@RequestMapping("show2")
	public SaResult show2() {
		return SaResult.ok();
	}

}
