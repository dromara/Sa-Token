package cn.dev33.satoken.integrate.annotation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 测试注解用的Controller 
 * 
 * @author kong
 * @since: 2022-9-2
 */
@RestController
@RequestMapping("/at/")
public class SaAnnotationController {

	// 登录 
	@RequestMapping("login")
	public SaResult login(long id) {
		StpUtil.login(id);
		return SaResult.ok().set("token", StpUtil.getTokenValue());
	}

	// 登录校验 
	@SaCheckLogin
	@RequestMapping("checkLogin")
	public SaResult checkLogin() {
		return SaResult.ok();
	}

	// 角色校验 
	@SaCheckRole("admin")
	@RequestMapping("checkRole")
	public SaResult checkRole() {
		return SaResult.ok();
	}

	// 权限校验 
	@SaCheckPermission("art-add")
	@RequestMapping("checkPermission")
	public SaResult checkPermission() {
		return SaResult.ok();
	}

	// 权限校验 or 角色校验 
	@SaCheckPermission(value = "art-add2", orRole = "admin")
	@RequestMapping("checkPermission2")
	public SaResult checkPermission2() {
		return SaResult.ok();
	}

	// 开启二级认证 
	@RequestMapping("openSafe")
	public SaResult openSafe() {
		StpUtil.openSafe(120);
		return SaResult.ok();
	}

	// 二级认证校验
	@SaCheckSafe
	@RequestMapping("checkSafe")
	public SaResult checkSafe() {
		return SaResult.ok();
	}
	
}
