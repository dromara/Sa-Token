# SSO模式二 URL重定向传播会话

如果我们的多个系统：部署在不同的域名之下，但是后端可以连接同一个Redis，那么便可以使用 **`[URL重定向传播会话]`** 的方式做到单点登录。


### 1、设计思路

首先我们再次复习一下，多个系统之间为什么无法同步登录状态？

1. 前端的`Token`无法在多个系统下共享。
2. 后端的`Session`无法在多个系统间共享。

关于第二点，我们已在 "SSO模式一" 章节中阐述，使用 [Alone独立Redis插件](/plugin/alone-redis) 做到权限缓存直连 SSO-Redis 数据中心，在此不再赘述。

而第一点，才是我们解决问题的关键所在，在跨域模式下，意味着 "共享Cookie方案" 的失效，我们必须采用一种新的方案来传递Token。

1. 用户在 子系统 点击 `[登录]` 按钮。
2. 用户跳转到子系统登录接口 `/sso/login`，并携带 `back参数` 记录初始页面URL。
	- 形如：`http://{sso-client}/sso/login?back=xxx` 
3. 子系统检测到此用户尚未登录，再次将其重定向至SSO认证中心，并携带`redirect参数`记录子系统的登录页URL。
	- 形如：`http://{sso-server}/sso/auth?redirect=xxx?back=xxx` 
4. 用户进入了 SSO认证中心 的登录页面，开始登录。
5. 用户 输入账号密码 并 登录成功，SSO认证中心再次将用户重定向至子系统的登录接口`/sso/login`，并携带`ticket码`参数。
	- 形如：`http://{sso-client}/sso/login?back=xxx&ticket=xxxxxxxxx`
6. 子系统根据 `ticket码` 从 `SSO-Redis` 中获取账号id，并在子系统登录此账号会话。
7. 子系统将用户再次重定向至最初始的 `back` 页面。

整个过程，除了第四步用户在SSO认证中心登录时会被打断，其余过程均是自动化的，当用户在另一个子系统再次点击`[登录]`按钮，由于此用户在SSO认证中心已有会话存在，
所以第四步也将自动化，也就是单点登录的最终目的 —— 一次登录，处处通行。


<button class="show-img" img-src="https://oss.dev33.cn/sa-token/doc/g/g3--sso2.gif">加载动态演示图</button>


下面我们按照步骤依次完成上述过程：

### 2、准备工作 
首先修改hosts文件`(C:\windows\system32\drivers\etc\hosts)`，添加以下IP映射，方便我们进行测试：
``` url
127.0.0.1 sa-sso-server.com
127.0.0.1 sa-sso-client1.com
127.0.0.1 sa-sso-client2.com
127.0.0.1 sa-sso-client3.com
```

### 3、搭建 Client 端项目 

> 搭建示例在官方仓库的 `/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso2-client/`，如遇到难点可结合源码进行测试学习

#### 3.1、去除 SSO-Server 的 Cookie 作用域配置 
在SSO模式一章节中我们打开了配置：
<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
    cookie: 
        # 配置 Cookie 作用域 
        domain: stp.com 
```
<!------------- tab:properties 风格  ------------->
``` properties
# 配置 Cookie 作用域 
sa-token.cookie.domain=stp.com
```
<!---------------------------- tabs:end ---------------------------->

此为模式一专属配置，现在我们将其注释掉**（一定要注释掉！）**


#### 3.2、创建 SSO-Client 端项目
创建一个 SpringBoot 项目 `sa-token-demo-sso2-client`，引入依赖：
<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 权限认证, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
<!-- Sa-Token 插件：整合SSO -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-sso</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- Sa-Token 整合redis (使用jackson序列化方式) -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-redis-jackson</artifactId>
	<version>${sa.top.version}</version>
</dependency>
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-pool2</artifactId>
</dependency>

<!-- Sa-Token插件：权限缓存与业务缓存分离 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-alone-redis</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 权限认证，在线文档：https://sa-token.cc
implementation 'cn.dev33:sa-token-spring-boot-starter:${sa.top.version}'

// Sa-Token 插件：整合SSO
implementation 'cn.dev33:sa-token-sso:${sa.top.version}'

// Sa-Token 整合 Redis (使用 jackson 序列化方式)
implementation 'cn.dev33:sa-token-redis-jackson:${sa.top.version}'
implementation 'org.apache.commons:commons-pool2'

// Sa-Token插件：权限缓存与业务缓存分离
implementation 'cn.dev33:sa-token-alone-redis:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->


#### 3.3、创建 SSO-Client 端认证接口

同 SSO-Server 一样，Sa-Token 为 SSO-Client 端所需代码也提供了完整的封装，你只需提供一个访问入口，接入 Sa-Token 的方法即可。

``` java

