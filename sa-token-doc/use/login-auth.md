# 登录认证

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/LoginAuthController.java"
	target="_blank">
	本章代码示例：Sa-Token 登录认证 —— [ sa-token-demo-case：com.pj.cases.LoginAuthController.java ]
</a>

--- 


### 设计思路

对于一些登录之后才能访问的接口（例如：查询我的账号资料），我们通常的做法是增加一层接口校验：

- 如果校验通过，则：正常返回数据。
- 如果校验未通过，则：抛出异常，告知其需要先进行登录。

那么，判断会话是否登录的依据是什么？我们先来简单分析一下登录访问流程：

1. 用户提交 `name` + `password` 参数，调用登录接口。
2. 登录成功，返回这个用户的 Token 会话凭证。
3. 用户后续的每次请求，都携带上这个 Token。
4. 服务器根据 Token 判断此会话是否登录成功。

所谓登录认证，指的就是服务器校验账号密码，为用户颁发 Token 会话凭证的过程，这个 Token 也是我们后续判断会话是否登录的关键所在。


<button class="show-img" img-src="https://oss.dev33.cn/sa-token/doc/g/g3--login-auth.gif">加载动态演示图</button>


### 登录与注销
根据以上思路，我们需要一个会话登录的函数：

``` java
// 会话登录：参数填写要登录的账号id，建议的数据类型：long | int | String， 不可以传入复杂类型，如：User、Admin 等等
StpUtil.login(Object id);	 
```

只此一句代码，便可以使会话登录成功，实际上，Sa-Token 在背后做了大量的工作，包括但不限于：

1. 检查此账号是否之前已有登录
2. 为账号生成 `Token` 凭证与 `Session` 会话
3. 通知全局侦听器，xx 账号登录成功
4. 将 `Token` 注入到请求上下文
5. 等等其它工作……

你暂时不需要完整的了解整个登录过程，你只需要记住关键一点：`Sa-Token 为这个账号创建了一个Token凭证，且通过 Cookie 上下文返回给了前端`。

所以一般情况下，我们的登录接口代码，会大致类似如下：

``` java
// 会话登录接口 
@RequestMapping("doLogin")
public SaResult doLogin(String name, String pwd) {
	// 第一步：比对前端提交的账号名称、密码
	if("zhang".equals(name) && "123456".equals(pwd)) {
		// 第二步：根据账号id，进行登录 
		StpUtil.login(10001);
		return SaResult.ok("登录成功");
	}
	return SaResult.error("登录失败");
}
```

如果你对以上代码阅读没有压力，你可能会注意到略显奇怪的一点：此处仅仅做了会话登录，但并没有主动向前端返回 Token 信息。
是因为不需要吗？严格来讲是需要的，只不过 `StpUtil.login(id)` 方法利用了 Cookie 自动注入的特性，省略了你手写返回 Token 的代码。

如果你对 Cookie 功能还不太了解，也不用担心，我们会在之后的 [ 前后端分离 ] 章节中详细的阐述 Cookie 功能，现在你只需要了解最基本的两点：

- Cookie 可以从后端控制往浏览器中写入 Token 值。
- Cookie 会在前端每次发起请求时自动提交 Token 值。

因此，在 Cookie 功能的加持下，我们可以仅靠 `StpUtil.login(id)` 一句代码就完成登录认证。

除了登录方法，我们还需要：

``` java
// 当前会话注销登录
StpUtil.logout();

// 获取当前会话是否已经登录，返回true=已登录，false=未登录
StpUtil.isLogin();

// 检验当前会话是否已经登录, 如果未登录，则抛出异常：`NotLoginException`
StpUtil.checkLogin();
```

异常 `NotLoginException` 代表当前会话暂未登录，可能的原因有很多：
前端没有提交 Token、前端提交的 Token 是无效的、前端提交的 Token 已经过期 …… 等等，可参照此篇：[未登录场景值](/fun/not-login-scene)，了解如何获取未登录的场景值。


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


### Token 查询
``` java
// 获取当前会话的token值
StpUtil.getTokenValue();

// 获取当前`StpLogic`的token名称
StpUtil.getTokenName();

// 获取指定token对应的账号id，如果未登录，则返回 null
StpUtil.getLoginIdByToken(String tokenValue);

// 获取当前会话剩余有效期（单位：s，返回-1代表永久有效）
StpUtil.getTokenTimeout();

// 获取当前会话的token信息参数
StpUtil.getTokenInfo();
```

有关`TokenInfo`参数详解，请参考：[TokenInfo参数详解](/fun/token-info)	


### 来个小测试，加深一下理解
新建 `LoginController`，复制以下代码
``` java
/**
 * 登录测试 
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
	
	// 查询 Token 信息  ---- http://localhost:8081/acc/tokenInfo
	@RequestMapping("tokenInfo")
	public SaResult tokenInfo() {
		return SaResult.data(StpUtil.getTokenInfo());
	}
	
	// 测试注销  ---- http://localhost:8081/acc/logout
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.logout();
		return SaResult.ok();
	}
	
}
```


---
<a class="dt-btn" href="https://www.wenjuan.ltd/s/UZBZJvb2ej/" target="_blank">本章小练习：Sa-Token 基础 - 登录认证，章节测试</a>

