package com.pj.ws;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import cn.dev33.satoken.stp.StpUtil;

/**
 * WebSocket 握手的前置拦截器 
 * 
 * @author kong
 * @since: 2022-2-11
 */
public class WebSocketInterceptor implements HandshakeInterceptor {

	// 握手之前触发 (return true 才会握手成功 )
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler,
			Map<String, Object> attr) {
		
		System.out.println("---- 握手之前触发 " + StpUtil.getTokenValue());
		
		// 未登录情况下拒绝握手 
		if(StpUtil.isLogin() == false) {
			System.out.println("---- 未授权客户端，连接失败");
			return false;
		}
		
		// 标记 userId，握手成功 
		attr.put("userId", StpUtil.getLoginIdAsLong());
		return true;
	}

	// 握手之后触发 
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		System.out.println("---- 握手之后触发 ");
	}
	
}
