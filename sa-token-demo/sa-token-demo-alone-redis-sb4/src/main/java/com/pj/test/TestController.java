package com.pj.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 测试专用Controller，演示 alone-redis 权限缓存与业务缓存分离
 *
 * @author click33
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	// 测试Sa-Token缓存，浏览器访问： http://localhost:8083/test/login
	@RequestMapping("login")
	public SaResult login(@RequestParam(defaultValue = "10001") String id) {
		System.out.println("--------------- 测试Sa-Token缓存");
		StpUtil.login(id);
		return SaResult.ok();
	}

	// 测试业务缓存，浏览器访问： http://localhost:8083/test/test
	@RequestMapping("test")
	public SaResult test() {
		System.out.println("--------------- 测试业务缓存");
		stringRedisTemplate.opsForValue().set("hello", "Hello World");
		return SaResult.ok();
	}

}
