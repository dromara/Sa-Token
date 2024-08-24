package com.pj.oauth2;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sa-Token-OAuth2 Server端 Controller
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

		// 重写 AccessToken 创建策略，返回会话令牌
		SaOAuth2Strategy.instance.createAccessToken = (clientId, loginId, scopes) -> {
			System.out.println("----返回会话令牌");
			return StpUtil.getOrCreateLoginSession(loginId);
		};

	}


	// ---------- 开放相关资源接口： Client端根据 Access-Token ，置换相关资源 ------------ 
	
	// 获取 userinfo 信息：昵称、头像、性别等等
	@RequestMapping("/oauth2/userinfo")
	public SaResult userinfo() {
		// 获取 Access-Token 对应的账号id
		String accessToken = SaOAuth2Manager.getDataResolver().readAccessToken(SaHolder.getRequest());
		Object loginId = SaOAuth2Util.getLoginIdByAccessToken(accessToken);
		System.out.println("-------- 此Access-Token对应的账号id: " + loginId);
		
		// 校验 Access-Token 是否具有权限: userinfo
		SaOAuth2Util.checkScope(accessToken, "userinfo");
		
		// 模拟账号信息 （真实环境需要查询数据库获取信息）
		Map<String, Object> map = new LinkedHashMap<>();
		// map.put("userId", loginId);  一般原则下，oauth2-server 不能把 userId 返回给 oauth2-client
		map.put("nickname", "林小林");
		map.put("avatar", "http://xxx.com/1.jpg");
		map.put("age", "18");
		map.put("sex", "男");
		map.put("address", "山东省 青岛市 城阳区");
		return SaResult.ok().setMap(map);
	}
	
}
