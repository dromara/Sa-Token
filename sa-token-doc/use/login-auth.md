# 登录认证

--- 


### 1、开始登录

一个完整的登录认证包含哪些步骤？让我们代入用户视角：在打开 网站/APP 后，用户的操作流程大致可以概括为：
1. 打开 网站/APP，进入登录页。
2. 输入 账号+密码 进行登录。
3. 进入首页，进行业务相关操作。
4. 注销登录，关闭 网站/APP。

在整个流程中，Sa-Token 负责哪些部分呢？ 下图可以帮助你理解：

<img class="w-100" src="/big-file/doc/use/use-login-auth.svg" />

如上图所示：<green>**无论用户采用何种登录方式，本质上都是通过提交一定的认证信息，使系统可以定位到 Ta 的唯一标识 —— userId**</green>。

当我们拿到 userId 后，便可以调用框架提供的 API 进行登录：

``` java
// 会话登录：参数填写要登录的账号id，建议的数据类型：long | int | String， 不可以传入复杂类型，如：User、Admin 等等
StpUtil.login(Object userId);	 
```

只此一句代码，便可以使会话登录成功。实际上，Sa-Token 在背后做了大量的工作，包括但不限于：
 
1. 检查此账号是否之前已有登录；
2. 为账号生成 Token 凭证与 Session 会话；
3. 记录 Token 活跃时间；
4. 通知全局侦听器，xx 账号登录成功；
5. 检查此账号登录数量是否已达上限；
6. 将 Token 注入到请求上下文；
7. 等等其它工作…… 

你暂时不需要完整了解完整过程，你只需要记住关键一点：<green>**Sa-Token 为这个账号创建了一个 token 凭证，且通过 Cookie 上下文返回给了前端**</green>。

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

如果你对以上代码阅读没有压力，你可能会注意到略显奇怪的一点：<green>**此处仅仅做了会话登录，但并没有主动向前端返回 token 信息。**</green>

是因为不需要吗？严格来讲是需要的，只不过 `StpUtil.login(id)` 方法利用了 Cookie 自动注入的特性，省略了你手写返回 token 的代码。

> [!TIP| label:Cookie 是什么？] 
> 如果你对 Cookie 功能还不太了解，也不用担心，我们会在之后的 [ 前后端分离 ] 章节中详细的阐述 Cookie 功能，现在你只需要了解最基本的两点：
> 
> - Cookie 可以从后端控制往浏览器中写入 token 值。
> - Cookie 会在前端每次发起请求时自动提交 token 值。
> 
> 因此，在 Cookie 功能的加持下，我们可以仅靠 `StpUtil.login(id)` 一句代码就完成登录认证。
> 
> 在浏览器打开 f12 控制台，即可看到被注入的 Cookie 值：
> 
> <button class="show-img" img-src="/big-file/doc/use/sa-login-cookie-pre.png">加载演示图</button>


### 2、校验是否登录

对于一些登录之后才能访问的接口（例如：查询我的账号资料），我们通常的做法是增加一层接口校验：

- 如果校验通过，则：<green>正常返回数据。</green>
- 如果校验未通过，则：<red>抛出异常，告知其需要先进行登录。</red>

<img class="w-100" src="/big-file/doc/use/use-login-check.svg" />

<!-- <button class="show-img" img-src="/big-file/doc/use/g3--login-auth.gif">加载动态演示图</button> -->

使用以下方法判断当前会话是否已登录：

``` java
// 判断当前会话是否已经登录，返回 true=已登录，false=未登录
StpUtil.isLogin();

// 检验当前会话是否已经登录, 如果已登录代码会安全通过，未登录则抛出异常：`NotLoginException`
StpUtil.checkLogin();
```

例如我们可以在接口内，根据是否登录返回不同的信息：

``` java
// 获取我的资料信息 
@RequestMapping("myInfo")
public String myInfo() {
	if( StpUtil.isLogin() ) {
		// ... 
		return "我的资料信息...";
	} else {
		return "未登录，请先登录";
	}
}
```

或者在未登录时直接抛出全局异常：

``` java
// 获取我的资料信息 
@RequestMapping("myInfo")
public String myInfo() {
	StpUtil.checkLogin();  // 如果当前未登录，这句代码会直接抛出异常 `NotLoginException`
	return "我的资料信息";
}
```

配合全局异常处理器，统一返回固定格式数据到前端：
``` java
@RestControllerAdvice
public class GlobalException {
	@ExceptionHandler(NotLoginException.class)
	public SaResult handlerException(NotLoginException e) {
		return SaResult.error(e.getMessage());
	}
}
```

异常 <red>`NotLoginException`</red> 代表当前会话暂未登录，可能的原因有很多：
- 前端没有提交 token。
- 前端提交的 token 是无效的。
- 前端提交的 token 已经过期。 
- …… 

可参照此篇：[未登录场景值](/fun/not-login-scene)，了解如何获取未登录的场景值。


### 3、会话查询

如果你想要获取当前登录的是谁：

``` java
// 获取当前会话账号id, 如果未登录，则抛出异常：`NotLoginException`
StpUtil.getLoginId();

// 类似查询API还有：
StpUtil.getLoginIdAsString();    // 获取当前会话账号id, 并转化为`String`类型
StpUtil.getLoginIdAsInt();       // 获取当前会话账号id, 并转化为`int`类型
StpUtil.getLoginIdAsLong();      // 获取当前会话账号id, 并转化为`long`类型

// ---------- 以下方法可以指定未登录情形下返回的默认值 ----------

// 获取当前会话账号id, 如果未登录，则返回 null 
StpUtil.getLoginIdDefaultNull();

// 获取当前会话账号id, 如果未登录，则返回默认值 （`defaultValue`可以为任意类型）
StpUtil.getLoginId(T defaultValue);
```


### 4、token 查询
``` java
// 获取当前会话的 token 值
StpUtil.getTokenValue();

// 获取当前`StpLogic`的 token 名称
StpUtil.getTokenName();

// 获取指定 token 对应的账号id，如果未登录，则返回 null
StpUtil.getLoginIdByToken(String tokenValue);

// 获取当前会话剩余有效期（单位：s，返回-1代表永久有效）
StpUtil.getTokenTimeout();

// 获取当前会话的 token 信息参数
StpUtil.getTokenInfo();
```

有关`TokenInfo`参数详解，请参考：[TokenInfo参数详解](/fun/token-info)	


### 5、会话注销
``` java
// 当前会话注销登录
StpUtil.logout();
```


### 6、来个小测试，加深一下理解
新建 `LoginController`，复制或手动敲出以下代码
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

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/use/LoginAuthController.java"
	target="_blank">
	本章代码示例：Sa-Token 登录认证 —— [ LoginAuthController.java ]
</a>
<a class="dt-btn" href="https://www.wenjuan.ltd/s/UZBZJvb2ej/" target="_blank">本章小练习：Sa-Token 基础 - 登录认证，章节测试</a>









