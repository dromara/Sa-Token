# 登录认证
--- 


### 核心思想

所谓登录认证，说白了就是限制某些API接口必须登录后才能访问（例：查询我的账号资料） <br>
那么如何判断一个会话是否登录？框架会在登录成功后给你做个标记，每次登录认证时校验这个标记，有标记者视为已登录，无标记者视为未登录！


### 登录与注销
根据以上思路，我们很容易想到以下api：

``` java
// 标记当前会话登录的账号id 
// 建议的参数类型：long | int | String， 不可以传入复杂类型，如：User、Admin等等
StpUtil.login(Object id);	 

// 当前会话注销登录
StpUtil.logout();

// 获取当前会话是否已经登录，返回true=已登录，false=未登录
StpUtil.isLogin();

// 检验当前会话是否已经登录, 如果未登录，则抛出异常：`NotLoginException`
StpUtil.checkLogin()
```

##### `NotLoginException`异常对象扩展：
1. 通过 `getLoginType()` 方法获取具体是哪个 `StpLogic` 抛出的异常 <br>
2. 通过 `getType()` 方法获取具体的场景值，详细参考章节：[未登录场景值](/fun/not-login-scene)


### 会话查询
``` java
// 获取当前会话账号id, 如果未登录，则抛出异常：`NotLoginException`
StpUtil.getLoginId();

// 类似查询API还有：
StpUtil.getLoginIdAsString();    // 获取当前会话账号id, 并转化为`String`类型
StpUtil.getLoginIdAsInt();       // 获取当前会话账号id, 并转化为`int`类型
StpUtil.getLoginIdAsLong();      // 获取当前会话账号id, 并转化为`long`类型

// ---------- 指定未登录情形下返回的默认值 ----------

// 获取当前会话账号id, 如果未登录，则返回null 
StpUtil.getLoginIdDefaultNull();

// 获取当前会话账号id, 如果未登录，则返回默认值 （`defaultValue`可以为任意类型）
StpUtil.getLoginId(T defaultValue);
```


### 其它API
``` java
// 获取指定token对应的账号id，如果未登录，则返回 null
StpUtil.getLoginIdByToken(String tokenValue);

// 获取当前`StpLogic`的token名称
StpUtil.getTokenName();

// 获取当前会话的token值
StpUtil.getTokenValue();

// 获取当前会话的token信息参数
StpUtil.getTokenInfo();
```


### 来个小测试，加深一下理解
新建 `LoginController`，复制以下代码
``` java
/**
 * 登录测试 
 * @author kong
 *
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

	// 测试登录  ---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
	@RequestMapping("doLogin")
	public SaResult doLogin(String name, String pwd) {
		// 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对 
		if("zhang".equals(name) && "123456".equals(pwd)) {
			StpUtil.login(10001);
			return SaResult.ok("登录成功");
		}
		return SaResult.error("登录失败");
	}

	// 查询登录状态  ---- http://localhost:8081/acc/isLogin
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		return SaResult.ok("是否登录：" + StpUtil.isLogin());
	}
	
	// 测试注销  ---- http://localhost:8081/acc/logout
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.logout();
		return SaResult.ok();
	}
	
}
```

> 有关`TokenInfo`参数详解，请参考：[TokenInfo参数详解](/fun/token-info)	



