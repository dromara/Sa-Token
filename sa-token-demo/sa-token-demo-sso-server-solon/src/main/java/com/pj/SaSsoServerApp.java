package com.pj;


import org.noear.solon.Solon;

public class SaSsoServerApp {

	public static void main(String[] args) {
		Solon.start(SaSsoServerApp.class, args);
		System.out.println("\n------ Sa-Token-SSO 统一认证中心启动成功 ");
	}
	
}