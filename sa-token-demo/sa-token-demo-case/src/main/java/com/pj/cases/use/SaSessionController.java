package com.pj.cases.use;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pj.model.SysUser;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token Session会话示例 
 * 
 * @author click33
 * @since 2022-10-15
 */
@RestController
@RequestMapping("/session/")
public class SaSessionController {

	/*
	 * 前提：首先调用登录接口进行登录，代码在 com.pj.cases.use.LoginAuthController 中有详细解释，此处不再赘述 
	 * 		---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
	 */
	
	// 简单存取值   ---- http://localhost:8081/session/getValue
	@RequestMapping("getValue")
	public SaResult getValue() {
		// 获取当前登录账号的专属 SaSession 对象 
		// 		注意点1：只有登录后才可以调用这个方法 
		//		注意点2：每个账号获取到的都是不同的 SaSession 对象，存取值时不会互相影响 
		//		注意点3：SaSession 和 HttpSession 是两个完全不同的对象，不可混淆使用 
		SaSession session = StpUtil.getSession();
		
		// 存值
		session.set("name", "zhangsan");
		session.set("age", 18);
		
		// 取值 
		Object name = session.get("name"); 
		String name2 = session.getString("name");   // 取值，并转化为 String 数据类型  
		int age = session.getInt("age");	// 转 int 类型
		long age2 = session.getLong("age");	// 转 long 类型
		float age3 = session.getFloat("age");	// 转 float 类型
		double age4 = session.getDouble("age");	// 转 double 类型 
		int age5 = session.get("age5", 22);  // 取不到时就返回默认值 
		int age6 = session.get("age5", () -> {  // 取不到时就执行 lambda 获取值 
			return 26;
		});
		
		/*
		 * 存取值范围是一次会话有效的，也就是说，在一次登录有效期内，你可以在一个请求里存值，然后在另一个请求里取值 
		 */
		
		List<Object> list = Arrays.asList(name, name2, age, age2, age3, age4, age5, age6);
		System.out.println(list);
		
		return SaResult.data(list);
	}

	// 复杂存取值   ---- http://localhost:8081/session/getModel
	@RequestMapping("getModel")
	public SaResult getModel() {
		// 实例化  
		SysUser user = new SysUser();
		user.setId(10001);
		user.setName("张三");
		user.setAge(19);
		
		// 写入这个对象到 SaSession 中 
		StpUtil.getSession().set("user", user);
		
		// 然后我们就可以在任意代码处获取这个 user 了
		SysUser user2 = StpUtil.getSession().getModel("user", SysUser.class);
		
		// 返回
		return SaResult.data(user2);
	}
	
	// 自定义Session   ---- http://localhost:8081/session/customSession
	@RequestMapping("customSession")
	public SaResult customSession() {
		
		// 自定义 Session 就是指使用一个特定的 key，来获取 Session 对象 
		SaSession roleSession = SaSessionCustomUtil.getSessionById("role-1001");
		
		// 一样可以自由的存值写值 
		roleSession.set("nnn", "lalala");
		System.out.println(roleSession.get("nnn"));
		
		// 返回
		return SaResult.ok();
	}
	
}
