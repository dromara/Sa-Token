package com.pj.cases.use;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 登录认证示例 
 * 
 * @author click33
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/acc/")
public class LoginAuthController {

	// 会话登录接口  ---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
	@RequestMapping("doLogin")
	public SaResult doLogin(String name, String pwd) {
		
		// 第一步：比对前端提交的 账号名称 & 密码 是否正确，比对成功后开始登录 
		// 		此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对 
		if("zhang".equals(name) && "123456".equals(pwd)) {
			
			// 第二步：根据账号id，进行登录 
			// 		此处填入的参数应该保持用户表唯一，比如用户id，不可以直接填入整个 User 对象 
			StpUtil.login(10001);
			
			// SaResult 是 Sa-Token 中对返回结果的简单封装，下面的示例将不再赘述 
			return SaResult.ok("登录成功");
		}
		
		return SaResult.error("登录失败");
	}

	// 查询当前登录状态  ---- http://localhost:8081/acc/isLogin
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		// StpUtil.isLogin() 查询当前客户端是否登录，返回 true 或 false 
		boolean isLogin = StpUtil.isLogin();
		return SaResult.ok("当前客户端是否登录：" + isLogin);
	}

	// 校验当前登录状态  ---- http://localhost:8081/acc/checkLogin
	@RequestMapping("checkLogin")
	public SaResult checkLogin() {
		// 检验当前会话是否已经登录, 如果未登录，则抛出异常：`NotLoginException`
		StpUtil.checkLogin();

		// 抛出异常后，代码将走入全局异常处理（GlobalException.java），如果没有抛出异常，则代表通过了登录校验，返回下面信息 
		return SaResult.ok("校验登录成功，这行字符串是只有登录后才会返回的信息");
	}

	// 获取当前登录的账号是谁  ---- http://localhost:8081/acc/getLoginId
	@RequestMapping("getLoginId")
	public SaResult getLoginId() {
		// 需要注意的是，StpUtil.getLoginId() 自带登录校验效果
		// 也就是说如果在未登录的情况下调用这句代码，框架就会抛出 `NotLoginException` 异常，效果和 StpUtil.checkLogin() 是一样的 
		Object userId = StpUtil.getLoginId();
		System.out.println("当前登录的账号id是：" + userId);
		
		// 如果不希望 StpUtil.getLoginId() 触发登录校验效果，可以填入一个默认值
		// 如果会话未登录，则返回这个默认值，如果会话已登录，将正常返回登录的账号id 
		Object userId2 = StpUtil.getLoginId(0);
		System.out.println("当前登录的账号id是：" + userId2);
		
		// 或者使其在未登录的时候返回 null 
		Object userId3 = StpUtil.getLoginIdDefaultNull();
		System.out.println("当前登录的账号id是：" + userId3);
		
		// 类型转换：
		// StpUtil.getLoginId() 返回的是 Object 类型，你可以使用以下方法指定其返回的类型 
		int userId4 = StpUtil.getLoginIdAsInt();  // 将返回值转换为 int 类型 
		long userId5 = StpUtil.getLoginIdAsLong();  // 将返回值转换为 long 类型 
		String userId6 = StpUtil.getLoginIdAsString();  // 将返回值转换为 String 类型 
		
		// 疑问：数据基本类型不是有八个吗，为什么只封装以上三种类型的转换？
		// 因为大多数项目都是拿 int、long 或 String 声明 UserId 的类型的，实在没见过哪个项目用 double、float、boolean 之类来声明 UserId 
		System.out.println("当前登录的账号id是：" + userId4 + " --- " + userId5 + " --- " + userId6);
		
		// 返回给前端 
		return SaResult.ok("当前客户端登录的账号id是：" + userId);
	}

	// 查询 Token 信息  ---- http://localhost:8081/acc/tokenInfo
	@RequestMapping("tokenInfo")
	public SaResult tokenInfo() {
		// TokenName 是 Token 名称的意思，此值也决定了前端提交 Token 时应该使用的参数名称 
		String tokenName = StpUtil.getTokenName();
		System.out.println("前端提交 Token 时应该使用的参数名称：" + tokenName);
		
		// 使用 StpUtil.getTokenValue() 获取前端提交的 Token 值 
		// 框架默认前端可以从以下三个途径中提交 Token：
		// 		Cookie 		（浏览器自动提交）
		// 		Header头	（代码手动提交）
		// 		Query 参数	（代码手动提交） 例如： /user/getInfo?satoken=xxxx-xxxx-xxxx-xxxx 
		// 读取顺序为： Query 参数 --> Header头 -- > Cookie 
		// 以上三个地方都读取不到 Token 信息的话，则视为前端没有提交 Token 
		String tokenValue = StpUtil.getTokenValue();
		System.out.println("前端提交的Token值为：" + tokenValue);
		
		// TokenInfo 包含了此 Token 的大多数信息 
		SaTokenInfo info = StpUtil.getTokenInfo();
		System.out.println("Token 名称：" + info.getTokenName());
		System.out.println("Token 值：" + info.getTokenValue());
		System.out.println("当前是否登录：" + info.getIsLogin());
		System.out.println("当前登录的账号id：" + info.getLoginId());
		System.out.println("当前登录账号的类型：" + info.getLoginType());
		System.out.println("当前登录客户端的设备类型：" + info.getLoginDevice());
		System.out.println("当前 Token 的剩余有效期：" + info.getTokenTimeout()); // 单位：秒，-1代表永久有效，-2代表值不存在
		System.out.println("当前 Token 的剩余临时有效期：" + info.getTokenActivityTimeout()); // 单位：秒，-1代表永久有效，-2代表值不存在
		System.out.println("当前 User-Session 的剩余有效期" + info.getSessionTimeout()); // 单位：秒，-1代表永久有效，-2代表值不存在
		System.out.println("当前 Token-Session 的剩余有效期" + info.getTokenSessionTimeout()); // 单位：秒，-1代表永久有效，-2代表值不存在
		
		// 返回给前端 
		return SaResult.data(StpUtil.getTokenInfo());
	}
	
	// 会话注销  ---- http://localhost:8081/acc/logout
	@RequestMapping("logout")
	public SaResult logout() {
		// 退出登录会清除三个地方的数据：
		// 		1、Redis中保存的 Token 信息
		// 		2、当前请求上下文中保存的 Token 信息 
		// 		3、Cookie 中保存的 Token 信息（如果未使用Cookie模式则不会清除）
		StpUtil.logout();
		
		// StpUtil.logout() 在未登录时也是可以调用成功的，
		// 也就是说，无论客户端有没有登录，执行完 StpUtil.logout() 后，都会处于未登录状态 
		System.out.println("当前是否处于登录状态：" + StpUtil.isLogin());
		
		// 返回给前端 
		return SaResult.ok("退出登录成功");
	}
	
}
