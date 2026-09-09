package com.pj.test;

import com.jfinal.core.Controller;
import com.jfinal.core.Path;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 登录测试
 * @author click33
 *
 */
@Path("/acc")
public class LoginController extends Controller {

	// 测试登录  ---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
	public void doLogin() {
		String name = getPara("name");
		String pwd = getPara("pwd");
		// 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
		if("zhang".equals(name) && "123456".equals(pwd)) {
			StpUtil.login(10001);
			renderJson(SaResult.ok("登录成功"));
			return;
		}
		renderJson(SaResult.error("登录失败"));
	}

	// 查询登录状态  ---- http://localhost:8081/acc/isLogin
	public void isLogin() {
		renderJson(SaResult.ok("是否登录：" + StpUtil.isLogin()));
	}

	// 查询 Token 信息  ---- http://localhost:8081/acc/tokenInfo
	public void tokenInfo() {
		renderJson(SaResult.data(StpUtil.getTokenInfo()));
	}

	// 测试注销  ---- http://localhost:8081/acc/logout
	public void logout() {
		StpUtil.logout();
		renderJson(SaResult.ok());
	}

}
