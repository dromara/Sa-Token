package com.pj.test;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

import com.pj.util.AjaxJson;

import cn.dev33.satoken.stp.StpUtil;
import org.noear.solon.annotation.Param;

/**
 * 测试: 同域单点登录
 * @author click33
 * @author noear
 */
@Controller
@Mapping("/sso/")
public class SSOController {

	// 测试：进行登录
	@Mapping("doLogin")
	public AjaxJson doLogin(@Param(defaultValue = "10001") String id) {
		System.out.println("---------------- 进行登录 ");
		StpUtil.login(id);
		return AjaxJson.getSuccess("登录成功: " + id);
	}

	// 测试：是否登录
	@Mapping("isLogin")
	public AjaxJson isLogin() {
		System.out.println("---------------- 是否登录 ");
		boolean isLogin = StpUtil.isLogin();
		return AjaxJson.getSuccess("是否登录: " + isLogin);
	}

}
