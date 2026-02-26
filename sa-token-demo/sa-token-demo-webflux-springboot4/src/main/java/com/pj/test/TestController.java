package com.pj.test;

import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 测试专用Controller 
 * @author click33
 *
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	@Autowired
	UserService userService;

	// 登录测试：Controller 里调用 Sa-Token API   --- http://localhost:8081/test/login
	@RequestMapping("login")
	public Mono<SaResult> login(@RequestParam(defaultValue="10001") String id) {
		return SaReactorHolder.sync(() -> {
			StpUtil.login(id);
			return SaResult.ok("登录成功");
		});
	}

	// API测试：手动设置上下文、try-finally 形式     	--- http://localhost:8081/test/isLogin
	@RequestMapping("isLogin")
	public SaResult isLogin(ServerWebExchange exchange) {
		try {
			SaReactorSyncHolder.setContext(exchange);
			System.out.println("是否登录：" + StpUtil.isLogin());
			return SaResult.data(StpUtil.getTokenInfo());
		} finally {
			SaReactorSyncHolder.clearContext();
		}
	}

	// API测试：手动设置上下文、lambda 表达式形式    	--- http://localhost:8081/test/isLogin2
	@RequestMapping("isLogin2")
	public SaResult isLogin2(ServerWebExchange exchange) {
		SaResult res = SaReactorSyncHolder.setContext(exchange, ()->{
			System.out.println("是否登录：" + StpUtil.isLogin());
			return SaResult.data(StpUtil.getTokenInfo());
		});
		return SaResult.data(res);
	}

	// API测试：自动设置上下文、lambda 表达式形式    	--- http://localhost:8081/test/isLogin3
	@RequestMapping("isLogin3")
	public Mono<SaResult> isLogin3() {
		return SaReactorHolder.sync(() -> {
			System.out.println("是否登录：" + StpUtil.isLogin());
			userService.isLogin();
			return SaResult.data(StpUtil.getTokenInfo());
		});
	}

	// API测试：自动设置上下文、调用 userService Mono 方法     	--- http://localhost:8081/test/isLogin4
	@RequestMapping("isLogin4")
	public Mono<SaResult> isLogin4() {
		return userService.findUserIdByNamePwd("ZhangSan", "123456").flatMap(userId -> {
			return SaReactorHolder.sync(() -> {
				StpUtil.login(userId);
				return SaResult.data(StpUtil.getTokenInfo());
			});
		});
	}

	// API测试：切换线程、复杂嵌套调用 	--- http://localhost:8081/test/isLogin5
	@RequestMapping("isLogin5")
	public Mono<SaResult> isLogin5() {
		System.out.println("线程id-----" + Thread.currentThread().getId());
		// 要点：在流里调用 Sa-Token API 之前，必须用 SaReactorHolder.sync( () -> {} ) 进行包裹
		return Mono.delay(Duration.ofSeconds(1))
				.doOnNext(r-> System.out.println("线程id-----" + Thread.currentThread().getId()))
				.map(r-> SaReactorHolder.sync( () -> userService.isLogin() ))
				.map(r-> userService.findUserIdByNamePwd("ZhangSan", "123456"))
				.map(r-> SaReactorHolder.sync( () -> userService.isLogin() ))
				.flatMap(isLogin -> {
					System.out.println("是否登录 " + isLogin);
					return SaReactorHolder.sync(() -> {
						System.out.println("是否登录 " + StpUtil.isLogin());
						return SaResult.data(StpUtil.getTokenInfo());
					});
				});
	}

	// API测试：使用上下文无关的API 	--- http://localhost:8081/test/isLogin6
	@RequestMapping("isLogin6")
	public SaResult isLogin6(@CookieValue("satoken") String satoken) {
		System.out.println("token 为：" + satoken);
		System.out.println("登录人：" + StpUtil.getLoginIdByToken(satoken));
		return SaResult.ok("登录人：" + StpUtil.getLoginIdByToken(satoken));
	}

	// 测试   浏览器访问： http://localhost:8081/test/test
	@RequestMapping("test")
	public SaResult test() {
		System.out.println("线程id------- " + Thread.currentThread().getId());
		return SaResult.ok();
	}

}
