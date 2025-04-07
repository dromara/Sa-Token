package com.pj.test;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 测试几种场景的异步场景 Controller
 *
 * @author click33
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	@Autowired
	public ThreadPoolTaskExecutor taskExecutor;

	// 【同步】登录  	---- http://localhost:8081/test/login?id=10001
	@RequestMapping("login")
	public SaResult login(@RequestParam(defaultValue = "10001") long id) {
		StpUtil.login(id);
		return SaResult.ok("登录成功");
	}

	// 【同步】判断是否登录  	--- http://localhost:8081/test/isLogin
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		System.out.println("是否登录：" + StpUtil.isLogin());
		return SaResult.data(StpUtil.getTokenValue());
	}

	// 【同步】注销   浏览器访问： http://localhost:8081/test/logout
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.logout();
		return SaResult.data(null);
	}

	// 【异步】new Thread  	--- http://localhost:8081/test/isLogin2
	@RequestMapping("isLogin2")
	public SaResult isLogin2() {
		System.out.println("是否登录：" + StpUtil.isLogin());
		String tokenValue = StpUtil.getTokenValue();
		new Thread(() -> {
			SaTokenContextMockUtil.setMockContext(()->{
				StpUtil.setTokenValueToStorage(tokenValue);
				System.out.println("是否登录：" + StpUtil.isLogin());
			});
		}).start();
		return SaResult.data(StpUtil.getTokenValue());
	}

	// 【异步】线程池 ThreadPoolTaskExecutor    	--- http://localhost:8081/test/isLogin3
	@RequestMapping("isLogin3")
	public SaResult isLogin3(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("是否登录：" + StpUtil.isLogin());
		String tokenValue = StpUtil.getTokenValue();
		taskExecutor.execute(() -> {
			SaTokenContextMockUtil.setMockContext(()->{
				StpUtil.setTokenValueToStorage(tokenValue);
				System.out.println("是否登录：" + StpUtil.isLogin());
			});
		});
		return SaResult.data(StpUtil.getTokenValue());
	}

	// 【异步】@Async    	--- http://localhost:8081/test/isLogin4
	@Async
	@RequestMapping("isLogin4")
	public SaResult isLogin4(@CookieValue("satoken") String satoken) {
		SaTokenContextMockUtil.setMockContext(()->{
			StpUtil.setTokenValueToStorage(satoken);
			System.out.println("是否登录：" + StpUtil.isLogin());
		});
		return SaResult.ok();
	}

	// 【异步】定时任务
	@Scheduled(cron = "0 * * * * ?")  // 一分钟执行一次
//	@Scheduled(cron = "0/10 * * * * ?")  // 十秒执行一次
	public void scheduledMethod(){
		// 错误写法：直接调用 Sa-Token API 会报错
		// System.out.println("定时任务，Mock 范围外：是否登录：" + StpUtil.isLogin());
		System.out.println(SaFoxUtil.formatDate(new Date()));

		// 需要先设置模拟上下文
		SaTokenContextMockUtil.setMockContext(() -> {
			// StpUtil.setTokenValueToStorage("f452571f-bfdb-413d-aba9-e26992cf07be");   // 模拟 Token
			System.out.println("定时任务，Mock 范围内：是否登录：" + StpUtil.isLogin());
			// 模拟登录
//			StpUtil.login(10066);   // 模拟 登录
//			System.out.println("定时任务，Mock 范围内：登录账号：" + StpUtil.getLoginId());
		});

	}

}

	/*
	 使用 InheritableThreadLocal 存储上下文带来的坑：

		@RequestMapping("isLogin2")
		public SaResult isLogin2() {
			System.out.println("是否登录：" + StpUtil.isLogin());

			new Thread(() -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				System.out.println("是否登录：" + StpUtil.isLogin());
			}).start();

			return SaResult.data(null);
		}

		如果不 Thread.sleep(1000)，外面 true，里面 true
		如果 Thread.sleep(1000)，则外面 true，里面false

		因为 SpringBoot 会在请求结束后清除 request 里的数据，
		此时子线程内部可以读取到 request，但是 request 无值，导致代码既能成功运行，又逻辑错误，是一种难以排查的隐形 bug
		应该避免使用 InheritableThreadLocal 来存储上下文数据

	 */
