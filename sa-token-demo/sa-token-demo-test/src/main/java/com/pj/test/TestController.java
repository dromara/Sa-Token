package com.pj.test;

import cn.dev33.satoken.annotation.SaCheckHttpDigest;
import cn.dev33.satoken.annotation.SaCheckSign;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 测试专用Controller 
 * @author click33
 *
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	// 测试登录  ---- http://localhost:8081/test/login
	@RequestMapping("login")
	public SaResult login(@RequestParam(defaultValue = "10001") long id, String dt) {
		StpUtil.login(id, new SaLoginParameter()
				.setIsConcurrent(true)
				.setIsShare(false)
						.setDeviceType(dt)
				.setMaxLoginCount(4)
				.setMaxTryTimes(12)
				.setTerminalExtra("deviceSimpleTitle", "XiaoMi 15 Ultra")
				.setTerminalExtra("loginAddress", "浙江省杭州市西湖区")
				.setTerminalExtra("loginIp", "127.0.0.1")
				.setTerminalExtra("loginTime", SaFoxUtil.formatDate(new Date()))
		);
		StpUtil.getTokenSession();
		return SaResult.ok("登录成功");
	}

	// 测试   浏览器访问： http://localhost:8081/test/test
	@RequestMapping("test")
	public SaResult test() {
		System.out.println("------------进来了 " + SaFoxUtil.formatDate(new Date()));


		// 获取所有已登录的会话id
		List<String> sessionIdList = StpUtil.searchSessionId(null, 0, -1, false);

		for (String sessionId : sessionIdList) {

			// 根据会话id，查询对应的 SaSession 对象，此处一个 SaSession 对象即代表一个登录的账号
			SaSession session = StpUtil.getSessionBySessionId(sessionId);

			// 查询这个账号都在哪些设备登录了，依据上面的示例，账号A 的 SaTerminalInfo 数量是 3，账号B 的 SaTerminalInfo 数量是 2
			List<SaTerminalInfo> terminalList = session.terminalListCopy();
			System.out.println("会话id：" + sessionId + "，共在 " + terminalList.size() + " 设备登录");
		}


		// 返回
		return SaResult.data(null);
	}
	
	// 测试   浏览器访问： http://localhost:8081/test/test2
	@RequestMapping("test2")
	public SaResult test2() {
		return SaResult.ok();
	}

	// 测试   浏览器访问： http://localhost:8081/test/getRequestPath
	@RequestMapping("getRequestPath")
	public SaResult getRequestPath() {
		System.out.println("------------ 测试访问路径获取 ");
		System.out.println("SpringMVCUtil.getRequest().getRequestURI()  " + SpringMVCUtil.getRequest().getRequestURI());
		System.out.println("SaHolder.getRequest().getRequestPath()  " + SaHolder.getRequest().getRequestPath());
		return SaResult.ok();
	}

	// 测试 Http Digest 认证   浏览器访问： http://localhost:8081/test/testDigest
	@SaCheckHttpDigest("sa:123456")
	@RequestMapping("testDigest")
	public SaResult testDigest() {
		// SaHttpDigestUtil.check("sa", "123456");
		// 返回
		return SaResult.data(null);
	}

	// 测试注销   浏览器访问： http://localhost:8081/test/logout
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.logout();
		return SaResult.data(null);
	}

}
