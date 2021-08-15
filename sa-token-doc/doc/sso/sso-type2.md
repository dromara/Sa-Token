# SSO模式二 URL重定向传播会话

如果我们的多个系统：部署在不同的域名之下，但是后端可以连接同一个Redis，那么便可以使用 **`[URL重定向传播会话]`** 的方式做到单点登录


### 0、解题思路

首先我们再次复习一下多个系统之间，为什么无法同步登录状态？

1. 前端的`Token`无法在多个系统下共享
2. 后端的`Session`无法在多个系统间共享

关于第二点，我们已经在"SSO模式一"章节中阐述，使用 [Alone独立Redis插件](/plugin/alone-redis) 做到权限缓存直连SSO-Redis数据中心，在此不再赘述

而第一点，才是我们解决问题的关键所在，在跨域模式下，意味着"共享Cookie方案"的失效，我们必须采用一种新的方案来传递Token 

1. 用户在 子系统 点击`[登录]`按钮
2. 用户跳转到子系统登录页面，并携带`back参数`记录初始页面URL 
3. 子系统检测到此用户尚未登录，再次将其重定向至SSO认证中心，并携带`redirect参数`记录子系统的登录页URL
4. 用户在SSO认证中心尚未登录，开始登录
5. 用户在SSO认证中心登录成功，重定向至子系统的登录页URL，并携带`ticket码`
6. 子系统使用ticket码从`SSO-Redis`中获取账号id，并在子系统登录此账号会话
7. 子系统将用户再次重定向至最初始的`back`页面

整个过程，除了第四步用户在SSO认证中心登录时会被打断，其余过程均是自动化的，当用户在另一个子系统再次点击`[登录]`按钮，由于此用户在SSO认证中心已有会话登录，
所以第四步也将自动化，也就是单点登录的最终目的 —— 一次登录，处处通行。

下面我们按照步骤依次完成上述过程

### 1、准备工作 
首先修改hosts文件`(C:\windows\system32\drivers\etc\hosts)`，添加以下IP映射，方便我们进行测试：
``` url
127.0.0.1 sa-sso-server.com
127.0.0.1 sa-sso-client1.com
127.0.0.1 sa-sso-client2.com
127.0.0.1 sa-sso-client3.com
```


### 2、搭建SSO-Server认证中心

> 搭建示例在官方仓库的 `/sa-token-demo/sa-token-demo-sso2-server/`，如遇到难点可结合源码进行测试学习

##### 2.1、创建SSO-Server端项目
创建SpringBoot项目 `sa-token-demo-sso-server`（不会的同学自行百度或参考仓库示例），添加pom依赖：

``` xml
<!-- Sa-Token 权限认证, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- Sa-Token整合redis (使用jackson序列化方式) -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-dao-redis-jackson</artifactId>
	<version>${sa.top.version}</version>
</dependency>
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-pool2</artifactId>
</dependency>
```

##### 2.2、创建SSO-Server端认证接口
``` java
/**
 * Sa-Token-SSO Server端 Controller 
 */
@RestController
public class SsoServerController {

	// SSO-Server端：处理所有SSO相关请求 
	@RequestMapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoHandle.serverRequest();
	}
	
	// 配置SSO相关参数 
	@Autowired
	private void configSso(SaTokenConfig cfg) {
		cfg.sso
			// 配置：未登录时返回的View 
			.setNotLoginView(() -> {
				String msg = "当前会话在SSO-Server端尚未登录，请先访问"
						+ "<a href='/sso/doLogin?name=sa&pwd=123456' target='_blank'> doLogin登录 </a>"
						+ "进行登录之后，刷新页面开始授权";
				return msg;
			})
			// 配置：登录处理函数 
			.setDoLoginHandle((name, pwd) -> {
				// 此处仅做模拟登录，真实环境应该查询数据进行登录 
				if("sa".equals(name) && "123456".equals(pwd)) {
					StpUtil.login(10001);
					return SaResult.ok("登录成功！");
				}
				return SaResult.error("登录失败！");
			})
			;
	}
	
}
```
注意：在`setDoLoginHandle`函数里如果要获取name, pwd以外的参数，可通过`SaHolder.getRequest().getParam("xxx")`来获取 

