package com.pj.h5;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 前后台分离架构下集成SSO所需的代码 （SSO-Server端）
 * <p>（注：如果不需要前后端分离架构下集成SSO，可删除此包下所有代码）</p>
 * @author kong
 *
 */
@RestController
public class H5Controller {
	
	/**
	 * 获取 redirectUrl 
	 */
	@RequestMapping("/getRedirectUrl")
	private Object getRedirectUrl(String redirect) {
		// 未登录情况下，返回 code=401 
		if(StpUtil.isLogin() == false) {
			return SaResult.code(401);
		}
		// 已登录情况下，构建 redirectUrl 
		String redirectUrl = SaSsoUtil.buildRedirectUrl(StpUtil.getLoginId(), redirect);
		return SaResult.data(redirectUrl);
	}

	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}
