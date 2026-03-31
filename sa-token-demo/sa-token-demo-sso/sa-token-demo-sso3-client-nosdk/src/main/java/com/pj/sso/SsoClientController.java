package com.pj.sso;

import com.pj.sso.util.AjaxJson;
import com.pj.sso.util.MyHttpSessionHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * SSO Client端 Controller
 * @author click33
 */
@RestController
public class SsoClientController {

	// SSO-Client端：首页
	@RequestMapping("/")
	public String index(HttpSession session) {
		Object userId = session.getAttribute("userId");
		String str = "<h2>Sa-Token SSO-Client 应用端 (模式三-NoSdk)</h2>" +
					"<p>当前会话是否登录：" + (userId != null) + " (" + userId + ")</p>" +
					"<p><a href=\"javascript:location.href='/sso/login?back=' + encodeURIComponent(location.href);\">登录</a>" +
					" <a href='/sso/logout?back=' +  + encodeURIComponent(location.href);>注销</a>" +
					" <a href='/sso/myInfo' target=\"_blank\">获取资料</a></p>";
		return str;
	}

	// SSO-Client端：单点登录地址
	@RequestMapping("/sso/login")
	public Object ssoLogin(String ticket, @RequestParam(defaultValue = "/") String back,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {

		// 如果已经登录，则直接返回
		if (session.getAttribute("userId") != null) {
			response.sendRedirect(back);
			return null;
		}

		/*
		 * 此时有两种情况:
		 * 情况1：ticket无值，说明此请求是Client端访问，需要重定向至SSO认证中心
		 * 情况2：ticket有值，说明此请求从SSO认证中心重定向而来，需要根据ticket进行登录
		 */
		if (ticket == null) {
			// ------- 情况 1
			// 当前 url，形如：http://sso-client.com/sso/login?back=xxx
			String clientLoginUrl = request.getRequestURL().toString() + "?back=" + SsoRequestUtil.encodeUrl(back);
			// 最终授权地址，形如：http://sso-server.com/sso/auth?client=xxx&redirect=http://sso-client.com/sso/login?back=xxx
			String serverAuthUrl = SsoRequestUtil.authUrl
					+ "?client=" + SsoRequestUtil.clientId
					+ "&redirect=" + clientLoginUrl;
			response.sendRedirect(serverAuthUrl);
			return null;

		} else {
			// ------- 情况 2
			// 构建 checkTicket 请求参数，以 ticket 查询 userId
			Map<String, String> params = new LinkedHashMap<>();
			params.put("msgType", "checkTicket");
			params.put("client", SsoRequestUtil.clientId);
			params.put("ticket", ticket);
			SsoSignUtil.addSignParams(params);
			String pushUrl = SsoRequestUtil.buildUrl(SsoRequestUtil.pushSUrl, params);
			AjaxJson result = SsoRequestUtil.request(pushUrl);

			// 200 代表校验成功
			if (result.getCode() == 200 && !SsoRequestUtil.isEmpty(result.getData())) {
				// 登录上
				session.setAttribute("userId", result.getData());
				// 返回 back 地址
				response.sendRedirect(back);
				return null;
			} else {
				// 将 sso-server 回应的消息作为异常抛出
				throw new RuntimeException(result.getMsg());
			}
		}
	}

	// SSO-Client端：单点注销地址
	@RequestMapping("/sso/logout")
	public Object ssoLogout(@RequestParam(defaultValue = "/") String back,
			HttpServletResponse response, HttpSession session) throws IOException {

		// 如果未登录，则无需注销
		if (session.getAttribute("userId") == null) {
			response.sendRedirect(back);
			return null;
		}

		// 调用 sso-server 认证中心单点注销 API
		Object loginId = session.getAttribute("userId");
		Map<String, String> params = new LinkedHashMap<>();
		params.put("msgType", "signout");
		params.put("client", SsoRequestUtil.clientId);
		params.put("loginId", String.valueOf(loginId));
		SsoSignUtil.addSignParams(params);
		String pushUrl = SsoRequestUtil.buildUrl(SsoRequestUtil.pushSUrl, params);
		AjaxJson result = SsoRequestUtil.request(pushUrl);

		// 校验响应状态码，200 代表成功
		if (result.getCode() == 200) {
			// 极端场景下，sso-server 中心的单点注销可能并不会通知到此 client 端，所以这里需要再补一刀
			session.removeAttribute("userId");
			// 返回 back 地址
			response.sendRedirect(back);
			return null;
		} else {
			// 将 sso-server 回应的消息作为异常抛出
			throw new RuntimeException(result.getMsg());
		}
	}

	// SSO-Server 端消息推送接收地址（单点注销回调等）
	@RequestMapping("/sso/pushC")
	public Object ssoPushC(HttpServletRequest request) {

		// 将请求参数收集为 Map<String, String>
		Map<String, String> params = new LinkedHashMap<>();
		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			params.put(entry.getKey(), entry.getValue()[0]);
		}

		// 校验签名
		if (!SsoSignUtil.verifySign(params)) {
			return AjaxJson.getError("无效签名，拒绝应答");
		}

		// 按 msgType 分发处理
		String msgType = params.get("msgType");

		// 单点注销回调
		if ("logoutCall".equals(msgType)) {
			// 注销这个账号 id 在本 client 端的所有会话
			String loginId = params.get("loginId");
			for (HttpSession session : MyHttpSessionHolder.sessionList) {
				Object userId = session.getAttribute("userId");
				if (Objects.equals(String.valueOf(userId), loginId)) {
					session.removeAttribute("userId");
				}
			}
			return AjaxJson.getSuccess("账号id=" + loginId + " 注销成功");
		}

		// 其它消息类型
//		if("xxx".equals(msgType)) {
//			// 处理 xxx 消息
//		}

		return AjaxJson.getError("未知消息类型：" + msgType);
	}

	// 查询我的账号信息（调用 sso-server 端 userinfo 消息处理器）
	@RequestMapping("/sso/myInfo")
	public Object myInfo(HttpSession session) {
		// 如果尚未登录
		if (session.getAttribute("userId") == null) {
			return "尚未登录，无法获取";
		}

		Object loginId = session.getAttribute("userId");
		Map<String, String> params = new LinkedHashMap<>();
		params.put("msgType", "userinfo");
		params.put("client", SsoRequestUtil.clientId);
		params.put("loginId", String.valueOf(loginId));
		SsoSignUtil.addSignParams(params);
		String pushUrl = SsoRequestUtil.buildUrl(SsoRequestUtil.pushSUrl, params);
		AjaxJson result = SsoRequestUtil.request(pushUrl);

		// 返回给前端
		return result;
	}

	// 全局异常拦截
	@ExceptionHandler
	public AjaxJson handlerException(Exception e) {
		e.printStackTrace();
		return AjaxJson.getError(e.getMessage());
	}

}
