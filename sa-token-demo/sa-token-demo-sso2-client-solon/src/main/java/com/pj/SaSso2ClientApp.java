package com.pj;


import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

@SolonMain
public class SaSso2ClientApp {

	public static void main(String[] args) {
		Solon.start(SaSso2ClientApp.class, args);
		System.out.println("\nSa-Token SSO模式二 Client端启动成功");
	}
	
}