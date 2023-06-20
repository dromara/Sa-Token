package com.pj.test;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试专用Controller 
 * @author click33
 *
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	// 测试   浏览器访问： http://localhost:8081/test/test
	@RequestMapping("test")
	public SaResult test() {
		System.out.println("------------进来了");
		StpUtil.login(10001);
		return SaResult.ok();
	}
	
	// 测试   浏览器访问： http://localhost:8081/test/test2
	@RequestMapping("test2")
	public SaResult test2() {
		StpUtil.checkLogin();
		return SaResult.ok();
	}

}
