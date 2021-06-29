package com.pj.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pj.util.AjaxJson;
import com.pj.util.Ttime;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 压力测试 
 * @author kong
 *
 */
@RestController
@RequestMapping("/s-test/")
public class StressTestController {

	// 测试   浏览器访问： http://localhost:8081/s-test/login 
	// 测试前，请先将 is-read-cookie 配置为 false
	@RequestMapping("login")
	public AjaxJson login() {
//			StpUtil.getTokenSession().logout();
//			StpUtil.logoutByLoginId(10001);

		int count = 10;	// 循环多少轮 
		int loginCount = 10000;	// 每轮循环多少次  
		
		// 循环10次 取平均时间 
		List<Double> list = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			System.out.println("\n---------------------第" + i + "轮---------------------");
			Ttime t = new Ttime().start();
			// 每次登录的次数
			for (int j = 1; j <= loginCount; j++) {
				StpUtil.login("1000" + j, "PC-" + j);
				if(j % 1000 == 0) {
					System.out.println("已登录：" + j);
				}
			}
			t.end();
			list.add((t.returnMs() + 0.0) / 1000);
			System.out.println("第" + i + "轮" + "用时：" + t.toString());
		}
//			System.out.println(((SaTokenDaoDefaultImpl)SaTokenManager.getSaTokenDao()).dataMap.size());
		
		System.out.println("\n---------------------测试结果---------------------");
		System.out.println(list.size() + "次测试: " + list);
		double ss = 0;
		for (int i = 0; i < list.size(); i++) {
			ss += list.get(i);
		}
		System.out.println("平均用时: " + ss / list.size());
		return AjaxJson.getSuccess();
	}
	
}
