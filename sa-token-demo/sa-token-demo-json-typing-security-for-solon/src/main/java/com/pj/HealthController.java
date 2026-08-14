package com.pj;

import cn.dev33.satoken.util.SaResult;
import com.pj.poc.Snack4AutoTypeRcePoc;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * 健康检查与 PoC 手动触发。
 */
@Controller
public class HealthController {

	/** http://127.0.0.1:8094/ok */
	@Mapping("/ok")
	public SaResult ok() {
		return SaResult.ok("alive");
	}

	/** http://127.0.0.1:8094/poc/snack4-autotype */
	@Mapping("/poc/snack4-autotype")
	public SaResult snack4AutoTypePoc() {
		return Snack4AutoTypeRcePoc.run();
	}

}
