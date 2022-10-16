package com.pj.cases.up;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 二级认证示例 
 * 
 * @author kong
 * @since 2022-10-16 
 */
@RestController
@RequestMapping("/safe/")
public class SafeAuthController {

	/*
	 * 前提：首先调用登录接口进行登录，代码在 com.pj.cases.use.LoginAuthController 中有详细解释，此处不再赘述 
	 * 		---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
	 * 
	 * 测试步骤：
		1、前端调用 deleteProject 接口，尝试删除仓库。    ---- http://localhost:8081/safe/deleteProject
		2、后端校验会话尚未完成二级认证，返回： 仓库删除失败，请完成二级认证后再次访问接口。
		3、前端将信息提示给用户，用户输入密码，调用 openSafe 接口。    ---- http://localhost:8081/safe/openSafe
		4、后端比对用户输入的密码，完成二级认证，有效期为：120秒。
		5、前端在 120 秒内再次调用 deleteProject 接口，尝试删除仓库。    ---- http://localhost:8081/safe/deleteProject
		6、后端校验会话已完成二级认证，返回：仓库删除成功。
	 */
	
	// 删除仓库    ---- http://localhost:8081/safe/deleteProject
	@RequestMapping("deleteProject")
	public SaResult deleteProject(String projectId) {
	    // 第1步，先检查当前会话是否已完成二级认证 
		// 		这个地方既可以通过 StpUtil.isSafe() 手动判断，
		// 		也可以通过 StpUtil.checkSafe() 或者 @SaCheckSafe 来校验（校验不通过时将抛出 NotSafeException 异常）
	    if(!StpUtil.isSafe()) {
	        return SaResult.error("仓库删除失败，请完成二级认证后再次访问接口");
	    }
	
	    // 第2步，如果已完成二级认证，则开始执行业务逻辑
	    // ... 
	
	    // 第3步，返回结果 
	    return SaResult.ok("仓库删除成功"); 
	}
	
	// 提供密码进行二级认证    ---- http://localhost:8081/safe/openSafe
	@RequestMapping("openSafe")
	public SaResult openSafe(String password) {
	    // 比对密码（此处只是举例，真实项目时可拿其它参数进行校验）
	    if("123456".equals(password)) {
	
	        // 比对成功，为当前会话打开二级认证，有效期为120秒 
	        StpUtil.openSafe(120);
	        return SaResult.ok("二级认证成功");
	    }
	
	    // 如果密码校验失败，则二级认证也会失败
	    return SaResult.error("二级认证失败"); 
	}

}
