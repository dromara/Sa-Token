package com.pj.test;

import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 当前复现用例的入口（随 Issue 更换可改这里）
 */
@RestController
public class TestController {

	/** 健康检查 --- http://localhost:8092/ok */
	@RequestMapping("/ok")
	public SaResult ok() {
		return SaResult.ok("alive");
	}

}
