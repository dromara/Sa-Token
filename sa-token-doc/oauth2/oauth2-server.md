# 搭建OAuth2-Server

--- 

### 1、准备工作 
首先修改hosts文件`(C:\windows\system32\drivers\etc\hosts)`，添加以下IP映射，方便我们进行测试：
``` url
127.0.0.1 sa-oauth-server.com
127.0.0.1 sa-oauth-client.com
```


### 2、引入依赖 
创建SpringBoot项目 `sa-token-demo-oauth2-server`（不会的同学自行百度或参考仓库示例），引入 `pom.xml` 依赖：

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml
<!-- Sa-Token 权限认证, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- Sa-Token OAuth2.0 模块 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-oauth2</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- Sa-Token 整合 Redis (可选) -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-redis-jackson</artifactId>
	<version>${sa-token.version}</version>
</dependency>
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-pool2</artifactId>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 权限认证，在线文档：https://sa-token.cc
implementation 'cn.dev33:sa-token-spring-boot-starter:${sa.top.version}'

// Sa-Token OAuth2.0 模块
implementation 'cn.dev33:sa-token-oauth2:${sa.top.version}'

// Sa-Token 整合 Redis (可选)
implementation 'cn.dev33:sa-token-redis-jackson:${sa.top.version}'
implementation 'org.apache.commons:commons-pool2'
```
<!---------------------------- tabs:end ---------------------------->

注：Redis 相关依赖是非必须的，如果集成了 redis，可以让你更细致的观察到 sa-token-oauth2 的底层数据格式。


### 3、开放服务 
<!-- 
1、自定义数据加载器：新建 `SaOAuth2DataLoaderImpl` 实现 `SaOAuth2DataLoader` 接口。

``` java
/**
 * Sa-Token OAuth2：自定义数据加载器
 */
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	
	// 根据 clientId 获取 Client 信息
	@Override
	public SaClientModel getClientModel(String clientId) {
		// 此为模拟数据，真实环境需要从数据库查询 
		if("1001".equals(clientId)) {
			return new SaClientModel()
					.setClientId("1001")    // client id
					.setClientSecret("aaaa-bbbb-cccc-dddd-eeee")    // client 秘钥
					.addAllowRedirectUris("*")    // 所有允许授权的 url
					.addContractScopes("openid", "userid", "userinfo")    // 所有签约的权限
					.addAllowGrantTypes(	 // 所有允许的授权模式
							GrantType.authorization_code, // 授权码式
							GrantType.implicit,  // 隐式式
							GrantType.refresh_token,  // 刷新令牌
							GrantType.password,  // 密码式
							GrantType.client_credentials  // 客户端模式
					)
			;
		}
		return null;
	}
	
	// 根据 clientId 和 loginId 获取 openid
	@Override
	public String getOpenid(String clientId, Object loginId) {
		// 此处使用框架默认算法生成 openid，真实环境建议改为从数据库查询
		return SaOAuth2DataLoader.super.getOpenid(clientId, loginId);
	}

}
``` 

你可以在 [框架配置](/use/config?id=SaClientModel属性定义) 了解有关 `SaClientModel` 对象所有属性的详细定义
-->

1、新建`SaOAuth2ServerController`
``` java
/**
 * Sa-Token OAuth2 Server端 控制器 
 */
@RestController
public class SaOAuth2ServerController {

	// OAuth2-Server 端：处理所有 OAuth2 相关请求
	@RequestMapping("/oauth2/*")
	public Object request() {
		System.out.println("------- 进入请求: " + SaHolder.getRequest().getUrl());
		return SaOAuth2ServerProcessor.instance.dister();
	}

	// Sa-Token OAuth2 定制化配置 
	@Autowired
	public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
		
		// 添加 client 信息 
		oauth2Server.addClient(
			new SaClientModel()
				.setClientId("1001")    // client id
				.setClientSecret("aaaa-bbbb-cccc-dddd-eeee")    // client 秘钥
				.addAllowRedirectUris("*")    // 所有允许授权的 url
				.addContractScopes("openid", "userid", "userinfo")    // 所有签约的权限
				.addAllowGrantTypes(	 // 所有允许的授权模式
						GrantType.authorization_code, // 授权码式
						GrantType.implicit,  // 隐式式
						GrantType.refresh_token,  // 刷新令牌
						GrantType.password,  // 密码式
						GrantType.client_credentials  // 客户端模式
				)
		);
		
		// 可以添加更多 client 信息，只要保持 clientId 唯一就行了
		// oauth2Server.addClient(...)
		
		// 配置：未登录时返回的View 
		SaOAuth2Strategy.instance.notLoginView = () -> {
			// 简化模拟表单
			String doLoginCode =
					"fetch(`/oauth2/doLogin?name=${document.querySelector('#name').value}&pwd=${document.querySelector('#pwd').value}`) " +
							" .then(res => res.json()) " +
							" .then(res => { if(res.code === 200) { location.reload() } else { alert(res.msg) } } )";
			String res =
					"<h2>当前客户端在 OAuth-Server 认证中心尚未登录，请先登录</h2>" +
							"用户：<input id='name' /> <br> " +
							"密码：<input id='pwd' /> <br>" +
							"<button onclick=\"" + doLoginCode + "\">登录</button>";
			return res;
		};
		
		// 配置：登录处理函数 
		SaOAuth2Strategy.instance.doLoginHandle = (name, pwd) -> {
			if("sa".equals(name) && "123456".equals(pwd)) {
				StpUtil.login(10001);
				return SaResult.ok();
			}
			return SaResult.error("账号名或密码错误");
		};
		
