package com.pj.test;

import java.time.Duration;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pj.util.AjaxJson;

import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.stp.StpUtil;
import reactor.core.publisher.Mono;

/**
 * 测试专用Controller 
 * @author kong
 *
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	// 测试登录接口 [同步模式]， 浏览器访问： http://localhost:8081/test/login
	@RequestMapping("login")
	public AjaxJson login(@RequestParam(defaultValue="10001") String id) {
		StpUtil.login(id);			
		return AjaxJson.getSuccess("登录成功");
	}
	
	// API测试 [同步模式]， 浏览器访问： http://localhost:8081/test/isLogin
	@RequestMapping("isLogin")
	public AjaxJson isLogin() {
		System.out.println("当前会话是否登录：" + StpUtil.isLogin());
		return AjaxJson.getSuccessData(StpUtil.getTokenInfo());
	}

	// API测试 [异步模式]， 浏览器访问： http://localhost:8081/test/isLogin2
	@RequestMapping("isLogin2")
	public Mono<AjaxJson> isLogin2() {
		System.out.println("当前会话是否登录：" + StpUtil.isLogin());
		AjaxJson aj = AjaxJson.getSuccessData(StpUtil.getTokenInfo());
		return Mono.just(aj);
	}

	// API测试 [异步模式, 同一线程]， 浏览器访问： http://localhost:8081/test/isLogin3
	@RequestMapping("isLogin3")
	public Mono<AjaxJson> isLogin3() {
		System.out.println("当前会话是否登录：" + StpUtil.isLogin());
		// 异步方式 
		return SaReactorHolder.getContext().map(e -> {
			System.out.println("当前会话是否登录2：" + StpUtil.isLogin());
			return AjaxJson.getSuccessData(StpUtil.getTokenInfo());
		});
	}

	// API测试 [异步模式, 不同线程]， 浏览器访问： http://localhost:8081/test/isLogin4
	@RequestMapping("isLogin4")
	public Mono<AjaxJson> isLogin4() {
		System.out.println("当前会话是否登录：" + StpUtil.isLogin());
		System.out.println("线程id-----" + Thread.currentThread().getId());
		return Mono.delay(Duration.ofSeconds(1)).flatMap(r->{
			return SaReactorHolder.getContext().map(rr->{
				System.out.println("线程id---内--" + Thread.currentThread().getId());
				System.out.println("当前会话是否登录2：" + StpUtil.isLogin());
				return AjaxJson.getSuccessData(StpUtil.getTokenInfo());
			});
		});
	}
	
	// 测试   浏览器访问： http://localhost:8081/test/test
	@RequestMapping("test")
	public AjaxJson test() {
		System.out.println("线程id-----------Controller--" + Thread.currentThread().getId() + "\t\t");
		System.out.println("当前会话是否登录：" + StpUtil.isLogin());
		return AjaxJson.getSuccessData(StpUtil.getTokenInfo());
	}
	

}
