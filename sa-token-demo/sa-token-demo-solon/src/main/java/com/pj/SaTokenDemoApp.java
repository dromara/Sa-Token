package com.pj;


import cn.dev33.satoken.SaManager;
import org.noear.solon.Solon;

/**
 * sa-token整合 solon 示例
 * @author noear
 *
 */
public class SaTokenDemoApp {
	public static void main(String[] args) {
		Solon.start(SaTokenDemoApp.class, args);
		System.out.println("\n启动成功：sa-token配置如下：" + SaManager.getConfig());
	}
}