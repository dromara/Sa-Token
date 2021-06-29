# SSO模式三 Http请求获取会话

如果既无法做到前端同域，也无法做到后端同Redis，那么可以使用模式三完成单点登录 

> 阅读本篇之前请务必先熟读SSO模式二！因为模式三仅仅属于模式二的一个特殊场景，熟读模式二有助于您快速理解本章内容


### 0、问题分析
我们先来分析一下，当后端不使用共享Redis时，会对架构发生哪些影响 

1. Client端 无法直连 Redis 校验 ticket，取出账号id 
2. Client端 无法与 Server端 共用一套会话，需要自行维护子会话
3. 由于不是一套会话，所以无法“一次注销，全端下线”，需要额外编写代码完成单点注销

所以模式三的主要目标：也就是在 模式二的基础上 解决上述 三个难题 

> 模式三的Demo示例地址：<br/>
> SSO-Server端： `/sa-token-demo/sa-token-demo-sso3-server/` [源码链接](https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-sso3-server) <br/>
> SSO-Client端： `/sa-token-demo/sa-token-demo-sso3-client/` [源码链接](https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-sso3-client) <br/>
> 如遇难点可参考示例


### 1、SSO-Server认证中心开放ticket校验接口
既然Client端无法直连Redis校验ticket，那就在Server端开放ticket校验接口，然后Client端通过http请求获取数据