		// 配置：确认授权时返回的 view 
		SaOAuth2Strategy.instance.confirmView = (clientId, scopes) -> {
			String scopeStr = SaFoxUtil.convertListToString(scopes);
			String yesCode =
					"fetch('/oauth2/doConfirm?client_id=" + clientId + "&scope=" + scopeStr + "', {method: 'POST'})" +
					".then(res => res.json())" +
					".then(res => location.reload())";
			String res = "<p>应用 " + clientId + " 请求授权：" + scopeStr + "，是否同意？</p>"
					+ "<p>" +
					"		<button onclick=\"" + yesCode + "\">同意</button>" +
					"		<button onclick='history.back()'>拒绝</button>" +
					"</p>";
			return res;
		};
	}
	
}
```
注意：
- 在 `doLoginHandle` 函数里如果要获取 name, pwd 以外的参数，可通过 `SaHolder.getRequest().getParam("xxx")` 来获取。
- 你可以在 [框架配置](/use/config?id=SaClientModel属性定义) 了解有关 `SaClientModel` 对象所有属性的详细定义。


2、全局异常处理
``` java
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
}
```

3、创建启动类：
``` java
/**
 * 启动：Sa-OAuth2 Server端 
 */
@SpringBootApplication 
public class SaOAuth2ServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaOAuth2ServerApplication.class, args);
		System.out.println("\nSa-Token-OAuth2 Server端启动成功，配置如下：");
		System.out.println(SaOAuth2Manager.getServerConfig());
	}
}
```
启动项目


### 4、访问测试 

1、由于暂未搭建Client端，我们可以使用 Sa-Token 官网作为重定向URL进行测试：
``` url
http://sa-oauth-server.com:8000/oauth2/authorize?response_type=code&client_id=1001&redirect_uri=https://sa-token.cc&scope=openid
```

2、由于首次访问，我们在OAuth-Server端暂未登录，会被转发到登录视图 

![sa-oauth2-server-login-view](https://oss.dev33.cn/sa-token/doc/oauth2-new/sa-oauth2-server-login-view--v43.png 's-w-sh')

3、输入 `sa/123456` 进行登录之后，会提示我们确认授权
![sa-oauth2-server-scope](https://oss.dev33.cn/sa-token/doc/oauth2-new/sa-oauth2-server-scope.png 's-w-sh')

4、点击同意授权之后，我们会被重定向至 redirect_uri 页面，并携带了code参数 

![sa-oauth2-server-code](https://oss.dev33.cn/sa-token/doc/oauth2-new/sa-oauth2-server-code.png 's-w-sh')

4、我们拿着code参数，访问以下地址：
``` url
http://sa-oauth-server.com:8000/oauth2/token?grant_type=authorization_code&client_id=1001&client_secret=aaaa-bbbb-cccc-dddd-eeee&code={code}
```

将得到 `Access-Token`、`Refresh-Token`、`openid`等授权信息：

``` js
{
  "code": 200,
  "msg": "ok",
  "data": null,
  "token_type": "bearer",
  "access_token": "cAls8jnBLmeo5yuCUMwb8zxaSsQPPzGINXF3NOCjCqFHplr6hagdT6A5HeR2",
  "refresh_token": "L2rPbJ3aaOXwaB4Zu0EGWNz5EjVNpw5u2oMP9CS2IEap7rR3Hb76ZqqHS07J",
  "expires_in": 7199,
  "refresh_expires_in": 2591999,
  "client_id": "1001",
  "scope": "openid",
  "openid": "ded91dc189a437dd1bac2274be167d50"
}
```

<!-- ![sa-oauth2-server-token](https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-server-token.png 's-w-sh') -->

测试完毕


### 5、运行官方示例
以上代码只是简单模拟了一下OAuth2.0的授权流程，现在，我们运行一下官方示例，里面有制作好的UI界面

- OAuth2-Server端： `/sa-token-demo/sa-token-demo-oauth2/sa-token-demo-oauth2-server/` [源码链接](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-oauth2/sa-token-demo-oauth2-server) <br/>
- OAuth2-Client端： `/sa-token-demo/sa-token-demo-oauth2/sa-token-demo-oauth2-client/` [源码链接](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-oauth2/sa-token-demo-oauth2-client) <br/>

依次启动`OAuth2-Server` 与 `OAuth2-Client`，然后从浏览器访问：[http://sa-oauth-client.com:8002](http://sa-oauth-client.com:8002)

![sa-oauth2-client-index](https://oss.dev33.cn/sa-token/doc/oauth2-new/sa-oauth2-client-index.png 's-w-sh')

如图，可以针对OAuth2.0四种模式进行详细测试 



### 6、OAuth2 前端测试页

OAuth2 前端测试页： 
`/sa-token-demo/sa-token-demo-oauth2/sa-token-demo-oauth2-client-h5/` 
[源码链接](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-oauth2/sa-token-demo-oauth2-client-h5) <br/>

此示例允许你在前端自由配置 OAuth-Client 端所需的各个参数，方便对 OAuth2 四种模式的测试。

![sa-oauth2-client-index](https://oss.dev33.cn/sa-token/doc/oauth2-new/sa-oauth2-client-test-h5-page.png 's-w-sh')

<p><a class="case-btn case-btn-video" href="https://www.bilibili.com/video/BV13LSMYzEmE/" target="_blank">
	参考视频：OAuth2 四种模式 前端测试页
</a></p>


