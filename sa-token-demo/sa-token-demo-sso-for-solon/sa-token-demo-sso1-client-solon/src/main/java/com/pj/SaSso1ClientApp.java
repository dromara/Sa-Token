package com.pj;


import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

/**
 * SSO模式一，Client端 Demo 
 * @author click33
 *
 */
@SolonMain
public class SaSso1ClientApp {

	public static void main(String[] args) {
		Solon.start(SaSso1ClientApp.class, args);
		System.out.println("\nSa-Token SSO模式一 Client端启动成功");
	}
	
}