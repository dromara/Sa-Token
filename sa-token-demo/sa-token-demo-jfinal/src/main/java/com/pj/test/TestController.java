package com.pj.test;

import java.util.Date;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.core.Path;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 测试专用Controller
 * @author click33
 *
 */
@Path("/test")
public class TestController extends Controller {

	// 测试登录接口， 浏览器访问： http://localhost:8081/test/login
	public void login() {
		String id = getPara("id", "10001");
		System.out.println("======================= 进入方法，测试登录接口 ========================= ");
		System.out.println("当前会话的token：" + StpUtil.getTokenValue());
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号：" + StpUtil.getLoginIdDefaultNull());

		StpUtil.login(id);			// 在当前会话登录此账号
		System.out.println("登录成功");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号：" + StpUtil.getLoginId());
		System.out.println("当前登录设备：" + StpUtil.getLoginDevice());

		renderJson(SaResult.ok());
	}

	// 测试退出登录 ， 浏览器访问： http://localhost:8081/test/logout
	public void logout() {
		StpUtil.logout();
		renderJson(SaResult.ok());
	}

	// 测试角色接口， 浏览器访问： http://localhost:8081/test/testRole
	public void testRole() {
		System.out.println("======================= 进入方法，测试角色接口 ========================= ");

		System.out.println("是否具有角色标识 user " + StpUtil.hasRole("user"));
		System.out.println("是否具有角色标识 admin " + StpUtil.hasRole("admin"));

		System.out.println("没有admin权限就抛出异常");
		StpUtil.checkRole("admin");

		System.out.println("在【admin、user】中只要拥有一个就不会抛出异常");
		StpUtil.checkRoleOr("admin", "user");

		System.out.println("在【admin、user】中必须全部拥有才不会抛出异常");
		StpUtil.checkRoleAnd("admin", "user");

		System.out.println("角色测试通过");

		renderJson(SaResult.ok());
	}

	// 测试权限接口， 浏览器访问： http://localhost:8081/test/testJur
	public void testJur() {
		System.out.println("======================= 进入方法，测试权限接口 ========================= ");

		System.out.println("是否具有权限101" + StpUtil.hasPermission("101"));
		System.out.println("是否具有权限user-add" + StpUtil.hasPermission("user-add"));
		System.out.println("是否具有权限article-get" + StpUtil.hasPermission("article-get"));

		System.out.println("没有user-add权限就抛出异常");
		StpUtil.checkPermission("user-add");

		System.out.println("在【101、102】中只要拥有一个就不会抛出异常");
		StpUtil.checkPermissionOr("101", "102");

		System.out.println("在【101、102】中必须全部拥有才不会抛出异常");
		StpUtil.checkPermissionAnd("101", "102");

		System.out.println("权限测试通过");

		renderJson(SaResult.ok());
	}

	// 测试会话session接口， 浏览器访问： http://localhost:8081/test/session
	public void session() {
		System.out.println("======================= 进入方法，测试会话session接口 ========================= ");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号session的id" + StpUtil.getSession().getId());
		System.out.println("当前登录账号session的id" + StpUtil.getSession().getId());
		System.out.println("测试取值name：" + StpUtil.getSession().get("name"));
		StpUtil.getSession().set("name", new Date());	// 写入一个值
		System.out.println("测试取值name：" + StpUtil.getSession().get("name"));
		renderJson(SaResult.ok());
	}

