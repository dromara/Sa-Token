package com.pj.sso;


import cn.dev33.satoken.sso.SaSsoProcessor;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * Sa-Token-SSO Server端 Controller 
 * @author kong
 *
 */
@Controller
public class SsoServerController {

	/*
	 * SSO-Server端：处理所有SSO相关请求 
	 * 		http://{host}:{port}/sso/auth			-- 单点登录授权地址，接受参数：redirect=授权重定向地址 
	 * 		http://{host}:{port}/sso/doLogin		-- 账号密码登录接口，接受参数：name、pwd 
	 * 		http://{host}:{port}/sso/checkTicket	-- Ticket校验接口（isHttp=true时打开），接受参数：ticket=ticket码、ssoLogoutCall=单点注销回调地址 [可选] 
	 * 		http://{host}:{port}/sso/signout		-- 单点注销地址（isSlo=true时打开），接受参数：loginId=账号id、secretkey=接口调用秘钥 
	 */
	@Mapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoProcessor.instance.serverDister();
	}
}
