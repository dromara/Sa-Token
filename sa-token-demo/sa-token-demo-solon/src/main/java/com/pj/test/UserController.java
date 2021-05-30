package com.pj.test;

import cn.dev33.satoken.stp.StpUtil;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * 登录测试
 * @author kong
 * @author noear
 */
@Controller
@Mapping("/user/")
public class UserController {

	// 测试登录，浏览器访问： http://localhost:8081/user/doLogin?username=zhang&password=123456
	@Mapping("doLogin")
	public String doLogin(String username, String password) {
		// 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对 
		if("zhang".equals(username) && "123456".equals(password)) {
			StpUtil.setLoginId(10001);
			return "登录成功";
		}
		return "登录失败";
	}

	// 查询登录状态，浏览器访问： http://localhost:8081/user/isLogin
	@Mapping("isLogin")
	public String isLogin(String username, String password) {
		return "当前会话是否登录：" + StpUtil.isLogin();
	}
	
}
