# SSO模式三 Http请求获取会话

如果既无法做到前端同域，也无法做到后端同Redis，那么可以使用模式三完成单点登录 

> 阅读本篇之前请务必先熟读SSO模式二！因为模式三仅仅属于模式二的一个特殊场景，熟读模式二有助于您快速理解本章内容


### 0、问题分析
我们先来分析一下，当后端不使用共享 Redis 时，会对架构产生哪些影响：

1. Client 端无法直连 Redis 校验 ticket，取出账号id。
2. Client 端无法与 Server 端共用一套会话，需要自行维护子会话。
3. 由于不是一套会话，所以无法“一次注销，全端下线”，需要额外编写代码完成单点注销。

所以模式三的主要目标：也就是在 模式二的基础上 解决上述 三个难题 

> 模式三的 Demo 示例地址：
> 
> - SSO-Server 端：`/sa-token-demo/sa-token-demo-sso3-server/` [源码链接](https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-sso3-server) <br/>
> - SSO-Client 端：`/sa-token-demo/sa-token-demo-sso3-client/` [源码链接](https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-sso3-client) <br/>
> 
> 如遇难点可参考示例


### 1、SSO-Server 认证中心开放 Ticket 校验接口
既然 Client 端无法直连 Redis 校验 Ticket，那我们就在 Server 端开放 Ticket 校验接口，然后 Client 端通过 http 请求获取数据。

