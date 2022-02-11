package com.pj.ws;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * WebSocket 连接测试 
 */
@Component
@ServerEndpoint("/ws-connect/{satoken}")
public class WebSocketConnect {

    /**
     * 固定前缀 
     */
    private static final String USER_ID = "user_id_";
	
	 /** 
	  * 存放Session集合，方便推送消息 （javax.websocket.Session）  
	  */
    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();
    
	// 监听：连接成功
	@OnOpen
	public void onOpen(Session session, @PathParam("satoken") String satoken) throws IOException {
		
		// 根据 token 获取对应的 userId 
		Object loginId = StpUtil.getLoginIdByToken(satoken);
		if(loginId == null) {
			session.close();
			throw new SaTokenException("连接失败，无效Token：" + satoken);
		}
		
		// put到集合，方便后续操作 
		long userId = SaFoxUtil.getValueByType(loginId, long.class);
		sessionMap.put(USER_ID + userId, session);
		
		// 给个提示 
		String tips = "Web-Socket 连接成功，sid=" + session.getId() + "，userId=" + userId;
		System.out.println(tips);
		sendMessage(session, tips);
	}

	// 监听: 连接关闭
	@OnClose
	public void onClose(Session session) {
		System.out.println("连接关闭，sid=" + session.getId());
		for (String key : sessionMap.keySet()) {
			if(sessionMap.get(key).getId().equals(session.getId())) {
				sessionMap.remove(key);
			}
		}
	}
	
	// 监听：收到客户端发送的消息 
	@OnMessage
	public void onMessage(Session session, String message) {
		System.out.println("sid为：" + session.getId() + "，发来：" + message);
	}
	
	// 监听：发生异常 
	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("sid为：" + session.getId() + "，发生错误");
		error.printStackTrace();
	}
	
	// ---------
	
	// 向指定客户端推送消息 
	public static void sendMessage(Session session, String message) {
		try {
			System.out.println("向sid为：" + session.getId() + "，发送：" + message);
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	// 向指定用户推送消息 
	public static void sendMessage(long userId, String message) {
		Session session = sessionMap.get(USER_ID + userId);
		if(session != null) {
			sendMessage(session, message);
		}
	}
	
}