/**
 * Sa-Token-SSO Client端 Controller 
 */
@RestController
public class SsoClientController {

	// 首页 
	@RequestMapping("/")
	public String index() {
		String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
					"<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" + 
					"<p><a href=\"javascript:location.href='/sso/login?back=' + encodeURIComponent(location.href);\">登录</a> " + 
					"<a href='/sso/logout?back=self'>注销</a></p>";
		return str;
	}
	
	/*
	 * SSO-Client端：处理所有SSO相关请求 
	 * 		http://{host}:{port}/sso/login          -- Client端登录地址，接受参数：back=登录后的跳转地址 
	 * 		http://{host}:{port}/sso/logout         -- Client端单点注销地址（isSlo=true时打开），接受参数：back=注销后的跳转地址 
	 * 		http://{host}:{port}/sso/logoutCall     -- Client端单点注销回调地址（isSlo=true时打开），此接口为框架回调，开发者无需关心
	 */
	@RequestMapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoClientProcessor.instance.dister();
	}

}
```

##### 3.4、配置SSO认证中心地址 
你需要在 `application.yml` 配置如下信息：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# 端口
server:
    port: 9001

# sa-token配置 
sa-token: 
    # SSO-相关配置
    sso-client: 
        # SSO-Server 端主机地址
        server-url: http://sa-sso-server.com:9000

    # 配置Sa-Token单独使用的Redis连接 （此处需要和SSO-Server端连接同一个Redis）
    alone-redis: 
        # Redis数据库索引 (默认为0)
        database: 1
        # Redis服务器地址
        host: 127.0.0.1
        # Redis服务器连接端口
        port: 6379
        # Redis服务器连接密码（默认为空）
        password: 
        # 连接超时时间
        timeout: 10s
```
<!------------- tab:properties 风格  ------------->
``` properties
# 端口
server.port=9001

######### Sa-Token 配置 #########
# SSO-Server端 统一认证地址 
sa-token.sso-client.server-url=http://sa-sso-server.com:9000

# 配置 Sa-Token 单独使用的Redis连接 （此处需要和SSO-Server端连接同一个Redis）
# Redis数据库索引
sa-token.alone-redis.database=1
# Redis服务器地址
sa-token.alone-redis.host=127.0.0.1
# Redis服务器连接端口
sa-token.alone-redis.port=6379
# Redis服务器连接密码（默认为空）
sa-token.alone-redis.password=
# 连接超时时间
sa-token.alone-redis.timeout=10s
```
<!---------------------------- tabs:end ---------------------------->


注意点：`sa-token.alone-redis` 的配置需要和SSO-Server端连接同一个Redis**（database 值也要一样！database 值也要一样！database 值也要一样！重说三！）**

#### 3.5、写启动类
``` java
@SpringBootApplication
public class SaSso2ClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaSso2ClientApplication.class, args);
		
		System.out.println();
		System.out.println("---------------------- Sa-Token SSO 模式二 Client 端启动成功 ----------------------");
		System.out.println("配置信息：" + SaSsoManager.getClientConfig());
		System.out.println("测试访问应用端一: http://sa-sso-client1.com:9001");
		System.out.println("测试访问应用端二: http://sa-sso-client2.com:9001");
		System.out.println("测试访问应用端三: http://sa-sso-client3.com:9001");
		System.out.println("测试前需要根据官网文档修改hosts文件，测试账号密码：sa / 123456");
		System.out.println();
	}
}
```
启动项目 


### 4、测试访问 

(1) 依次启动 `SSO-Server` 与 `SSO-Client`，然后从浏览器访问：[http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)

<!-- （注：先前版本文档测试demo端口号为9001，后为了方便区分三种模式改为了9002，因此出现文字描述与截图端口号不一致情况，请注意甄别，后不再赘述） -->

![sso-client-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client-index.png 's-w-sh')

(2) 首次打开，提示当前未登录，我们点击 **`登录`** 按钮，页面会被重定向到登录中心

