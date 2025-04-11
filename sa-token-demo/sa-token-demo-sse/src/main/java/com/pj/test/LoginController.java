package com.pj.test;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.pj.util.SseEmitterHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录测试 
 * @author click33
 *
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

	// 测试登录  ---- http://localhost:8081/acc/doLogin?uid=10001
	@RequestMapping("doLogin")
	public SaResult doLogin(@RequestParam(defaultValue = "10001") long uid) {
		StpUtil.login(uid);
		return SaResult.data(StpUtil.getTokenInfo());
	}

	// 查询登录状态  ---- http://localhost:8081/acc/isLogin
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		return SaResult.ok("是否登录：" + StpUtil.isLogin());
	}

	// 测试注销  ---- http://localhost:8081/acc/logout
	@RequestMapping("logout")
	public SaResult logout() {
		if(StpUtil.isLogin()) {
			long uid = StpUtil.getLoginIdAsLong();
			SseEmitterHolder.closeByUid(uid);
			StpUtil.logout();
		}
		return SaResult.ok();
	}
	
}
