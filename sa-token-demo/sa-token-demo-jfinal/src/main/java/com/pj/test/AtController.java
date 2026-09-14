package com.pj.test;

import com.jfinal.core.Controller;
import com.jfinal.core.Path;

import cn.dev33.satoken.annotation.SaCheckHttpBasic;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 注解鉴权测试
 * @author click33
 *
 */
@Path("/at")
public class AtController extends Controller {

	// 登录认证，登录之后才可以进入方法  ---- http://localhost:8081/at/checkLogin
	@SaCheckLogin
	public void checkLogin() {
		renderJson(SaResult.ok());
	}

	// 权限认证，具备user-add权限才可以进入方法  ---- http://localhost:8081/at/checkPermission
	@SaCheckPermission("user-add")
	public void checkPermission() {
		renderJson(SaResult.ok());
	}

	// 权限认证，同时具备所有权限才可以进入  ---- http://localhost:8081/at/checkPermissionAnd
	@SaCheckPermission({"user-add", "user-delete", "user-update"})
	public void checkPermissionAnd() {
		renderJson(SaResult.ok());
	}

	// 权限认证，只要具备其中一个就可以进入  ---- http://localhost:8081/at/checkPermissionOr
	@SaCheckPermission(value = {"user-add", "user-delete", "user-update"}, mode = SaMode.OR)
	public void checkPermissionOr() {
		renderJson(SaResult.ok());
	}

	// 角色认证，只有具备admin角色才可以进入  ---- http://localhost:8081/at/checkRole
	@SaCheckRole("admin")
	public void checkRole() {
		renderJson(SaResult.ok());
	}

	// 完成二级认证  ---- http://localhost:8081/at/openSafe
	public void openSafe() {
		StpUtil.openSafe(200); // 打开二级认证，有效期为200秒
		renderJson(SaResult.ok());
	}

	// 通过二级认证后才可以进入  ---- http://localhost:8081/at/checkSafe
	@SaCheckSafe
	public void checkSafe() {
		renderJson(SaResult.ok());
	}

	// 通过Basic认证后才可以进入  ---- http://localhost:8081/at/checkBasic
	@SaCheckHttpBasic(account = "sa:123456")
	public void checkBasic() {
		renderJson(SaResult.ok());
	}

}
