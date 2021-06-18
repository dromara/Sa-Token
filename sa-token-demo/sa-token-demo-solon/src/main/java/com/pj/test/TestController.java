package com.pj.test;

import java.util.Date;
import java.util.List;

import com.pj.util.AjaxJson;
import com.pj.util.Ttime;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;

/**
 * 测试专用Controller 
 * @author kong
 * @author noear
 */
@Controller
@Mapping("/test/")
public class TestController {

	
	// 测试登录接口， 浏览器访问： http://localhost:8081/test/login
	@Mapping("login")
	public AjaxJson login(@Param(defaultValue="10001") String id) {
		System.out.println("======================= 进入方法，测试登录接口 ========================= ");
		System.out.println("当前会话的token：" + StpUtil.getTokenValue());
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号：" + StpUtil.getLoginIdDefaultNull());
		
		StpUtil.login(id);			// 在当前会话登录此账号 	
		System.out.println("登录成功");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号：" + StpUtil.getLoginId());
//		System.out.println("当前登录账号并转为int：" + StpUtil.getLoginIdAsInt());
		System.out.println("当前登录设备：" + StpUtil.getLoginDevice());
//		System.out.println("当前token信息：" + StpUtil.getTokenInfo());	
		
		return AjaxJson.getSuccess();
	}
	
	// 测试退出登录 ， 浏览器访问： http://localhost:8081/test/logout
	@Mapping("logout")
	public AjaxJson logout() {
		StpUtil.logout();
//		StpUtil.logoutByLoginId(10001);
		return AjaxJson.getSuccess();
	}
	
	// 测试角色接口， 浏览器访问： http://localhost:8081/test/testRole
	@Mapping("testRole")
	public AjaxJson testRole() {
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
		
		return AjaxJson.getSuccess();
	}

	// 测试权限接口， 浏览器访问： http://localhost:8081/test/testJur
	@Mapping("testJur")
	public AjaxJson testJur() {
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
		
		return AjaxJson.getSuccess();
	}

	// 测试会话session接口， 浏览器访问： http://localhost:8081/test/session 
	@Mapping("session")
	public AjaxJson session()  {
		System.out.println("======================= 进入方法，测试会话session接口 ========================= ");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号session的id" + StpUtil.getSession().getId());
		System.out.println("当前登录账号session的id" + StpUtil.getSession().getId());
		System.out.println("测试取值name：" + StpUtil.getSession().get("name"));
		StpUtil.getSession().set("name", new Date());	// 写入一个值 
		System.out.println("测试取值name：" + StpUtil.getSession().get("name"));
		System.out.println( ONode.stringify(StpUtil.getSession()));
		return AjaxJson.getSuccess();
	}
	