![sso-server-auth.png](https://oss.dev33.cn/sa-token/doc/sso/sso-server-auth.png 's-w-sh')

(3) SSO-Server提示我们在认证中心尚未登录，我们点击 **`doLogin登录`** 按钮进行模拟登录

![sso-server-dologin.png](https://oss.dev33.cn/sa-token/doc/sso/sso-server-dologin.png 's-w-sh')

(4) SSO-Server认证中心登录成功，我们回到刚才的页面刷新页面

![sso-client-index-ok.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client-index-ok.png 's-w-sh')

(5) 页面被重定向至`Client`端首页，并提示登录成功，至此，`Client1`应用已单点登录成功！ 

(6) 我们再次访问`Client2`：[http://sa-sso-client2.com:9001/](http://sa-sso-client2.com:9001/)

![sso-client2-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client2-index.png 's-w-sh')

(7) 提示未登录，我们点击 **`登录`** 按钮，会直接提示登录成功

![sso-client2-index-ok.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client2-index-ok.png 's-w-sh')

(8) 同样的方式，我们打开`Client3`，也可以直接登录成功：[http://sa-sso-client3.com:9001/](http://sa-sso-client3.com:9001/)

![sso-client3-index-ok.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client3-index-ok.png 's-w-sh')

至此，测试完毕！

可以看出，除了在`Client1`端我们需要手动登录一次之外，在`Client2端`和`Client3端`都是可以无需再次认证，直接登录成功的。

我们可以通过 F12控制台 Network 跟踪整个过程

![sso-genzong](https://oss.dev33.cn/sa-token/doc/sso/sso-genzong.png 's-w-sh')

<!-- 
### 5、运行官方仓库 

以上示例，虽然完整的复现了单点登录的过程，但是页面还是有些简陋，我们可以运行一下官方仓库的示例，里面有制作好的登录页面

> 下载官方示例，依次运行：
> - `/sa-token-demo/sa-token-demo-sso2-server/`
> - `/sa-token-demo/sa-token-demo-sso2-client/`
> 
> 然后访问：
> - [http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)

![sso-server-login-hua](https://oss.dev33.cn/sa-token/doc/sso/sso-server-login-hua.png 's-w-sh')

默认测试密码：`sa / 123456`，其余流程保持不变 
 -->
 
 
### 5、无刷单点注销

有了单点登录，就必然伴随着单点注销（一处注销，全端下线）

如果你的所有 client 都是基于 SSO 模式二来对接的，那么单点注销其实很简单：

``` java
// 在 `sa-token.is-share=true` 的情况下，调用此代码即可单点注销：
StpUtil.logout();

// 在 `sa-token.is-share=false` 的情况下，调用此代码即可单点注销：
StpUtil.logout(StpUtil.getLoginId());
```

你可能会比较疑惑，这不就是个普通的会话注销API吗，为什么会有单点注销的效果？

因为模式二需要各个 sso-client 和 sso-server 连接同一个 redis，即使登录再多的 client，本质上对应的仍是同一个会话，因此可以做到任意一处调用注销，全端一起下线的效果。

而如果你的各个 client 架构各不相同，有的是模式二对接，有的是模式三对接，则需要麻烦一点才能做到单点注销。

这里的“麻烦”指两处：1、框架内部逻辑麻烦；2、开发者集成麻烦。

框架内部的麻烦 sa-token-sso 已经封装完毕，无需过多关注，而开发者的麻烦步骤也不是很多：


#### 5.1、增加 pom.xml 配置 

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

因为我们已经在控制台手动打印 url 请求日志了，所以此处 `forest.log-enabled=false` 关闭 Forest 框架自身的日志打印，这不是必须的，你可以将其打开。


#### 5.2、SSO-Client 端新增配置：API调用秘钥

在 `application.yml` 增加：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
    sign:
        # API 接口调用秘钥
        secret-key: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
		
forest: 
	# 关闭 forest 请求日志打印
	log-enabled: false
```
<!------------- tab:properties 风格  ------------->
``` properties
# 接口调用秘钥 
sa-token.sign.secret-key=kQwIOrYvnXmSDkwEiFngrKidMcdrgKor

# 关闭 forest 请求日志打印
forest.log-enabled=false
```
<!---------------------------- tabs:end ---------------------------->

注意 secretkey 秘钥需要与SSO认证中心的一致 

#### 5.3、SSO-Client 配置 http 请求处理器
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

#### 5.3、启动测试 
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


### 6、跨 Redis 的单点登录
以上流程解决了跨域模式下的单点登录，但是后端仍然采用了共享Redis来同步会话，如果我们的架构设计中Client端与Server端无法共享Redis，又该怎么完成单点登录？

这就要采用模式三了，且往下看：[SSO模式三：Http请求获取会话](/sso/sso-type3)









