# SSO模式三 Http请求获取会话

如果既无法做到前端同域，也无法做到后端同Redis，那么可以使用模式三完成单点登录 

> [!WARNING| label:小提示] 
> 阅读本篇之前请务必先熟读SSO模式二！因为模式三仅仅属于模式二的一个特殊场景，熟读模式二有助于您快速理解本章内容


### 1、问题分析
我们先来分析一下，当后端不使用共享 Redis 时，会对架构产生哪些影响：

1. Client 端无法直连 Redis 校验 ticket，取出账号id。
2. Client 端无法与 Server 端共用一套会话，需要自行维护子会话。
3. 由于不是一套会话，所以无法“一次注销，全端下线”，需要额外编写代码完成单点注销（其实此处的“额外编写代码”已在SSO模式二“无刷单点注销”部分介绍完毕）。

所以模式三的主要目标：也就是在 模式二的基础上 解决上述 三个难题 

> [!TIP| label:demo | style:callout] 
> 模式三的 Demo 示例地址：`/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso3-client/` 
> [源码链接](https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso3-client)，如遇难点可参考示例 


### 2、在Client 端更改 Ticket 校验方式


#### 2.1、增加 pom.xml 配置 

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml
<!-- Http请求工具 -->
<dependency>
	<groupId>com.dtflys.forest</groupId>
	<artifactId>forest-spring-boot-starter</artifactId>
	<version>1.5.26</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Http请求工具
implementation 'com.dtflys.forest:forest-spring-boot-starter:1.5.26'
```
<!---------------------------- tabs:end ---------------------------->

Forest 是一个轻量级 http 请求工具，详情参考：[Forest](https://forest.dtflyx.com/)


#### 2.2、SSO-Client 端新增配置：API调用秘钥

在 `application.yml` 增加：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
    sso-client: 
        # 打开模式三（使用Http请求校验ticket）
        is-http: true
    sign:
        # API 接口调用秘钥
        secret-key: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
		
forest: 
	# 关闭 forest 请求日志打印
	log-enabled: false
```
<!------------- tab:properties 风格  ------------->
``` properties
# 打开模式三（使用Http请求校验ticket）
sa-token.sso-client.is-http=true
# 接口调用秘钥 
sa-token.sign.secret-key=kQwIOrYvnXmSDkwEiFngrKidMcdrgKor

# 关闭 forest 请求日志打印
forest.log-enabled=false
```
<!---------------------------- tabs:end ---------------------------->

因为我们已经在控制台手动打印 url 请求日志了，所以此处 `forest.log-enabled=false` 关闭 Forest 框架自身的日志打印，这不是必须的，你可以将其打开。

注意 secretkey 秘钥需要与SSO认证中心的一致 

#### 2.3、SSO-Client 配置 http 请求处理器
``` java
// 配置SSO相关参数
@Autowired
private void configSso(SaSsoClientConfig ssoClient) {
	// 配置Http请求处理器
	ssoClient.sendHttp = url -> {
		System.out.println("------ 发起请求：" + url);
		String resStr = Forest.get(url).executeAsString();
		System.out.println("------ 请求结果：" + resStr);
		return resStr;
	};
}
```


#### 2.4、测试

