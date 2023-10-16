package com.pj.test;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.spring.SpringMVCUtil;
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
		return SaResult.ok();
	}
	
	// 测试   浏览器访问： http://localhost:8081/test/test2
	@RequestMapping("test2")
	public SaResult test2() {
		return SaResult.ok();
	}

	// 测试   浏览器访问： http://localhost:8081/test/getRequestPath
	@RequestMapping("getRequestPath")
	public SaResult getRequestPath() {
		System.out.println("-------------- 测试请求 path 获取");
		System.out.println("request.getRequestURI() " + SpringMVCUtil.getRequest().getRequestURI());
		System.out.println("saRequest.getRequestPath() " + SaHolder.getRequest().getRequestPath());
		return SaResult.ok();
	}

}
