package com.pj.cases.more;

import cn.dev33.satoken.annotation.SaCheckEL;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SaCheckEL EL表达式注解鉴权示例
 *
 * @author click33
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/check-el/")
public class SaCheckELController {

	// 登录校验  ---- http://localhost:8081/check-el/test1
	@SaCheckEL("stp.checkLogin()")
	@RequestMapping("test1")
	public SaResult test1() {
		return SaResult.ok();
	}

	// 角色校验  ---- http://localhost:8081/check-el/test2
	@SaCheckEL("stp.checkRole('dev-admin')")
	@RequestMapping("test2")
	public SaResult test2() {
		return SaResult.ok();
	}

	// 权限校验  ---- http://localhost:8081/check-el/test3
	@SaCheckEL("stp.checkPermission('user:edit')")
	@RequestMapping("test3")
	public SaResult test3() {
		return SaResult.ok();
	}

	// 二级认证  ---- http://localhost:8081/check-el/test4
	@SaCheckEL("stp.checkSafe()")
	@RequestMapping("test4")
	public SaResult test4() {
		return SaResult.ok();
	}

	// 参数长度校验  ---- http://localhost:8081/check-el/test5?name=zhangsan
	@SaCheckEL("NEED( #name.length() > 3 )")
	@RequestMapping("test5")
	public SaResult test5(@RequestParam(defaultValue = "") String name) {
		return SaResult.ok().set("name", name);
	}

	// 参数长度校验，并自定义异常描述信息  ---- http://localhost:8081/check-el/test6?name=z
	@SaCheckEL("NEED( #name !=null && #name.length() > 3, 'name长度不够' )")
	@RequestMapping("test6")
	public SaResult test6(String name) {
		return SaResult.ok().set("name", name);
	}

	// 已登录, 或者查询数据在公开范围内 ---- http://localhost:8081/check-el/test7?id=10044
	@SaCheckEL("NEED( stp.isLogin() or (#id != null and #id > 10010) )")
	@RequestMapping("test7")
	public SaResult test7(long id) {
		return SaResult.ok().set("id", id);
	}

	// SaSession 里取值校验 ---- http://localhost:8081/check-el/test8
	@SaCheckEL("NEED( stp.getSession().get('name') == 'zhangsan' )")
	@RequestMapping("test8")
	public SaResult test8() {
		return SaResult.ok();
	}

	// 多账号体系鉴权测试 ---- http://localhost:8081/check-el/test9
	@SaCheckEL("stpUser.checkLogin()")
	@RequestMapping("test9")
	public SaResult test9() {
		return SaResult.ok();
	}

	// 本模块需要鉴权的权限码
	public String permissionCode = "article:add";

	// 调用本类的成员变量 ---- http://localhost:8081/check-el/test10
	@SaCheckEL("stp.checkPermission( this.permissionCode )")
	@RequestMapping("test10")
	public SaResult test10() {
		return SaResult.ok();
	}

	// 忽略鉴权测试 ---- http://localhost:8081/check-el/test11
	@SaIgnore
	@SaCheckEL("stp.checkPermission( 'abc' )")
	@RequestMapping("test11")
	public SaResult test11() {
		return SaResult.ok();
	}


}
