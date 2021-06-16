package com.pj.test;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pj.util.AjaxJson;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;

/**
 * 测试专用Controller 
 * @author kong
 *
 */
@RestController
@RequestMapping("/test/")
public class TestJwtController {

	

	// 测试登录接口， 浏览器访问： http://localhost:8081/test/login
	@RequestMapping("login")
	public AjaxJson login(@RequestParam(defaultValue="10001") String id) {
		System.out.println("======================= 进入方法，测试登录接口 ========================= ");
		System.out.println("当前会话的token：" + StpUtil.getTokenValue());
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号：" + StpUtil.getLoginIdDefaultNull());
		
		StpUtil.login(id);			// 在当前会话登录此账号 	
		System.out.println("登录成功");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号：" + StpUtil.getLoginId());
//		System.out.println("当前登录账号并转为int：" + StpUtil.getLoginIdAsInt());
		System.out.println("当前登录设备：" + StpUtil.getLoginDevice());
//		System.out.println("当前token信息：" + StpUtil.getTokenInfo());	
		
		return AjaxJson.getSuccess();
	}
	
	// 打印当前token信息， 浏览器访问： http://localhost:8081/test/tokenInfo
	@RequestMapping("tokenInfo")
	public AjaxJson tokenInfo() {
		System.out.println("======================= 进入方法，打印当前token信息 ========================= ");
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
		System.out.println(tokenInfo);
		return AjaxJson.getSuccessData(tokenInfo);
	}
	

	// 测试会话session接口， 浏览器访问： http://localhost:8081/test/session 
	@RequestMapping("session")
	public AjaxJson session() throws JsonProcessingException {
		System.out.println("======================= 进入方法，测试会话session接口 ========================= ");
		System.out.println("当前是否登录：" + StpUtil.isLogin());
		System.out.println("当前登录账号session的id" + StpUtil.getSession().getId());
		System.out.println("当前登录账号session的id" + StpUtil.getSession().getId());
		System.out.println("测试取值name：" + StpUtil.getSession().getAttribute("name"));
		StpUtil.getSession().setAttribute("name", new Date());	// 写入一个值 
		System.out.println("测试取值name：" + StpUtil.getSession().getAttribute("name"));
		System.out.println( new ObjectMapper().writeValueAsString(StpUtil.getSession()));
		return AjaxJson.getSuccess();
	}
	
	
	// 测试   浏览器访问： http://localhost:8081/test/test
	@RequestMapping("test")
	public AjaxJson test() {
		System.out.println();
		System.out.println("--------------进入请求--------------");
		StpUtil.login(10001);	
		System.out.println(StpUtil.getTokenInfo().getTokenValue());
		return AjaxJson.getSuccess();
	}


}
