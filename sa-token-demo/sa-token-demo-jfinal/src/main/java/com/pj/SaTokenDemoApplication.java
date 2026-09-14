package com.pj;

import com.jfinal.server.undertow.UndertowServer;

import cn.dev33.satoken.SaManager;

/**
 * Sa-Token整合JFinal 示例
 * @author click33
 *
 */
public class SaTokenDemoApplication {

	public static void main(String[] args) {
		UndertowServer.create(SaTokenJfinalConfig.class)
			.setPort(8081)
			.addHotSwapClassPrefix("com.pj.")
			.start();
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}
