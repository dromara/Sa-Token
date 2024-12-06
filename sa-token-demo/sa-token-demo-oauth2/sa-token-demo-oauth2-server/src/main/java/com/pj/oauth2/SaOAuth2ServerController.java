package com.pj.oauth2;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Sa-Token-OAuth2 Server 认证端 Controller
 *
 * @author click33
 */
@RestController
public class SaOAuth2ServerController {

	// OAuth2-Server 端：处理所有 OAuth2 相关请求
	@RequestMapping("/oauth2/*")
	public Object request() {
		System.out.println("------- 进入请求: " + SaHolder.getRequest().getUrl());
		return SaOAuth2ServerProcessor.instance.dister();
	}

	// Sa-Token OAuth2 定制化配置
	@Autowired
	public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
		// 未登录的视图
		oauth2Server.notLoginView = ()->{
			return new ModelAndView("login.html");
		};

		// 登录处理函数
		oauth2Server.doLoginHandle = (name, pwd) -> {
			if("sa".equals(name) && "123456".equals(pwd)) {
				StpUtil.login(10001);
				return SaResult.ok();
			}
			return SaResult.error("账号名或密码错误");
		};

		// 授权确认视图
		oauth2Server.confirmView = (clientId, scopes)->{
			Map<String, Object> map = new HashMap<>();
			map.put("clientId", clientId);
			map.put("scope", scopes);
			return new ModelAndView("confirm.html", map);
		};

	}

}
