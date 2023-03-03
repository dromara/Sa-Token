package com.pj;


import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

@SolonMain
public class SaSsoServerApp {

	public static void main(String[] args) {
		Solon.start(SaSsoServerApp.class, args);
		System.out.println("\n------ Sa-Token-SSO 统一认证中心启动成功 ");
	}
	
}