重启项目，访问测试：
- [http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)
- [http://sa-sso-client2.com:9001/](http://sa-sso-client2.com:9001/)
- [http://sa-sso-client3.com:9001/](http://sa-sso-client3.com:9001/)

> [!WARNING| label:小提示] 
> 注：如果已测试运行模式二，可先将Redis中的数据清空，以防旧数据对测试造成干扰






### 3、获取 UserInfo 
除了账号id，我们可能还需要将用户的昵称、头像等信息从 Server端 带到 Client端，即：用户资料的拉取。

在模式二中我们只需要将需要同步的资料放到 SaSession 即可，但是在模式三中两端不再连接同一个 Redis，这时候我们需要通过 http 接口来同步信息。

在旧版本`（<= v1.34.0）` 框架提供的方案是配置 getUserinfo 接口地址，从 client 调用拉取数据，该方案有以下缺点：
- 每次调用只能传递固定 loginId 一个参数，不方便。
- 只能拉取 userinfo 数据，不通用。
- 如果还需要拉取其它业务数据，需要再自定义一个接口，比较麻烦。

为此，我们设计了更通用、灵活的 getData 接口，解决上述三个难题。

#### 3.1、首先在 Server 端开放一个查询数据的接口

``` java
// 示例：获取数据接口（用于在模式三下，为 client 端开放拉取数据的接口）
@RequestMapping("/sso/getData")
public SaResult getData(String apiType, String loginId) {
	System.out.println("---------------- 获取数据 ----------------");
	System.out.println("apiType=" + apiType);
	System.out.println("loginId=" + loginId);

	// 校验签名：只有拥有正确秘钥发起的请求才能通过校验
	SaSignUtil.checkRequest(SaHolder.getRequest());

	// 自定义返回结果（模拟）
	return SaResult.ok()
			.set("id", loginId)
			.set("name", "LinXiaoYu")
			.set("sex", "女")
			.set("age", 18);
}
```

> [!WARNING| label:小提示] 
> 如果配置了 “不同 client 不同秘钥” 模式，则需要将上述的： <br>
> &emsp;&emsp;SaSignUtil.checkRequest(SaHolder.getRequest());  <br>
> 
> 改为以下方式： <br>
> &emsp;&emsp;String client = SaHolder.getRequest().getHeader("client"); <br>
> &emsp;&emsp;SaSsoServerProcessor.instance.ssoServerTemplate.getSignTemplate(client).checkRequest(SaHolder.getRequest()); <br>
> 
> 如果没有配置 “不同 client 不同秘钥” 模式，则请忽略本条提示。


#### 3.2、在 Client 端调用此接口查询数据

在 `SsoClientController` 中新增接口 
``` java
// 查询我的账号信息 
@RequestMapping("/sso/myInfo")
public Object myInfo() {
	// 组织请求参数
	Map<String, Object> map = new HashMap<>();
	map.put("apiType", "userinfo");
	map.put("loginId", StpUtil.getLoginId());

	// 发起请求
	Object resData = SaSsoUtil.getData(map);
	System.out.println("sso-server 返回的信息：" + resData);
	return resData;
}
```

#### 3.3、访问测试
访问测试：[http://sa-sso-client1.com:9001/sso/myInfo](http://sa-sso-client1.com:9001/sso/myInfo)


### 4、自定义接口通信

上述示例展示在 client 端向 server 拉取 userinfo 数据的步骤，如果你还需要拉取其它业务的数据，稍加改造示例便可以实现。

#### 4.1、方式一，使用 apiType 参数来区分业务

我们可以约定好，使用 apiType 来区分不同的业务，例如：
- 当 `apiType=userinfo` 时：代表拉取用户资料。
- 当 `apiType=followList` 时：代表拉取用户的关注列表。
- 当 `apiType=fansList` 时：代表拉取用户的粉丝列表。

此时，我们便可以通过在 client 端传入不同的 apiType 参数，来区分不同的业务。

``` java
// 查询我的账号信息 
@RequestMapping("/sso/myFollowList")
public Object myFollowList() {
	// 组织请求参数
	Map<String, Object> map = new HashMap<>();
	map.put("apiType", "followList");  // 关键代码，代表本次我要拉取关注列表
	map.put("loginId", StpUtil.getLoginId());

	// 发起请求
	Object resData = SaSsoUtil.getData(map);
	System.out.println("sso-server 返回的信息：" + resData);
	return resData;
}
```

然后在 server 端我们通过不同的 apiType 值，返回不同的信息即可。


#### 4.2、方式二：直接在调用接口时传入一个自定义 path 

我们可以 client 端，调用 `SaSsoUtil.getData` 方法时，传入一个自定义 path，例如：

``` java
// 查询我的账号信息 
@RequestMapping("/sso/myFansList")
public Object myFansList() {
	// 组织请求参数
	Map<String, Object> map = new HashMap<>();
	// map.put("apiType", "userinfo");   // 此时已经不需要 apiType 参数了 
	map.put("loginId", StpUtil.getLoginId());

	// 发起请求 （传入自定义的 path 地址）
	Object resData = SaSsoUtil.getData("/sso/getFansList", map);
	System.out.println("sso-server 返回的信息：" + resData);
	return resData;
}
```

同时，我们需要在 server 端开放这个自定义的 `/sso/getFansList` 接口：

``` java
// 获取指定用户的粉丝列表
@RequestMapping("/sso/getFansList")
public Object getFansList(Long loginId) {
	System.out.println("---------------- 获取 loginId=" + loginId + " 的粉丝列表 ----------------");

	// 校验签名：只有拥有正确秘钥发起的请求才能通过校验
	SaSignUtil.checkRequest(SaHolder.getRequest());

	// 查询数据 (此处仅做模拟)
	List<Integer> list = Arrays.asList(10041, 10042, 10043, 10044);

	// 返回
	return list;
}
```

#### 4.3、访问测试
访问测试：[http://sa-sso-client1.com:9001/sso/myFansList](http://sa-sso-client1.com:9001/sso/myFansList)


### 5、无刷单点注销

有了单点登录，就必然伴随着单点注销（一处注销，全端下线）


此处简单介绍一下 SSO 模式三的单点注销链路过程：

1. Client 端在校验 ticket 时，将注销回调地址发送到 Server 端。
2. Server 端将此 Client 的注销回调回调信息存储到 List 集合。
3. Client 端向 Server 端发送单点注销请求。
4. Server 端遍历Set集合，逐个通知 Client 端下线。
5. Server 端注销下线。
6. 单点注销完成。


<button class="show-img" img-src="https://oss.dev33.cn/sa-token/doc/g/g3--sso3-logout.gif">加载动态演示图</button>


这些逻辑 Sa-Token 内部已经封装完毕，你只需按照文档步骤集成即可。

#### 5.1、更改注销方案

将 sso-client 首页路由方法里的注销链接换成 `/sso/logout` 接口：
``` java
// SSO-Client端：首页
@RequestMapping("/")
public String index() {
	String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
				"<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" + 
				"<p><a href=\"javascript:location.href='/sso/login?back=' + encodeURIComponent(location.href);\">登录</a>" + 
				" <a href='/sso/logout?back=self'>注销</a></p>";
	return str;
}
```

#### 5.2、启动测试 
重启项目，依次登录三个 client：
- [http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)
- [http://sa-sso-client2.com:9001/](http://sa-sso-client2.com:9001/)
- [http://sa-sso-client3.com:9001/](http://sa-sso-client3.com:9001/)

![sso-type3-client-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-client-index.png 's-w-sh')

在任意一个 client 里，点击 **`[注销]`** 按钮，即可单点注销成功（打开另外两个client，刷新一下页面，登录态丢失）。

<!-- ![sso-type3-slo.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-slo.png 's-w-sh') -->

![sso-type3-slo-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-slo-index.png 's-w-sh')

PS：这里我们为了方便演示，使用的是超链接跳页面的形式，正式项目中使用 Ajax 调用接口即可做到无刷单点登录退出。

例如，我们使用 [Apifox 接口测试工具](https://www.apifox.cn/) 可以做到同样的效果：

![sso-slo-apifox.png](https://oss.dev33.cn/sa-token/doc/sso/sso-slo-apifox.png 's-w-sh')

测试完毕！



### 6、后记
当我们熟读三种模式的单点登录之后，其实不难发现：所谓单点登录，其本质就是多个系统之间的会话共享。

当我们理解这一点之后，三种模式的工作原理也浮出水面：

- 模式一：采用共享 Cookie 来做到前端 Token 的共享，从而达到后端的 Session 会话共享。
- 模式二：采用 URL 重定向，以 ticket 码为授权中介，做到多个系统间的会话传播。
- 模式三：采用 Http 请求主动查询会话，做到 Client 端与 Server 端的会话同步。




