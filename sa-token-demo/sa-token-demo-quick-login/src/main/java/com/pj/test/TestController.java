package com.pj.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 测试专用Controller 
 * @author kong
 *
 */
@RestController
public class TestController {

	// 浏览器访问测试： http://localhost:8081
	@RequestMapping({"/"})
	public String index() {
		String str = "<br />"
//				+ "<h1 style='text-align: center;'>Welcome to the system</h1>"
				+ "<h1 style='text-align: center;'>资源页 （登录后才可进入本页面） </h1>"
				+ "<hr/>"
				+ "<p style='text-align: center;'> Sa-Token " + SaTokenConsts.VERSION_NO + " </p>";
		return str;
	}
	
}
