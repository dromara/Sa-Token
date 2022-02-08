# 搭建OAuth2-Server

--- 

### 1、准备工作 
首先修改hosts文件`(C:\windows\system32\drivers\etc\hosts)`，添加以下IP映射，方便我们进行测试：
``` url
127.0.0.1 sa-oauth-server.com
127.0.0.1 sa-oauth-client.com
```


### 2、引入依赖 
创建SpringBoot项目 `sa-token-demo-oauth2-server`（不会的同学自行百度或参考仓库示例），添加pom依赖：

``` xml
<!-- Sa-Token 权限认证, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- Sa-Token-OAuth2.0 模块 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-oauth2</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

### 3、开放服务 
1、新建 `SaOAuth2TemplateImpl` 
``` java
/**
 * Sa-Token OAuth2.0 整合实现 
 */
@Component
public class SaOAuth2TemplateImpl extends SaOAuth2Template {
	
	// 根据 id 获取 Client 信息 
	@Override
	public SaClientModel getClientModel(String clientId) {
		// 此为模拟数据，真实环境需要从数据库查询 
		if("1001".equals(clientId)) {
			return new SaClientModel()
					.setClientId("10001")
					.setClientSecret("aaaa-bbbb-cccc-dddd-eeee")
					.setAllowUrl("*")
					.setContractScope("userinfo")
					.setIsAutoMode(true);
		}
		return null;
	}
	
	// 根据ClientId 和 LoginId 获取openid 
	@Override
	public String getOpenid(String clientId, Object loginId) {
		// 此为模拟数据，真实环境需要从数据库查询 
		return "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__";
	}
	
}
```

你可以在 [框架配置](/use/config?id=SaClientModel属性定义) 了解有关 `SaClientModel` 对象所有属性的详细定义


2、新建`SaOAuth2ServerController`
``` java
/**
 * Sa-OAuth2 Server端 控制器 
 */
@RestController
public class SaOAuth2ServerController {

	// 处理所有OAuth相关请求 
	@RequestMapping("/oauth2/*")
	public Object request() {
		System.out.println("------- 进入请求: " + SaHolder.getRequest().getUrl());
		return SaOAuth2Handle.serverRequest();
	}
	
	// Sa-OAuth2 定制化配置 
	@Autowired
	public void setSaOAuth2Config(SaOAuth2Config cfg) {
		cfg.
			// 配置：未登录时返回的View 
			setNotLoginView(() -> {
				String msg = "当前会话在SSO-Server端尚未登录，请先访问"
	                        + "<a href='/oauth2/doLogin?name=sa&pwd=123456' target='_blank'> doLogin登录 </a>"
	                        + "进行登录之后，刷新页面开始授权";
	            return msg;
			}).
			// 配置：登录处理函数 
			setDoLoginHandle((name, pwd) -> {
				if("sa".equals(name) && "123456".equals(pwd)) {
					StpUtil.login(10001);
					return SaResult.ok();
				}
				return SaResult.error("账号名或密码错误");
			}).
			// 配置：确认授权时返回的View 
			setConfirmView((clientId, scope) -> {
				String msg = "<p>应用 " + clientId + " 请求授权：" + scope + "</p>"
                        + "<p>请确认：<a href='/oauth2/doConfirm?client_id=" + clientId + "&scope=" + scope + "' target='_blank'> 确认授权 </a></p>"
                        + "<p>确认之后刷新页面</p>";
				return msg;
			})
			;
	}

	// 全局异常拦截  
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}
```
注意：在`setDoLoginHandle`函数里如果要获取name, pwd以外的参数，可通过`SaHolder.getRequest().getParam("xxx")`来获取 

3、创建启动类：
``` java
/**
 * 启动：Sa-OAuth2 Server端 
 */
@SpringBootApplication 
public class SaOAuth2ServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaOAuth2ServerApplication.class, args);
		System.out.println("\nSa-Token-OAuth Server端启动成功");
	}
}
```
启动项目


### 4、访问测试 

1、由于暂未搭建Client端，我们可以使用Sa-Token官网作为重定向URL进行测试：
``` url
http://sa-oauth-server.com:8001/oauth2/authorize?response_type=code&client_id=1001&redirect_uri=http://sa-token.dev33.cn/&scope=userinfo
```

2、由于首次访问，我们在OAuth-Server端暂未登录，会被转发到登录视图 

![sa-oauth2-server-login-view](https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-server-login-view.png 's-w-sh')

3、点击doLogin进行登录之后刷新页面，会提示我们确认授权
![sa-oauth2-server-login-view](https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-server-scope.png 's-w-sh')

4、点击确认授权之后刷新页面，我们会被重定向至 redirect_uri 页面，并携带了code参数 

![sa-oauth2-server-code](https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-server-code.png 's-w-sh')

4、我们拿着code参数，访问以下地址：
``` url
http://sa-oauth-server.com:8001/oauth2/token?grant_type=authorization_code&client_id=1001&client_secret=aaaa-bbbb-cccc-dddd-eeee&code={code}
```

将得到 `Access-Token`、`Refresh-Token`、`openid`等授权信息 

![sa-oauth2-server-token](https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-server-token.png 's-w-sh')

测试完毕


### 5、运行官方示例 
以上代码只是简单模拟了一下OAuth2.0的授权流程，现在，我们运行一下官方示例，里面有制作好的UI界面

- OAuth2-Server端： `/sa-token-demo/sa-token-demo-oauth2-server/` [源码链接](https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-oauth2-server) <br/>
- OAuth2-Client端： `/sa-token-demo/sa-token-demo-oauth2-client/` [源码链接](https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-oauth2-client) <br/>

依次启动`OAuth2-Server` 与 `OAuth2-Client`，然后从浏览器访问：[http://sa-oauth-client.com:8002](http://sa-oauth-client.com:8002)

![sa-oauth2-client-index](https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-client-index.png 's-w-sh')

如图，可以针对OAuth2.0四种模式进行详细测试 