##### 2.3、application.yml配置
``` yml
# 端口
server:
    port: 9000

# Sa-Token配置
sa-token: 
	# SSO-相关配置
	sso: 
		# Ticket有效期 (单位: 秒)，默认五分钟 
		ticket-timeout: 300
		# 所有允许的授权回调地址 （此处为了方便测试配置为*，线上生产环境一定要配置为详细地地址）
		allow-url: "*"
        
spring: 
    # Redis配置 
    redis:
        # Redis数据库索引（默认为0）
        database: 1
        # Redis服务器地址
        host: 127.0.0.1
        # Redis服务器连接端口
        port: 6379
        # Redis服务器连接密码（默认为空）
        password: 
```
注意点：`allow-url`为了方便测试配置为`*`，线上生产环境一定要配置为详细URL地址 （详见下方“配置域名校验”）

##### 2.4、创建SSO-Server端启动类
``` java 
@SpringBootApplication
public class SaSsoServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaSsoServerApplication.class, args);
		System.out.println("\nSa-Token-SSO 认证中心启动成功");
	}
}
```


### 3、搭建SSO-Client应用端 

> 搭建示例在官方仓库的 `/sa-token-demo/sa-token-demo-sso2-client/`，如遇到难点可结合源码进行测试学习

##### 3.1、创建SSO-Client端项目
创建一个SpringBoot项目 `sa-token-demo-sso-client`，添加pom依赖：
``` xml
<!-- Sa-Token 权限认证, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- Sa-Token 整合redis (使用jackson序列化方式) -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-dao-redis-jackson</artifactId>
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


##### 3.2、创建SSO-Client端认证接口
``` java

/**
 * Sa-Token-SSO Client端 Controller 
 */
@RestController
public class SsoClientController {

	// SSO-Client端：首页
	@RequestMapping("/")
	public String index() {
		String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
					"<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" + 
					"<p><a href=\"javascript:location.href='/sso/login?back=' + encodeURIComponent(location.href);\">登录</a> " + 
					"<a href='/sso/logout?back=self'>注销</a></p>";
		return str;
	}
	
	// SSO-Client端：处理所有SSO相关请求 
	@RequestMapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoHandle.clientRequest();
	}

}
```

##### 3.3、配置SSO认证中心地址 
你需要在 `application.yml` 配置如下信息：
``` yml
# 端口
server:
    port: 9001
	
