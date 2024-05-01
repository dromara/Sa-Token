package com.pj.sso;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pj.sso.util.AjaxJson;
import com.pj.sso.util.MyHttpSessionHolder;

/**
 * SSO Client端 Controller 
 * @author click33
 */
@RestController
public class SsoClientController {

	// SSO-Client端：首页
	@RequestMapping("/")
	public String index(HttpSession session) {
		String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
					"<p>当前会话登录账号：" + session.getAttribute("userId") + "</p>" + 
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
		if(session.getAttribute("userId") != null) {
			response.sendRedirect(back);
			return null;
		}
		
		/*
		 * 此时有两种情况: 
		 * 情况1：ticket无值，说明此请求是Client端访问，需要重定向至SSO认证中心 
		 * 情况2：ticket有值，说明此请求从SSO认证中心重定向而来，需要根据ticket进行登录 
		 */
		if(ticket == null) {
			String currUrl = request.getRequestURL().toString();
			String clientLoginUrl = currUrl + "?back=" + SsoRequestUtil.encodeUrl(back);
			String serverAuthUrl = SsoRequestUtil.authUrl + "?redirect=" + clientLoginUrl;
			response.sendRedirect(serverAuthUrl);
			return null;
		} else {
			// 获取当前 client 端的单点注销回调地址 
			String ssoLogoutCall = "";
			if(SsoRequestUtil.isSlo) {
				ssoLogoutCall = request.getRequestURL().toString().replace("/sso/login", "/sso/logoutCall"); 
			}
			
			// 校验 ticket
			String timestamp = String.valueOf(System.currentTimeMillis());	// 时间戳
			String nonce = SsoRequestUtil.getRandomString(20);		// 随机字符串
			String sign = SsoRequestUtil.getSignByTicket(ticket, ssoLogoutCall, timestamp, nonce);	// 参数签名
			String checkUrl = SsoRequestUtil.checkTicketUrl +
					"?timestamp=" + timestamp +
					"&nonce=" + nonce +
					"&sign=" + sign +
					"&ticket=" + ticket +
					"&ssoLogoutCall=" + ssoLogoutCall;
			AjaxJson result = SsoRequestUtil.request(checkUrl);
			
			// 200 代表校验成功 
			if(result.getCode() == 200 && SsoRequestUtil.isEmpty(result.getData()) == false) {
				// 登录上 
				Object loginId = result.getData();
				session.setAttribute("userId", loginId);
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
        if(session.getAttribute("userId") == null) {
			response.sendRedirect(back);
			return null;
        }
        
        // 调用 sso-server 认证中心单点注销API 
        Object loginId = session.getAttribute("userId");  // 账号id 
		String timestamp = String.valueOf(System.currentTimeMillis());	// 时间戳 
		String nonce = SsoRequestUtil.getRandomString(20);		// 随机字符串
		String sign = SsoRequestUtil.getSign(loginId, timestamp, nonce);	// 参数签名
		
        String url = SsoRequestUtil.sloUrl + 
        		"?loginId=" + loginId +
        		"&timestamp=" + timestamp +
        		"&nonce=" + nonce +
        		"&sign=" + sign;
        AjaxJson result = SsoRequestUtil.request(url);
        
		// 校验响应状态码，200 代表成功 
		if(result.getCode() == 200) {
			
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
	
	// SSO-Client端：单点注销回调地址
	@RequestMapping("/sso/logoutCall")
	public Object ssoLogoutCall(String loginId, String autoLogout, String timestamp, String nonce, String sign) {
		
		// 校验签名 
		String calcSign = SsoRequestUtil.getSignByLogoutCall(loginId, autoLogout, timestamp, nonce);
		if(calcSign.equals(sign) == false) {
			System.out.println("无效签名，拒绝应答：" + sign);
			return AjaxJson.getError("无效签名，拒绝应答" + sign);
		}
		
		// 注销这个账号id 
		for (HttpSession session: MyHttpSessionHolder.sessionList) {
			Object userId = session.getAttribute("userId");
			if(Objects.equals(String.valueOf(userId), loginId)) {
				session.removeAttribute("userId");
			}
		}
		
		return AjaxJson.getSuccess("账号id=" + loginId + " 注销成功");
	}

	// 查询我的账号信息 (调用此接口的前提是 sso-server 端开放了 /sso/userinfo 路由)
	@RequestMapping("/sso/myInfo")
	public Object myInfo(HttpSession session) {
		// 如果尚未登录 
		if(session.getAttribute("userId") == null) {
			return "尚未登录，无法获取";
		}

        // 组织 url 参数 
        Object loginId = session.getAttribute("userId");  // 账号id 
		String timestamp = String.valueOf(System.currentTimeMillis());	// 时间戳 
		String nonce = SsoRequestUtil.getRandomString(20);		// 随机字符串
		String sign = SsoRequestUtil.getSign(loginId, timestamp, nonce);	// 参数签名
		
        String url = SsoRequestUtil.getDataUrl +
        		"?loginId=" + loginId +
        		"&timestamp=" + timestamp +
        		"&nonce=" + nonce +
        		"&sign=" + sign;
        AjaxJson result = SsoRequestUtil.request(url);
        
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
