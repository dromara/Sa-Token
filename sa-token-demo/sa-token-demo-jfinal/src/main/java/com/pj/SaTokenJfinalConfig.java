package com.pj;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import com.jfinal.kit.JsonKit;
import com.pj.current.GlobalException;
import com.pj.satoken.StpInterfaceImpl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.jfinal.SaAnnotationInterceptor;
import cn.dev33.satoken.jfinal.SaTokenActionHandler;
import cn.dev33.satoken.jfinal.SaTokenContextForJfinal;
import cn.dev33.satoken.jfinal.SaTokenPathFilter;
import cn.dev33.satoken.util.SaResult;

/**
 * [Sa-Token 权限认证] JFinal 配置类
 * @author click33
 *
 */
public class SaTokenJfinalConfig extends JFinalConfig {

	public SaTokenJfinalConfig() {
		SaTokenConfig config = new SaTokenConfig();
		config.setTokenName("satoken");
		config.setTimeout(2592000);
		config.setActiveTimeout(-1);
		config.setIsConcurrent(true);
		config.setIsShare(false);
		config.setTokenStyle("uuid");
		config.setIsLog(true);
		SaManager.setConfig(config);
		SaManager.setSaTokenContext(new SaTokenContextForJfinal());
		SaManager.setStpInterface(new StpInterfaceImpl());
	}

	@Override
	public void configConstant(Constants constants) {
		constants.setDevMode(true);
		constants.setEncoding("UTF-8");
	}

	@Override
	public void configRoute(Routes routes) {
		routes.scan("com.pj.test");
	}

	@Override
	public void configEngine(Engine engine) {
	}

	@Override
	public void configPlugin(Plugins plugins) {
	}

	@Override
	public void configInterceptor(Interceptors interceptors) {
		// 全局异常：拦住 Action 里 StpUtil.checkXxx() 抛出的鉴权异常
		interceptors.add(new GlobalException());
		// 注册 [Sa-Token 全局过滤器]
		interceptors.add(new SaTokenPathFilter()
			.addInclude("/**")
			.setAuth(obj -> {
				// SaManager.getLog().debug("----- 请求path={}  提交token={}", SaHolder.getRequest().getRequestPath(), StpUtil.getTokenValue());
			})
			.setError(e -> {
				System.out.println("---------- sa全局异常 ");
				return JsonKit.toJson(SaResult.error(e.getMessage()));
			})
			.setBeforeAuth(r -> {
				SaHolder.getResponse()
					.setServer("sa-server")
					.setHeader("X-Frame-Options", "SAMEORIGIN")
					.setHeader("X-XSS-Protection", "1; mode=block")
					.setHeader("X-Content-Type-Options", "nosniff")
					;
			})
		);
		// 注册 Sa-Token 拦截器打开注解鉴权功能
		interceptors.add(new SaAnnotationInterceptor());
	}

	@Override
	public void configHandler(Handlers handlers) {
		handlers.setActionHandler(new SaTokenActionHandler());
	}

}
