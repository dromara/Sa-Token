package com.pj.h5;


import cn.dev33.satoken.sso.template.SaSsoUtil;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * 前后台分离架构下集成SSO所需的代码 （SSO-Server端）
 * <p>（注：如果不需要前后端分离架构下集成SSO，可删除此包下所有代码）</p>
 * @author click33
 *
 */
@Controller
public class H5Controller {

	/**
	 * 获取 redirectUrl
	 */
	@Mapping("/sso/getRedirectUrl")
	public SaResult getRedirectUrl(String client, String redirect, String mode) {
		// 未登录情况下，返回 code=401
		if(StpUtil.isLogin() == false) {
			return SaResult.code(401);
		}
		// 已登录情况下，构建 redirectUrl
		redirect = SaFoxUtil.decoderUrl(redirect);
		if(SaSsoConsts.MODE_SIMPLE.equals(mode)) {
			// 模式一
			SaSsoUtil.checkRedirectUrl(client, redirect);
			return SaResult.data(redirect);
		} else {
			// 模式二或模式三
			String redirectUrl = SaSsoUtil.buildRedirectUrl(client, redirect, StpUtil.getLoginId(), StpUtil.getLoginDeviceId());
			return SaResult.data(redirectUrl);
		}
	}

//	/**
//	 * 控制当前类的异常
//	 */
//	@Override
//	public void render(Object data, Context ctx) throws Throwable {
//		if (data instanceof Throwable) {
//			Throwable e = (Throwable) data;
//			e.printStackTrace();
//			ctx.render(SaResult.error(e.getMessage()));
//		} else {
//			ctx.render(data);
//		}
//	}

}
