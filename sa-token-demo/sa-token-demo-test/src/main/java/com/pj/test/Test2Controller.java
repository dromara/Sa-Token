package com.pj.test;

import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试专用Controller 
 * @author click33
 *
 */
@RestController
public class Test2Controller {

	// 测试登录  ---- http://localhost:8081/.test
	@RequestMapping("/.test")
	public SaResult test2() {
		System.out.println("--- 进来了");
		return SaResult.ok("登录成功");
	}

}
