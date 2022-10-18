package com.pj.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pj.util.AjaxJson;

/**
 * 测试专用Controller 
 * @author kong
 *
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	// 测试   浏览器访问： http://localhost:8081/test/test
	@RequestMapping("test")
	public AjaxJson test() {
		System.out.println("------------进来了"); 
		return AjaxJson.getSuccess(); 
	}
	
	// 测试   浏览器访问： http://localhost:8081/test/test2
	@RequestMapping("test2")
	public AjaxJson test2() {
		return AjaxJson.getSuccess();
	}

}