	// 测试自定义session接口， 浏览器访问： http://localhost:8081/test/session2 
	@Mapping("session2")
	public AjaxJson session2() {
		System.out.println("======================= 进入方法，测试自定义session接口 ========================= ");
		// 自定义session就是无需登录也可以使用 的session ：比如拿用户的手机号当做 key， 来获取 session 
		System.out.println("自定义 session的id为：" + SaSessionCustomUtil.getSessionById("1895544896").getId());
		System.out.println("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").get("name"));
		SaSessionCustomUtil.getSessionById("1895544896").set("name", "张三");	// 写入值 
		System.out.println("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").get("name"));
		System.out.println("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").get("name"));
		return AjaxJson.getSuccess();
	}

	// ---------- 
	// 测试token专属session， 浏览器访问： http://localhost:8081/test/getTokenSession 
	@Mapping("getTokenSession")
	public AjaxJson getTokenSession() {
		System.out.println("======================= 进入方法，测试会话session接口 ========================= ");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前token专属session: " + StpUtil.getTokenSession().getId());

		System.out.println("测试取值name：" + StpUtil.getTokenSession().get("name"));
		StpUtil.getTokenSession().set("name", "张三");	// 写入一个值 
		System.out.println("测试取值name：" + StpUtil.getTokenSession().get("name"));
		
		return AjaxJson.getSuccess();
	}
	
	// 打印当前token信息， 浏览器访问： http://localhost:8081/test/tokenInfo
	@Mapping("tokenInfo")
	public AjaxJson tokenInfo() {
		System.out.println("======================= 进入方法，打印当前token信息 ========================= ");
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
		System.out.println(tokenInfo);
		return AjaxJson.getSuccessData(tokenInfo);
	}
	
	// 测试注解式鉴权， 浏览器访问： http://localhost:8081/test/atCheck
	@SaCheckLogin						// 注解式鉴权：当前会话必须登录才能通过 
	@SaCheckRole("super-admin")			// 注解式鉴权：当前会话必须具有指定角色标识才能通过 
	@SaCheckPermission("user-add")		// 注解式鉴权：当前会话必须具有指定权限才能通过 
	@Mapping("atCheck")
	public AjaxJson atCheck() {
		System.out.println("======================= 进入方法，测试注解鉴权接口 ========================= ");
		System.out.println("只有通过注解鉴权，才能进入此方法");
//		StpUtil.checkActivityTimeout();
//		StpUtil.updateLastActivityToNow();
		return AjaxJson.getSuccess();
	}
	
	// 测试注解式鉴权， 浏览器访问： http://localhost:8081/test/atJurOr
	@Mapping("atJurOr")
	@SaCheckPermission(value = {"user-add", "user-all", "user-delete"}, mode = SaMode.OR)		// 注解式鉴权：只要具有其中一个权限即可通过校验 
	public AjaxJson atJurOr() {
		return AjaxJson.getSuccessData("用户信息");
	}
	
	// [活动时间] 续签： http://localhost:8081/test/rene
	@Mapping("rene")
	public AjaxJson rene() {
		StpUtil.checkActivityTimeout();
		StpUtil.updateLastActivityToNow();
		return AjaxJson.getSuccess("续签成功");
	}
	
	// 测试踢人下线   浏览器访问： http://localhost:8081/test/kickOut 
	@Mapping("kickOut")
	public AjaxJson kickOut() {
		// 先登录上 
		StpUtil.login(10001);
		// 踢下线 
		StpUtil.logoutByLoginId(10001);
		// 再尝试获取
		StpUtil.getLoginId();
		// 返回 
		return AjaxJson.getSuccess();
	}

	// 测试登录接口, 按照设备登录， 浏览器访问： http://localhost:8081/test/login2
	@Mapping("login2")
	public AjaxJson login2(@Param(defaultValue="10001") String id, @Param(defaultValue="PC") String device) {
		StpUtil.login(id, device);
		return AjaxJson.getSuccess();
	}
	
	// 测试身份临时切换： http://localhost:8081/test/switchTo
	@Mapping("switchTo")
	public AjaxJson switchTo() {
		System.out.println("当前会话身份：" + StpUtil.getLoginIdDefaultNull());
		System.out.println("是否正在身份临时切换中: " + StpUtil.isSwitch()); 
		StpUtil.switchTo(10044, () -> {
			System.out.println("是否正在身份临时切换中: " + StpUtil.isSwitch()); 
			System.out.println("当前会话身份已被切换为：" + StpUtil.getLoginId());
		});		
		System.out.println("是否正在身份临时切换中: " + StpUtil.isSwitch()); 
		return AjaxJson.getSuccess();
	}
	
	// 测试会话治理   浏览器访问： http://localhost:8081/test/search
	@Mapping("search")
	public AjaxJson search() {
		System.out.println("--------------");
		Ttime t = new Ttime().start();
		List<String> tokenValue = StpUtil.searchTokenValue("8feb8265f773", 0, 10);
		for (String v : tokenValue) {
//			SaSession session = StpUtil.getSessionBySessionId(sid);
			System.out.println(v);
		}
		System.out.println("用时：" + t.end().toString());
		return AjaxJson.getSuccess();
	}

	// 测试指定设备登录   浏览器访问： http://localhost:8081/test/loginByDevice
	@Mapping("loginByDevice")
	public AjaxJson loginByDevice() {
		System.out.println("--------------");
		StpUtil.login(10001, "PC");
		return AjaxJson.getSuccessData("登录成功");
	}

	
	// 测试   浏览器访问： http://localhost:8081/test/test
	@Mapping("test")
	public AjaxJson test() {
		System.out.println("进来了");
		return AjaxJson.getSuccess("访问成功");
	}
	
	// 测试   浏览器访问： http://localhost:8081/test/test2
	@Mapping("test2")
	public AjaxJson test2() {
		return AjaxJson.getSuccess();
	}


}
