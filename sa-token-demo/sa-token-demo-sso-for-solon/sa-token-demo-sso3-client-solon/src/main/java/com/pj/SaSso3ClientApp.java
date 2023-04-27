package com.pj;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

@SolonMain
public class SaSso3ClientApp {

	public static void main(String[] args) {
		Solon.start(SaSso3ClientApp.class, args);
		System.out.println("\nSa-Token SSO模式三 Client端启动成功");
	}

}