	// 测试自定义session接口， 浏览器访问： http://localhost:8081/test/session2
	public void session2() {
		System.out.println("======================= 进入方法，测试自定义session接口 ========================= ");
		// 自定义session就是无需登录也可以使用 的session ：比如拿用户的手机号当做 key， 来获取 session
		System.out.println("自定义 session的id为：" + SaSessionCustomUtil.getSessionById("1895544896").getId());
		System.out.println("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").get("name"));
		SaSessionCustomUtil.getSessionById("1895544896").set("name", "张三");	// 写入值
		System.out.println("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").get("name"));
		System.out.println("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").get("name"));
		renderJson(SaResult.ok());
	}

	// ----------
	// 测试token专属session， 浏览器访问： http://localhost:8081/test/getTokenSession
	public void getTokenSession() {
		System.out.println("======================= 进入方法，测试会话session接口 ========================= ");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前token专属session: " + StpUtil.getTokenSession().getId());

		System.out.println("测试取值name：" + StpUtil.getTokenSession().get("name"));
		StpUtil.getTokenSession().set("name", "张三");	// 写入一个值
		System.out.println("测试取值name：" + StpUtil.getTokenSession().get("name"));

		renderJson(SaResult.ok());
	}

	// 打印当前token信息， 浏览器访问： http://localhost:8081/test/tokenInfo
	public void tokenInfo() {
		System.out.println("======================= 进入方法，打印当前token信息 ========================= ");
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
		System.out.println(tokenInfo);
		renderJson(SaResult.data(tokenInfo));
	}

	// 测试注解式鉴权， 浏览器访问： http://localhost:8081/test/atCheck
	@SaCheckLogin						// 注解式鉴权：当前会话必须登录才能通过
	@SaCheckRole("super-admin")			// 注解式鉴权：当前会话必须具有指定角色标识才能通过
	@SaCheckPermission("user-add")		// 注解式鉴权：当前会话必须具有指定权限才能通过
	public void atCheck() {
		System.out.println("======================= 进入方法，测试注解鉴权接口 ========================= ");
		System.out.println("只有通过注解鉴权，才能进入此方法");
		renderJson(SaResult.ok());
	}

	// 测试注解式鉴权， 浏览器访问： http://localhost:8081/test/atJurOr
	@SaCheckPermission(value = {"user-add", "user-all", "user-delete"}, mode = SaMode.OR)		// 注解式鉴权：只要具有其中一个权限即可通过校验
	public void atJurOr() {
		renderJson(SaResult.data("用户信息"));
	}

	// [活动时间] 续签： http://localhost:8081/test/rene
	public void rene() {
		StpUtil.checkActiveTimeout();
		StpUtil.updateLastActiveToNow();
		renderJson(SaResult.ok("续签成功"));
	}

	// 测试踢人下线   浏览器访问： http://localhost:8081/test/kickOut
	public void kickOut() {
		// 先登录上
		StpUtil.login(10001);
		// 踢下线
		StpUtil.kickout(10001);
		// 再尝试获取
		StpUtil.getLoginId();
		// 返回
		renderJson(SaResult.ok());
	}

	// 测试登录接口, 按照设备登录， 浏览器访问： http://localhost:8081/test/login2
	public void login2() {
		String id = getPara("id", "10001");
		String device = getPara("device", "PC");
		StpUtil.login(id, device);
		renderJson(SaResult.ok());
	}

	// 测试身份临时切换： http://localhost:8081/test/switchTo
	public void switchTo() {
		System.out.println("当前会话身份：" + StpUtil.getLoginIdDefaultNull());
		System.out.println("是否正在身份临时切换中: " + StpUtil.isSwitch());
		StpUtil.switchTo(10044, () -> {
			System.out.println("是否正在身份临时切换中: " + StpUtil.isSwitch());
			System.out.println("当前会话身份已被切换为：" + StpUtil.getLoginId());
		});
		System.out.println("是否正在身份临时切换中: " + StpUtil.isSwitch());
		renderJson(SaResult.ok());
	}

	// 测试会话治理   浏览器访问： http://localhost:8081/test/search
	public void search() {
		System.out.println("--------------");
		List<String> tokenValue = StpUtil.searchTokenValue("8feb8265f773", 0, 10, true);
		for (String v : tokenValue) {
			System.out.println(v);
		}
		renderJson(SaResult.ok());
	}

	// 测试指定设备登录   浏览器访问： http://localhost:8081/test/loginByDevice
	public void loginByDevice() {
		System.out.println("--------------");
		StpUtil.login(10001, "PC");
		renderJson(SaResult.data("登录成功"));
	}

	// 测试   浏览器访问： http://localhost:8081/test/test
	public void test() {
		System.out.println("------------进来了");
		renderJson(SaResult.ok());
	}

	// 测试   浏览器访问： http://localhost:8081/test/test2
	public void test2() {
		renderJson(SaResult.ok());
	}

}