#### 1.1、添加依赖
首先在 Server 端和 Client 端均添加以下依赖（如果不需要单点注销功能则 Server 端可不引入）
``` xml
<!-- Http请求工具 -->
<dependency>
	 <groupId>com.ejlchina</groupId>
	 <artifactId>okhttps</artifactId>
	 <version>3.1.1</version>
</dependency>
```
> OkHttps是一个轻量级http请求工具，详情参考：[OkHttps](https://gitee.com/ejlchina-zhxu/okhttps)

#### 1.2、认证中心开放接口
在 SSO-Server 端的 `application.yml` 中，新增以下配置：
``` yml
sa-token: 
    sso: 
        # 使用Http请求校验ticket 
        is-http: true
```
此配置项的作用是开放ticket校验接口，让Client端通过http请求获取会话

#### 1.3、Client端新增配置
在SSO-Client端的 `SsoClientController` 中，新增以下配置
``` java
// 配置SSO相关参数 
@Autowired
private void configSso(SaTokenConfig cfg) {
	cfg.sso
		// 配置 Http 请求处理器
		.setSendHttp(url -> {
			return OkHttps.sync(url).get().getBody().toString();
		})
		;
}
```

``` yml
sa-token: 
	sso: 
        # 使用Http请求校验ticket 
        is-http: true
		# SSO-Server端 ticket校验地址 
		check-ticket-url: http://sa-sso-server.com:9000/sso/checkTicket
```

#### 1.4、启动项目测试
启动SSO-Server、SSO-Client，访问测试：[http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)
> 注：如果已测试运行模式二，可先将Redis中的数据清空，以防旧数据对测试造成干扰


### 2、获取 Userinfo 
除了账号id，我们可能还需要将用户的昵称、头像等信息从 Server端 带到 Client端，即：用户资料的同步。要解决这个需求，我们只需：

#### 2.1、在 Server 端自定义接口，查询用户资料
``` java
// 自定义接口：获取userinfo 
@RequestMapping("/sso/userinfo")
public Object userinfo(String loginId, String secretkey) {
	System.out.println("---------------- 获取userinfo --------");
	
	// 校验调用秘钥 
	SaSsoUtil.checkSecretkey(secretkey);
	
	// 自定义返回结果（模拟）
	return SaResult.ok()
			.set("id", loginId)
			.set("name", "linxiaoyu")
			.set("sex", "女")
			.set("age", 18);
}
```

#### 2.2、在 Client 端调用此接口查询 userinfo
首先在yml中配置接口地址 
``` yml
sa-token: 
    sso: 
        # SSO-Server端 查询userinfo地址 
        userinfo-url: http://sa-sso-server.com:9000/sso/userinfo
```

然后在`SsoClientController`中新增接口 
``` java
// 查询我的账号信息 
@RequestMapping("/sso/myinfo")
public Object myinfo() {
	Object userinfo = SaSsoUtil.getUserinfo(StpUtil.getLoginId());
	System.out.println("--------info：" + userinfo);
	return userinfo;
}
```

访问测试：[http://sa-sso-client1.com:9001/sso/myinfo](http://sa-sso-client1.com:9001/sso/myinfo)



### 3、无刷单点注销

有了单点登录就必然要有单点注销，网上给出的大多数解决方案是将注销请求重定向至SSO-Server中心，逐个通知Client端下线

在某些场景下，页面的跳转可能造成不太好的用户体验，Sa-Token-SSO 允许你以 `REST API` 的形式构建接口，做到页面无刷新单点注销。

1. Client 端在校验 ticket 时，将注销回调地址发送到 Server 端。
2. Server 端将此 Client 的注销回调地址存储到 Set 集合。
3. Client 端向 Server 端发送单点注销请求。
4. Server 端遍历Set集合，逐个通知 Client 端下线。
5. Server 端注销下线。
6. 单点注销完成。

这些逻辑 Sa-Token 内部已经封装完毕，你只需按照文章增加以下配置即可：

#### 2.1、SSO-Server认证中心增加配置 
在 `SsoServerController` 中新增配置 
``` java
// 配置SSO相关参数 
@Autowired
private void configSso(SaTokenConfig cfg) {
	cfg.sso
		// ... (其它配置保持不变) 
		// 配置Http请求处理器
		.setSendHttp(url -> {
			// 此处为了提高响应速度这里可将sync换为async 
			return OkHttps.sync(url).get();
		})
		;
}
```

并在 `application.yml` 下新增配置：
``` yml
sa-token: 
	sso: 
        # 打开单点注销功能 
        is-slo: true
		# API调用秘钥（用于SSO模式三的单点注销功能）
		secretkey: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```

#### 2.2、SSO-Client 端新增配置 

在 `application.yml` 增加配置：`API调用秘钥` 和 `单点注销接口URL`。
``` yml
sa-token: 
	sso: 
        # 打开单点注销功能 
        is-slo: true
		# 单点注销地址 
		slo-url: http://sa-sso-server.com:9000/sso/logout
		# 接口调用秘钥 
		secretkey: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```

#### 2.3 启动测试 
启动SSO-Server、SSO-Client，访问测试：[http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)，
我们主要的测试点在于 `单点注销`，正常登录即可。

![sso-type3-client-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-client-index.png 's-w-sh')

点击 **`[注销]`** 按钮，即可单点注销成功。

<!-- ![sso-type3-slo.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-slo.png 's-w-sh') -->

![sso-type3-slo-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-slo-index.png 's-w-sh')

PS：这里我们为了方便演示，使用的是超链接跳页面的形式，正式项目中使用 Ajax 调用接口即可做到无刷单点登录退出。

例如，我们使用 [APIPost接口测试工具](https://www.apipost.cn/) 可以做到同样的效果：

![sso-slo-apipost.png](https://oss.dev33.cn/sa-token/doc/sso/sso-slo-apipost.png 's-w-sh')

测试完毕！




### 4、后记
当我们熟读三种模式的单点登录之后，其实不难发现：所谓单点登录，其本质就是多个系统之间的会话共享。

当我们理解这一点之后，三种模式的工作原理也浮出水面：

- 模式一：采用共享 Cookie 来做到前端 Token 的共享，从而达到后端的 Session 会话共享。
- 模式二：采用 URL 重定向，以 ticket 码为授权中介，做到多个系统间的会话传播。
- 模式三：采用 Http 请求主动查询会话，做到 Client 端与 Server 端的会话同步。




