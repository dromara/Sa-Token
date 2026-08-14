package com.pj;

import cn.dev33.satoken.util.SaResult;
import com.pj.poc.JacksonDefaultTypingRcePoc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查与 PoC 手动触发。
 */
@RestController
public class HealthController {

	/** http://127.0.0.1:8093/ok */
	@RequestMapping("/ok")
	public SaResult ok() {
		return SaResult.ok("alive");
	}

	/** http://127.0.0.1:8093/poc/jackson-default-typing */
	@RequestMapping("/poc/jackson-default-typing")
	public SaResult jacksonDefaultTypingPoc() {
		return JacksonDefaultTypingRcePoc.run();
	}

}
