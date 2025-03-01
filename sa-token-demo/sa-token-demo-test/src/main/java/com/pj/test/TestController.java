package com.pj.test;

import cn.dev33.satoken.annotation.SaCheckHttpDigest;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.stp.SaLoginParameter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import com.pj.model.SysUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 测试专用Controller 
 * @author click33
 *
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	// 测试登录  ---- http://localhost:8081/test/login
	@RequestMapping("login")
	public SaResult login(@RequestParam(defaultValue = "10001") long id) {
		StpUtil.login(id, new SaLoginParameter()
				.setIsConcurrent(true)
				.setIsShare(false)
				.setMaxLoginCount(4)
				.setMaxTryTimes(12)
		);
		return SaResult.ok("登录成功");
	}

	// 测试   浏览器访问： http://localhost:8081/test/test
	@RequestMapping("test")
	public SaResult test() {
		System.out.println("------------进来了 " + SaFoxUtil.formatDate(new Date()));
//		StpUtil.getLoginId();
//		StpUtil.getAnonTokenSession();
//		StpUtil.setTokenValue("xxx");
		StpUtil.getSession().set("name", "zhang");
		StpUtil.getSession().set("age", 18);
		SysUser user = new SysUser(10001, "lisi", 22);
		StpUtil.getSession().set("user", user);
		StpUtil.getTokenSession().set("user", user);

		// 返回
		return SaResult.data(null);
	}
	
	// 测试   浏览器访问： http://localhost:8081/test/test2
	@RequestMapping("test2")
	public SaResult test2() {
		return SaResult.ok();
	}

	// 测试   浏览器访问： http://localhost:8081/test/getRequestPath
	@RequestMapping("getRequestPath")
	public SaResult getRequestPath() {
		System.out.println("------------ 测试访问路径获取 ");
		System.out.println("SpringMVCUtil.getRequest().getRequestURI()  " + SpringMVCUtil.getRequest().getRequestURI());
		System.out.println("SaHolder.getRequest().getRequestPath()  " + SaHolder.getRequest().getRequestPath());
		return SaResult.ok();
	}

	// 测试 Http Digest 认证   浏览器访问： http://localhost:8081/test/testDigest
	@SaCheckHttpDigest("sa:123456")
	@RequestMapping("testDigest")
	public SaResult testDigest() {
		// SaHttpDigestUtil.check("sa", "123456");
		// 返回
		return SaResult.data(null);
	}

}
