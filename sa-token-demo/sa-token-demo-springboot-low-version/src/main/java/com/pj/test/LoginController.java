package com.pj.test;

import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 登录测试
 * @author click33
 *
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

	// 测试登录  ---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
	@RequestMapping("doLogin")
	public SaResult doLogin(String name, String pwd) {
		// 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
		if("zhang".equals(name) && "123456".equals(pwd)) {
			StpUtil.login(10001);
			StpUtil.getTokenSession();
			return SaResult.ok("登录成功");
		}
		return SaResult.error("登录失败");
	}

	// 查询登录状态  ---- http://localhost:8081/acc/isLogin
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		return SaResult.ok("是否登录：" + StpUtil.isLogin());
	}

	// 校验登录  ---- http://localhost:8081/acc/checkLogin
	@RequestMapping("checkLogin")
	public SaResult checkLogin() {
		StpUtil.checkLogin();
		return SaResult.ok();
	}

	// 查询 Token 信息  ---- http://localhost:8081/acc/tokenInfo
	@RequestMapping("tokenInfo")
	public SaResult tokenInfo() {
		return SaResult.data(StpUtil.getTokenInfo());
	}

	// 查询账号登录设备信息  ---- http://localhost:8081/acc/terminalInfo
	@RequestMapping("terminalInfo")
	public SaResult terminalInfo() {
		System.out.println("账号 10001 登录设备信息：");
		List<SaTerminalInfo> terminalList = StpUtil.getTerminalListByLoginId(10001);
		for (SaTerminalInfo ter : terminalList) {
			System.out.println("登录index=" + ter.getIndex() + ", 设备type=" + ter.getDeviceType() + ", token=" + ter.getTokenValue() + ", 登录time=" + ter.getCreateTime());
		}
		return SaResult.data(terminalList);
	}


	// 测试注销  ---- http://localhost:8081/acc/logout
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.login(10001, SaLoginParameter.create().setIsConcurrent(false));
		return SaResult.ok();
	}

}