# sa-token配置 
sa-token: 
	# SSO-相关配置
	sso: 
		# SSO-Server端 单点登录地址 
		auth-url: http://sa-sso-server.com:9000/sso/auth
        # 是否打开单点注销接口
        is-slo: true
	
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
```
注意点：`sa-token.alone-redis` 的配置需要和SSO-Server端连接同一个Redis（database也要一样）

##### 3.4、写启动类
``` java
@SpringBootApplication
public class SaSsoClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaSsoClientApplication.class, args);
		System.out.println("\nSa-Token-SSO Client端启动成功");
	}
}
```
启动项目 


### 4、测试访问 

(1) 依次启动SSO-Server与SSO-Client端，然后从浏览器访问：[http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)

![sso-client-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client-index.png 's-w-sh')

(2) 首次打开，提示当前未登录，我们点击**`登录`** 按钮，页面会被重定向到登录中心

![sso-server-auth.png](https://oss.dev33.cn/sa-token/doc/sso/sso-server-auth.png 's-w-sh')

(3) SSO-Server提示我们在认证中心尚未登录，我们点击 **`doLogin登录`**按钮进行模拟登录

![sso-server-dologin.png](https://oss.dev33.cn/sa-token/doc/sso/sso-server-dologin.png 's-w-sh')

(4) SSO-Server认证中心登录成功，我们回到刚才的页面刷新页面

![sso-client-index-ok.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client-index-ok.png 's-w-sh')

(5) 页面被重定向至`Client`端首页，并提示登录成功，至此，`Client1`应用已单点登录成功！ 

(6) 我们再次访问`Client2`：[http://sa-sso-client2.com:9001/](http://sa-sso-client2.com:9001/)

![sso-client2-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client2-index.png 's-w-sh')

(7) 提示未登录，我们点击**`登录`**按钮，会直接提示登录成功

![sso-client2-index-ok.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client2-index-ok.png 's-w-sh')

(8) 同样的方式，我们打开`Client3`，也可以直接登录成功：[http://sa-sso-client3.com:9001/](http://sa-sso-client3.com:9001/)

![sso-client3-index-ok.png](https://oss.dev33.cn/sa-token/doc/sso/sso-client3-index-ok.png 's-w-sh')

至此，测试完毕！

可以看出，除了在`Client1`端我们需要手动登录一次之外，在`Client2端`和`Client3端`都是可以无需验证，直接登录成功的。

我们可以通过 F12控制台 Netword跟踪整个过程

![sso-genzong](https://oss.dev33.cn/sa-token/doc/sso/sso-genzong.png 's-w-sh')


### 5、运行官方仓库 

以上示例，虽然完整的复现了单点登录的过程，但是页面还是有些简陋，我们可以运行一下官方仓库的示例，里面有制作好的登录页面

> 下载官方示例，依次运行 `/sa-token-demo/sa-token-demo-sso-client/` 和 `/sa-token-demo/sa-token-demo-sso-server/`，访问：[http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)

![sso-server-login-hua](https://oss.dev33.cn/sa-token/doc/sso/sso-server-login-hua.png 's-w-sh')

默认测试密码：`sa / 123456`，其余流程保持不变 


### 6、配置域名校验

##### 6.1、Ticket劫持攻击
在以上的SSO-Server端示例中，配置项 `sa-token.sso.allow-url=*` 意为配置所有允许的Client端授权地址，不在此配置项中的URL将无法单点登录成功

以上示例为了方便测试被配置为*，但是，<font color="#FF0000" >在生产环境中，此配置项绝对不能配置为 * </font>，否则会有被ticket劫持的风险 

假设攻击者根据模仿我们的授权地址，巧妙的构造一个URL

> [http://sa-sso-server.com:9000/sso/auth?redirect=https://www.baidu.com/](http://sa-sso-server.com:9000/sso/auth?redirect=https://www.baidu.com/)

当不知情的小红被诱导访问了这个URL时，它将被重定向至百度首页

![sso-ticket-jc](https://oss.dev33.cn/sa-token/doc/sso/sso-ticket-jc.png 's-w-sh')

可以看到，代表着用户身份的ticket码也显现到了URL之中，借此漏洞，攻击者完全可以构建一个URL将小红的ticket码自动提交到攻击者自己的服务器，伪造小红身份登录网站

##### 6.2、防范方法

造成此漏洞的直接原因就是SSO-Server认证中心没有对 `redirect地址` 进行任何的限制，防范的方法也很简单，就是对`redirect参数`进行校验，如果其不在指定的URL列表中时，拒绝下放ticket 

我们将其配置为一个具体的URL：`allow-url=http://sa-sso-client1.com:9001/sso/login`，再次访问上述连接：

![sso-feifa-rf](https://oss.dev33.cn/sa-token/doc/sso/sso-feifa-rf.png 's-w-sh')

域名没有通过校验，拒绝授权！

##### 6.3、配置安全性参考表

| 配置方式		| 举例										| 安全性								|  建议									|
| :--------		| :--------									| :--------							| :--------								|
| 配置为*		| `*`										| <font color="#F00" >低</font>		| **<font color="#F00" >禁止在生产环境下使用</font>**	|
| 配置到域名	| `http://sa-sso-client1.com/*`					| <font color="#F70" >中</font>		| <font color="#F70" >不建议在生产环境下使用</font>	|
| 配置到详细地址| `http://sa-sso-client1.com:9001/sso/login`	| <font color="#080" >高</font>		| <font color="#080" >可以在生产环境下使用</font>	|


##### 6.4、疑问：为什么不直接回传Token，而是先回传ticket，再用ticket去查询对应的账号id？
Token作为长时间有效的会话凭证，在任何时候都不应该直接在暴露URL之中（虽然Token直接的暴露本身不会造成安全漏洞，但会为很多漏洞提供可乘之机）

因此Sa-Token-SSO选择先回传ticket，再由ticket获取账号id，且ticket一次性用完即废，提高安全性



### 7、跨Redis的单点登录
以上流程解决了跨域模式下的单点登录，但是后端仍然采用了共享Redis来同步会话，如果我们的架构设计中Client端与Server端无法共享Redis，又该怎么完成单点登录？

这就要采用模式三了，且往下看：[Http请求获取会话](/sso/sso-type3)


<!-- 
### 6、如何单点注销？
由于Server端与所有Client端都是在共用同一套会话，因此只要一端注销，即可全端下线，达到单点注销的效果 

在`SsoClientController`中添加以下代码：
``` java
// SSO-Client端：单点注销 (所有端一起下线)
@RequestMapping("logout")
public SaResult logout() {
	StpUtil.logout();
	return SaResult.ok();
}
``` -->








