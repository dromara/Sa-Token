package com.pj.cases;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 注解鉴权示例 
 * 
 * @author kong
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/at-check/")
public class AtCheckController {

	/*
	 * 前提1：首先调用登录接口进行登录，代码在 com.pj.cases.LoginAuthController 中有详细解释，此处不再赘述 
	 * 		---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
	 * 
	 * 前提2：项目在配置类中注册拦截器 SaInterceptor ，代码在  com.pj.satoken.SaTokenConfigure
	 * 		此拦截器将打开注解鉴权功能 
	 * 
	 * 然后我们就可以使用以下示例中的代码进行注解鉴权了 
	 */
	
	// 登录鉴权   ---- http://localhost:8081/at-check/checkLogin
	// 		登录认证后才可以进入方法 
	@SaCheckLogin
	@RequestMapping("checkLogin")
	public SaResult checkLogin() {
		// 通过注解鉴权后才能进入方法 ... 
		return SaResult.ok();
	}

	// 权限校验   ---- http://localhost:8081/at-check/checkPermission
	//		只有具有 user.add 权限的账号才可以进入方法 
	@SaCheckPermission("user.add")
	@RequestMapping("checkPermission")
	public SaResult checkPermission() {
		// ... 
		return SaResult.ok();
	}

	// 权限校验2   ---- http://localhost:8081/at-check/checkPermission2
	//		一次性校验多个权限，必须全部拥有，才可以进入方法 
	@SaCheckPermission(value = {"user.add", "user.delete", "user.update"}, mode = SaMode.AND)
	@RequestMapping("checkPermission2")
	public SaResult checkPermission2() {
		// ... 
		return SaResult.ok();
	}

	// 权限校验3   ---- http://localhost:8081/at-check/checkPermission3
	//		一次性校验多个权限，只要拥有其中一个，就可以进入方法 
	@SaCheckPermission(value = {"user.add", "user.delete", "user.update"}, mode = SaMode.OR)
	@RequestMapping("checkPermission3")
	public SaResult checkPermission3() {
		// ... 
		return SaResult.ok();
	}

	// 角色校验   ---- http://localhost:8081/at-check/checkRole
	//		只有具有 super-admin 角色的账号才可以进入方法 
	@SaCheckRole("super-admin")
	@RequestMapping("checkRole")
	public SaResult checkRole() {
		// ... 
		return SaResult.ok();
	}
	
	// 角色权限双重 “or校验”   ---- http://localhost:8081/at-check/userAdd
	//		具备 "user.add"权限 或者 "admin"角色 即可通过校验
	@RequestMapping("userAdd")
	@SaCheckPermission(value = "user.add", orRole = "admin")        
	public SaResult userAdd() {
	    return SaResult.data("用户信息");
	}

	// 忽略校验 ---- http://localhost:8081/at-check/ignore
	//		使用 @SaIgnore 修饰的方法，无需任何校验即可进入，具体使用示例可参照在线文档 
	@SaIgnore
	@SaCheckLogin   
	@RequestMapping("ignore")
	public SaResult ignore() {
	    return SaResult.ok();
	}

}
