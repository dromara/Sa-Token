package com.pj;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.strategy.SaJsonStrategy;
import com.pj.poc.Snack4AutoTypeRcePoc;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

/**
 * Snack4 AutoType 反序列化白名单 — Solon 长期回归入口。
 */
@SolonMain
public class JsonTypingSecurityForSolonApp {

	public static void main(String[] args) {
		// 注册 Object 类型，将使白名单机制失效
		// SaJsonStrategy.instance.registerAllowType(Object.class);

		Solon.start(JsonTypingSecurityForSolonApp.class, args);
		System.out.println("\n启动成功：Sa-Token JSON 实现 = " + SaManager.getSaJsonTemplate().getClass().getName());
		System.out.println("Snack4 AutoType 白名单回归见 README。\n");

		// 攻击成功就会弹出计算器 (windows)
		Snack4AutoTypeRcePoc.run();
	}

}
