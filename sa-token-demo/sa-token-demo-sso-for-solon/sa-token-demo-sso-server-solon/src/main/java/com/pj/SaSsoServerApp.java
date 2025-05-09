package com.pj;


import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.model.SaSsoClientInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

import java.util.ArrayList;
import java.util.List;

@SolonMain
public class SaSsoServerApp {

	public static void main(String[] args) {
		Solon.start(SaSsoServerApp.class, args);

		System.out.println();
		System.out.println("---------------------- Sa-Token SSO 统一认证中心启动成功 ----------------------");
		System.out.println("配置信息：" + SaSsoManager.getServerConfig());
		System.out.println("统一认证登录地址：http://sa-sso-server.com:9000/sso/auth");
		System.out.println("测试前需要根据官网文档修改 hosts 文件，测试账号密码：sa / 123456");
		System.out.println();

		SaSsoClientInfo sci = new SaSsoClientInfo();
		sci.setClient("client1");

		List<SaSsoClientInfo> list = new ArrayList<>();
		list.add(sci);

		StpUtil.getSessionByLoginId(10001).set("list", list);

		List<SaSsoClientInfo> list2 = (List)StpUtil.getSessionByLoginId(10001).get("list");
		for (SaSsoClientInfo info : list2) {
			System.out.println(info);
		}

	}
	
}