package com.pj.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 测试专用Controller 
 * @author kong
 *
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	// 测试Sa-Token缓存， 浏览器访问： http://localhost:8081/test/login
	@RequestMapping("login")
	public AjaxJson login(@RequestParam(defaultValue="10001") String id) {
		System.out.println("--------------- 测试Sa-Token缓存");
		StpUtil.login(id);	
		return AjaxJson.getSuccess();
	}
	
	// 测试业务缓存   浏览器访问： http://localhost:8081/test/test
	@RequestMapping("test")
	public AjaxJson test() {
		System.out.println("--------------- 测试业务缓存");
		stringRedisTemplate.opsForValue().set("hello", "Hello World");
		return AjaxJson.getSuccess();
	}
	
}
