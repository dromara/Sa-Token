package com.pj.h5;

import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

/**
 * 前后台分离架构下集成SSO所需的代码 （SSO-Client端）
 * <p>（注：如果不需要前后端分离架构下集成SSO，可删除此包下所有代码）</p>
 * @author click33
 *
 */
@Controller
public class H5Controller implements Render {

	// 当前是否登录 
	@Mapping("/sso/isLogin")
	public Object isLogin() {
		return SaResult.data(StpUtil.isLogin());
	}
	
	// 返回SSO认证中心登录地址 
	@Mapping("/sso/getSsoAuthUrl")
	public SaResult getSsoAuthUrl(String clientLoginUrl) {
		String serverAuthUrl = SaSsoUtil.buildServerAuthUrl(clientLoginUrl, "");
		return SaResult.data(serverAuthUrl);
	}
	
	// 根据ticket进行登录 
	@Mapping("/sso/doLoginByTicket")
	public SaResult doLoginByTicket(String ticket) {
		Object loginId = SaSsoProcessor.instance.checkTicket(ticket, "/sso/doLoginByTicket");
		if(loginId != null) {
			StpUtil.login(loginId);
			return SaResult.data(StpUtil.getTokenValue());
		}
		return SaResult.error("无效ticket：" + ticket); 
	}

	// 全局异常拦截并转换
	@Override
	public void render(Object data, Context ctx) throws Throwable {
		if(data instanceof Exception){
			data = SaResult.error(((Exception)data).getMessage());
		}

		ctx.render(data);
	}
	
}
