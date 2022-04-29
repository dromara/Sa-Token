package com.pj.sso.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.stereotype.Component;

/**
 * 记录所有已创建的 HttpSession 对象 
 * 
 * <b> 此种方式有性能问题，仅做demo示例，真实项目中请更换为其它方案记录用户会话数据 </b>
 * 
 * @author kong
 * @date: 2022-4-30
 */
@Component
public class MyHttpSessionHolder implements HttpSessionListener {
	
	public static List<HttpSession> sessionList = new ArrayList<>();
	
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		sessionList.add(httpSessionEvent.getSession());
	}

	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		HttpSession session = httpSessionEvent.getSession();
		sessionList.remove(session);
	}

}
