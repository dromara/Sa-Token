package com.pj;


import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

/**
 * sa-token整合 solon 示例
 * @author noear
 *
 */
@SolonMain
public class SaTokenDemoApp {

	public static void main(String[] args) {
		Solon.start(SaTokenDemoApp.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());

		SaSession session = StpUtil.getSessionByLoginId(10001);
		session.set("name", "zhang");
		session.set("user", new SysUser(10001, "张三"));

		session = StpUtil.getSessionByLoginId(10001);
		System.out.println(session.get("name"));
		System.out.println(session.get("user"));
	}
}