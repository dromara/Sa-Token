package com.pj.sso;

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

/**
 * Sa-Token-SSO Client端 Controller 
 * @author kong
 */
@Controller
public class SsoClientController implements Render {

	// SSO-Client端：首页
	@Produces(MimeType.TEXT_HTML_VALUE)
	@Mapping("/")
	public String index() {
		String authUrl = SaSsoManager.getConfig().splicingAuthUrl();
		String solUrl = SaSsoManager.getConfig().splicingSloUrl();
		String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
					"<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" + 
					"<p><a href=\"javascript:location.href='" + authUrl + "?mode=simple&redirect=' + encodeURIComponent(location.href);\">登录</a> " + 
					"<a href=\"javascript:location.href='" + solUrl + "?back=' + encodeURIComponent(location.href);\">注销</a> </p>";
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
