package com.pj.test;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 当前复现用例的入口（随 Issue 更换可改这里）
 */
@RestController
public class TestController {

	/** 健康检查（过滤器放行） --- http://localhost:8092/ok */
	@RequestMapping("/ok")
	public SaResult ok() {
		return SaResult.ok("alive");
	}

	/**
	 * 用例 A：直接调 setStatus
	 * --- http://localhost:8092/repro/direct-set-status
	 */
	@RequestMapping("/repro/direct-set-status")
	public Mono<SaResult> directSetStatus() {
		return SaReactorHolder.sync(() -> {
			SaHolder.getResponse().setStatus(401);
			return SaResult.ok("setStatus 已执行");
		});
	}

	/**
	 * 用例 B：Http Basic 校验失败内部会 setStatus(401)
	 * --- curl -i http://localhost:8092/basic/ping
	 */
	@RequestMapping("/basic/ping")
	public SaResult basicPing() {
		return SaResult.ok("basic ok");
	}

}
