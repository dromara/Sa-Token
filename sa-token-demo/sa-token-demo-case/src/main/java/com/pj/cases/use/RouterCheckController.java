package com.pj.cases.use;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.util.SaResult;

/**
 * 为路由拦截鉴权准备的路示例 
 * 
 * @author kong
 * @since 2022-10-15
 */
@RestController
public class RouterCheckController {

	// 路由拦截鉴权测试   ---- http://localhost:8081/xxx
	@RequestMapping({
		"/user/doLogin", "/user/doLogin2",
		"/user/info", "/admin/info", "/goods/info", "/orders/info", "/notice/info", "/comment/info",
		"/router/print", "/router/print2"
	})
	public SaResult checkLogin() {
		return SaResult.ok();
	}

}
