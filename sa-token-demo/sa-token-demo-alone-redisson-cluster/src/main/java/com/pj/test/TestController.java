package com.pj.test;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试专用Controller，演示 alone-redisson 集群模式下权限缓存与业务缓存分离
 *
 * @author click33
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	@Autowired
	RedissonClient redissonClient;

	// 测试 Sa-Token 缓存，浏览器访问： http://localhost:8085/test/login
	@RequestMapping("login")
	public SaResult login(@RequestParam(defaultValue = "10001") String id) {
		System.out.println("--------------- 测试Sa-Token缓存");
		StpUtil.login(id);
		return SaResult.ok();
	}

	// 测试业务缓存，浏览器访问： http://localhost:8085/test/test
	@RequestMapping("test")
	public SaResult test() {
		System.out.println("--------------- 测试业务缓存");
		redissonClient.getBucket("hello").set("Hello World");
		return SaResult.ok();
	}

}