##### 1.1、添加依赖
首先在Server端和Client端均添加以下依赖（如果不需要单点注销功能则Server端可不引入）
``` xml
<!-- Http请求工具 -->
<dependency>
	 <groupId>com.ejlchina</groupId>
	 <artifactId>okhttps</artifactId>
	 <version>3.1.1</version>
</dependency>
```
> OkHttps是一个轻量级http请求工具，详情参考：[OkHttps](https://gitee.com/ejlchina-zhxu/okhttps)

##### 1.2、认证中心开放接口
在SSO-Server端的`SsoServerController`中，新增以下接口：
``` java
// SSO-Server端：校验ticket 获取账号id 
@RequestMapping("checkTicket")
public Object checkTicket(String ticket, String sloCallback) {
	// 校验ticket，获取对应的账号id 
	Object loginId = SaSsoUtil.checkTicket(ticket);
	
	// 注册此客户端的单点注销回调URL（不需要单点注销功能可删除此行代码）
	SaSsoUtil.registerSloCallbackUrl(loginId, sloCallback);
	
	// 返回给Client端 
	return loginId;
}
```
此接口的作用是让Client端通过http请求校验ticket，获取对应的账号id

##### 1.3、Client端新增配置
``` yml
spring: 
    sa-token: 
        sso: 
            # SSO-Server端 ticket校验地址 
            check-ticket-url: http://sa-sso-server.com:9000/checkTicket
```

##### 1.4、修改校验ticket的逻辑 
在模式二的`SsoClientController`中，校验ticket的方法是：
``` java
// SSO-Client端：校验ticket，获取账号id 
private Object checkTicket(String ticket) {
	return SaSsoUtil.checkTicket(ticket);
}
```
不能直连Redis后，上述方法也将无效，我们把它改为以下方式：
``` java
// SSO-Client端：校验ticket码，获取对应的账号id 
private Object checkTicket(String ticket) {
	// 构建单点注销的回调URL（不需要单点注销时此值可填null ）
	String sloCallback = SaHolder.getRequest().getUrl().replace("/ssoLogin", "/sloCallback");
	
	// 使用OkHttps请求SSO-Server端，校验ticket 
	String checkUrl = SaSsoUtil.buildCheckTicketUrl(ticket, sloCallback);
	String loginId = OkHttps.sync(checkUrl).get().getBody().toString();
	
	// 判断返回值是否为有效账号Id
	return (SaFoxUtil.isEmpty(loginId) ? null : loginId);
}
```

##### 1.5 启动项目测试
启动SSO-Server、SSO-Client，访问测试：[http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)
> 注：如果已测试运行模式二，可先将Redis中的数据清空，以防旧数据对测试造成干扰


### 2、无刷单点注销

有了单点登录就必然要有单点注销，网上给出的大多数解决方案是将注销请求重定向至SSO-Server中心，逐个通知Client端下线

在某些场景下，页面的跳转可能造成不太好的用户体验，Sa-Token-SSO 允许你以 `REST API` 的形式构建接口，做到页面无刷新单点注销

1. Client端校验ticket的时候将注销回调地址发送到Server端
2. Server端将注销回调地址存储到Set集合
3. Client端向Server端发送单点注销请求
4. Server端遍历Set集合，逐个通知Client端下线
5. Server端注销下线
6. 单点注销完成

##### 2.1、SSO-Server认证中心增加单点注销接口
新建 `SsoServerLogoutController` 增加以下代码 
``` java
/**
 * Sa-Token-SSO Server端 单点注销 Controller 
 */
@RestController
public class SsoServerLogoutController {

	// SSO-Server端：单点注销
	@RequestMapping("ssoLogout")
	public String ssoLogout(String loginId, String secretkey) {
		
		// 遍历通知Client端注销会话 (为了提高响应速度这里可将sync换为async)
		SaSsoUtil.singleLogout(secretkey, loginId, url -> OkHttps.sync(url).get());
		
		// 完成
		return "ok";
	}
	
}
```

并在 `application.yml` 下配置API调用秘钥
``` yml
spring: 
    sa-token: 
        sso: 
            # API调用秘钥（用于SSO模式三的单点注销功能）
            secretkey: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```

##### 2.2、SSO-Client端增加注销接口
新建 `SsoClientLogoutController` 增加以下代码 
``` java
/**
 * Sa-Token-SSO Client端 单点注销 Controller 
 * @author kong
 */
@RestController
public class SsoClientLogoutController {

	// SSO-Client端：单端注销 (其它Client端会话不受影响)
	@RequestMapping("logout")
	public AjaxJson logout() {
		StpUtil.logout();
		return AjaxJson.getSuccess();
	}
	
	// SSO-Client端：单点注销 (所有端一起下线)
	@RequestMapping("ssoLogout")
	public AjaxJson ssoLogout() {
		// 如果未登录，则无需注销 
		if(StpUtil.isLogin() == false) {
			return AjaxJson.getSuccess();
		}
		// 调用SSO-Server认证中心API 
		String url = SaSsoUtil.buildSloUrl(StpUtil.getLoginId());
		String res = OkHttps.sync(url).get().getBody().toString();
		if(res.equals("ok")) {
			return AjaxJson.getSuccess("单点注销成功");
		}
		return AjaxJson.getError("单点注销失败"); 
	}
	
	// 单点注销的回调
	@RequestMapping("sloCallback")
	public String sloCallback(String loginId, String secretkey) {
		SaSsoUtil.checkSecretkey(secretkey);
		StpUtil.logoutByLoginId(loginId);
		return "ok";
	}
	
}
```

并在 `application.yml` 增加配置： API调用秘钥 和 单点注销接口URL
``` yml
spring: 
    sa-token: 
        sso: 
            # SSO-Server端 单点注销地址 
            slo-url: http://sa-sso-server.com:9000/ssoLogout
            # 接口调用秘钥（用于SSO模式三的单点注销功能）
            secretkey: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```

##### 2.3 更改Client端首页代码
为了方便测试，我们更改一下Client端中`SsoClientController`类的`index`方法代码
``` java
// SSO-Client端：首页
@RequestMapping("/")
public String index() {
	String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
				"<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" + 
				"<p><a href=\"javascript:location.href='/ssoLogin?back=' + encodeURIComponent(location.href);\">登录</a>" + 
				" <a href='/ssoLogout' target='_blank'>注销</a></p>";
	return str;
}
```
PS：相比于模式二，增加了单点注销的按钮


##### 2.4 启动测试 
启动SSO-Server、SSO-Client，访问测试：[http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)，
我们主要的测试点在于 `单点注销`，正常登陆即可

![sso-type3-client-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-client-index.png 's-w-sh')

点击 **`[注销]`** 按钮，即可单点注销成功 

![sso-type3-slo.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-slo.png 's-w-sh')

![sso-type3-slo-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-slo-index.png 's-w-sh')

PS：这里我们为了方便演示，使用的是超链接跳页面的形式，正式项目中使用Ajax调用接口即可做到无刷单点登录退出

例如我们使用 [APIPost接口测试工具](https://www.apipost.cn/) 可以做到同样的效果：

![sso-slo-apipost.png](https://oss.dev33.cn/sa-token/doc/sso/sso-slo-apipost.png 's-w-sh')

测试完毕！




### 3、后记
当我们熟读三种模式的单点登录之后，其实不难发现：所谓单点登录，其本质就是多个系统之间的会话共享 

当我们理解这一点之后，三种模式的工作原理也浮出水面：

- 模式一：采用共享Cookie来做到前端Token的共享，从而达到后端的Session会话共享
- 模式二：采用URL重定向，以ticket码为授权中介，做到多个系统间的会话传播
- 模式三：采用Http请求主动查询会话，做到Client端与Server端的会话同步 




