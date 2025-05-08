package com.pj.sso;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

/**
 * Sa-Token-SSO Client端 Controller 
 * @author click33
 */
@Controller
public class SsoClientController implements Render {

	// SSO-Client端：首页
	@Produces(MimeType.TEXT_HTML_VALUE)
	@Mapping("/")
	public String index() {
		String url = SaFoxUtil.encodeUrl( SaFoxUtil.joinParam(SaHolder.getRequest().getUrl(), Context.current().queryString()) );
		SaSsoClientConfig cfg = SaSsoManager.getClientConfig();

		String str = "<h2>Sa-Token SSO-Client 应用端 (模式一)</h2>" +
				"<p>当前会话是否登录：" + StpUtil.isLogin() + " (" + StpUtil.getLoginId("") + ")</p>" +
				"<p>" +
				"<a href='" + cfg.splicingAuthUrl() + "?mode=simple&client=" + cfg.getClient() + "&redirect=" + url + "'>登录</a> - " +
				"<a href='" + cfg.splicingSignoutUrl() + "?singleDeviceIdLogout=true&back=" + url + "'>单浏览器注销</a> - " +
				"<a href='" + cfg.splicingSignoutUrl() + "?back=" + url + "'>全端注销</a> " +
				"</p>";
		return str;
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
