package com.pj.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.StpUtil;

/**
 * 测试专用Controller 
 * @author kong
 *
 */
@RestController
@RequestMapping("/test/")
public class TestController {

	
	// 当前是否登录 ， 浏览器访问： http://localhost:8081/test/isLogin
	@RequestMapping("isLogin")
	public AjaxJson isLogin() {
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号id：" + StpUtil.getLoginId(-1));
		return AjaxJson.getSuccessData(StpUtil.getTokenInfo());
	}
	
	
	// 测试登录接口， 浏览器访问： http://localhost:8081/test/login
	@RequestMapping("login")
	public AjaxJson login(@RequestParam(defaultValue="10001") String id) {
		System.out.println("======================= 进入方法，测试登录接口 ========================= ");
		System.out.println("当前会话的token：" + StpUtil.getTokenValue());
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号：" + StpUtil.getLoginIdDefaultNull());
		StpUtil.setLoginId(id);			// 在当前会话登录此账号 	
		System.out.println("登录成功");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号：" + StpUtil.getLoginId());
		System.out.println("当前登录账号：" + StpUtil.getLoginIdAsInt());	// 获取登录id并转为int
		
//		StpUtil.logout();
//		System.out.println("注销登录");
//		System.out.println("当前是否登录：" + StpUtil.isLogin());
//		System.out.println("当前登录账号：" + StpUtil.getLoginIdDefaultNull());
//		StpUtil.setLoginId(id);			// 在当前会话登录此账号 	
//		System.out.println("根据token找登录id：" + StpUtil.getLoginIdByToken(StpUtil.getTokenValue()));
		
		System.out.println("当前token信息：" + StpUtil.getTokenInfo());	// 获取登录id并转为int 
		System.out.println("当前登录账号：" + StpUtil.getLoginIdDefaultNull());
		
		return AjaxJson.getSuccess();
	}
	
	// 测试权限接口， 浏览器访问： http://localhost:8081/test/jur
	@RequestMapping("jur")
	public AjaxJson jur() {
		System.out.println("======================= 进入方法，测试权限接口 ========================= ");
		
		System.out.println("是否具有权限101" + StpUtil.hasPermission(101));
		System.out.println("是否具有权限user-add" + StpUtil.hasPermission("user-add"));
		System.out.println("是否具有权限article-get" + StpUtil.hasPermission("article-get"));
		
		System.out.println("没有user-add权限就抛出异常");
		StpUtil.checkPermission("user-add");
		
		System.out.println("在【101、102】中只要拥有一个就不会抛出异常");
		StpUtil.checkPermissionOr("101", "102");

		System.out.println("在【101、102】中必须全部拥有才不会抛出异常");
		StpUtil.checkPermissionAnd("101", "102");

		System.out.println("权限测试通过");
		
		return AjaxJson.getSuccess();
	}

	// 测试会话session接口， 浏览器访问： http://localhost:8081/test/session 
	@RequestMapping("session")
	public AjaxJson session() {
		System.out.println("======================= 进入方法，测试会话session接口 ========================= ");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号session的id" + StpUtil.getSession().getId());
		System.out.println("当前登录账号session的id" + StpUtil.getSession().getId());
		System.out.println("测试取值name：" + StpUtil.getSession().getAttribute("name"));
		StpUtil.getSession().setAttribute("name", "张三");	// 写入一个值 
		System.out.println("测试取值name：" + StpUtil.getSession().getAttribute("name"));
		return AjaxJson.getSuccess();
	}
	
	// 测试自定义session接口， 浏览器访问： http://localhost:8081/test/session2 
	@RequestMapping("session2")
	public AjaxJson session2() {
		System.out.println("======================= 进入方法，测试自定义session接口 ========================= ");
		// 自定义session就是无需登录也可以使用 的session ：比如拿用户的手机号当做 key， 来获取 session 
		System.out.println("自定义 session的id为：" + SaSessionCustomUtil.getSessionById("1895544896").getId());
		System.out.println("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").getAttribute("name"));
		SaSessionCustomUtil.getSessionById("1895544896").setAttribute("name", "张三");	// 写入值 
		System.out.println("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").getAttribute("name"));
		System.out.println("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").getAttribute("name"));
		return AjaxJson.getSuccess();
	}

	// 打印当前token信息， 浏览器访问： http://localhost:8081/test/tokenInfo
	@RequestMapping("tokenInfo")
	public AjaxJson tokenInfo() {
		System.out.println("======================= 进入方法，打印当前token信息 ========================= ");
		System.out.println(StpUtil.getTokenInfo());
		return AjaxJson.getSuccess();
	}
	
	
	// 测试注解式鉴权， 浏览器访问： http://localhost:8081/test/atCheck
	@SaCheckLogin						// 注解式鉴权：当前会话必须登录才能通过 
	@SaCheckPermission("user-add")		// 注解式鉴权：当前会话必须具有指定权限才能通过 
	@RequestMapping("atCheck")
	public AjaxJson atCheck() {
		System.out.println("======================= 进入方法，测试注解鉴权接口 ========================= ");
		System.out.println("只有通过注解鉴权，才能进入此方法");
		return AjaxJson.getSuccess();
	}
	
	// 测试注解式鉴权， 浏览器访问： http://localhost:8081/test/getInfo
	@SaCheckLogin				// 注解式鉴权：当前会话必须登录才能通过 
	@RequestMapping("getInfo")
	public AjaxJson getInfo() {
		return AjaxJson.getSuccessData("用户信息");
	}
	
	


	// 测试踢人下线   浏览器访问： http://localhost:8081/test/kickOut 
	@RequestMapping("kickOut")
	public AjaxJson kickOut() {
		// 先登录上 
		StpUtil.setLoginId(10001);
		// 清退下线 
//		StpUtil.logoutByLoginId(10001);
		// 踢下线 
		StpUtil.kickoutByLoginId(10001);
		// 再尝试获取
		StpUtil.getLoginId();
		// 返回 
		return AjaxJson.getSuccess();
	}
	

	// 测试   浏览器访问： http://localhost:8081/test/test 
	@RequestMapping("test")
	public AjaxJson test() {
		StpUtil.setLoginId(10001);
//		StpUtil.getSession();
		StpUtil.logout();
		
//		System.out.println(StpUtil.getSession().getId());
//		System.out.println(StpUserUtil.getSession().getId());
//		StpUtil.getSessionByLoginId(10001).setAttribute("name", "123");
//		System.out.println(StpUtil.getSessionByLoginId(10001).getAttribute("name"));
		
		return AjaxJson.getSuccess();
	}

	
	
	
}
