package com.pj.h5;

import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.util.SaResult;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * 前后台分离架构下集成SSO所需的代码 （SSO-Client端）
 * <p>（注：如果不需要前后端分离架构下集成SSO，可删除此包下所有代码）</p>
 * @author click33
 *
 */
@Controller
public class H5Controller {

	// 判断当前是否登录
	@Mapping("/sso/isLogin")
	public Object isLogin() {
		return SaResult.data(StpUtil.isLogin()).set("loginId", StpUtil.getLoginIdDefaultNull());
	}

	// 返回SSO认证中心登录地址
	@Mapping("/sso/getSsoAuthUrl")
	public SaResult getSsoAuthUrl(String clientLoginUrl) {
		String serverAuthUrl = SaSsoClientUtil.buildServerAuthUrl(clientLoginUrl, "");
		return SaResult.data(serverAuthUrl);
	}

	// 根据 ticket 进行登录
	@Mapping("/sso/doLoginByTicket")
	public SaResult doLoginByTicket(String ticket) {
		SaCheckTicketResult ctr = SaSsoClientProcessor.instance.checkTicket(ticket);
		StpUtil.login(ctr.loginId, new SaLoginParameter()
				.setTimeout(ctr.remainTokenTimeout)
				.setDeviceId(ctr.deviceId)
		);
		return SaResult.data(StpUtil.getTokenValue());
	}

}
