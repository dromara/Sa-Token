# OAuth2-自定义 API 路由 

---

### 方式一：修改全局变量

在之前的章节中，我们演示了如何搭建一个 OAuth2 认证中心：
``` java
/**
 * Sa-Token-OAuth2 Server端 Controller 
 */
@RestController
public class SaOAuth2ServerController {

	// OAuth2-Server 端：处理所有 OAuth2 相关请求 
	@RequestMapping("/oauth2/*")
	public Object request() {
		return SaOAuth2ServerProcessor.instance.dister();
	}
	
	// ... 其它代码
	
}
```

这种写法集成简单但却不够灵活。例如获取 code 授权码地址只能是：`http://{host}:{port}/oauth2/authorize`，如果我们想要自定义其API地址，应该怎么做呢？

打开 OAuth2 模块相关源码，有关 API 的设计都定义在：
[SaOAuth2Consts.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-plugin/sa-token-oauth2/src/main/java/cn/dev33/satoken/oauth2/consts/SaOAuth2Consts.java)
中，我们可以对其进行二次修改。

例如，我们可以在 Main 方法启动类或者 OAuth2 配置方法中修改变量值：
``` java
// 配置 OAuth2 相关参数 
@Autowired
private void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
	// 自定义API地址
	SaOAuth2Consts.Api.authorize = "/oauth2/authorize2";
	// ... 
}
```

启动项目，统一认证地址就被我们修改成了：`http://{host}:{port}/oauth2/authorize2`


### 方式二：拆分路由入口
根据上述路由入口：`@RequestMapping("/oauth2/*")`，我们给它起一个合适的名字 —— 聚合式路由。

与之对应的，我们可以将其修改为拆分式路由：

``` java
/**
 * Sa-Token-OAuth2 Server端 Controller 
 */
@RestController
public class SaOAuth2ServerController {

	// 模式一：Code授权码 || 模式二：隐藏式
	@RequestMapping("/oauth2/authorize")
	public Object authorize() {
		return SaOAuth2ServerProcessor.instance.authorize();
	}

	// 用户登录
	@RequestMapping("/oauth2/doLogin")
	public Object doLogin() {
		return SaOAuth2ServerProcessor.instance.doLogin();
	}

	// 用户确认授权
	@RequestMapping("/oauth2/doConfirm")
	public Object doConfirm() {
		return SaOAuth2ServerProcessor.instance.doConfirm();
	}

	// Code 换 Access-Token || 模式三：密码式
	@RequestMapping("/oauth2/token")
	public Object token() {
		return SaOAuth2ServerProcessor.instance.token();
	}

	// Refresh-Token 刷新 Access-Token
	@RequestMapping("/oauth2/refresh")
	public Object refresh() {
		return SaOAuth2ServerProcessor.instance.refresh();
	}

	// 回收 Access-Token
	@RequestMapping("/oauth2/revoke")
	public Object revoke() {
		return SaOAuth2ServerProcessor.instance.revoke();
	}

	// 模式四：凭证式
	@RequestMapping("/oauth2/client_token")
	public Object clientToken() {
		return SaOAuth2ServerProcessor.instance.clientToken();
	}

}
```

拆分式路由 与 聚合式路由 在功能上完全等价，且提供了更为细致的路由管控。

