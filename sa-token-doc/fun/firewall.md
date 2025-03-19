# 防火墙

Sa-Token 内置防火墙组件 `SaFirewallStrategy`，用于拦截一些可能造成攻击的危险请求。
例如当前端提交的 path 为 `/test//login` 时，框架将会强制截断请求，响应以下内容：

``` txt
非法请求：/test//login
```

因为包含双斜杠的 path 请求通常被用于鉴权绕行攻击。类似的拦截规则还有很多， `SaFirewallStrategy` 采用 hooks 机制，允许开发者自由扩展拦截规则，
框架默认具有以下 hook 拦截规则：

- `SaFirewallCheckHookForWhitePath`：请求 path 白名单放行。
- `SaFirewallCheckHookForBlackPath`：请求 path 黑名单校验。
- `SaFirewallCheckHookForPathDangerCharacter`：请求 path 危险字符校。
- `SaFirewallCheckHookForPathBannedCharacter`：请求 path 禁止字符校验。
- `SaFirewallCheckHookForDirectoryTraversal`：请求 path 目录遍历符检测。
- `SaFirewallCheckHookForHost`：Host 检测。
- `SaFirewallCheckHookForHttpMethod`：请求 Method 检测。
- `SaFirewallCheckHookForHeader`：请求头检测。
- `SaFirewallCheckHookForParameter`：请求参数检测。


### 1、默认 hook 配置：

假设我们想要增加请求 path 黑名单，可以使用如下代码：

``` java
@Configuration
public class SaTokenConfigure {
	@PostConstruct
	public void saTokenPostConstruct() {
		SaFirewallCheckHookForBlackPath.instance.resetConfig("/abc");
	}
}
```

现在从浏览器访问 `/abc`，将会被防火墙组件直接拦截：

``` txt
非法请求：/abc
```


除了 `SaFirewallCheckHookForBlackPath` 以外，其它所有 hook 均可通过此方式重载配置，在此暂不冗余演示。


### 2、注册新的 hook 规则：
你可以使用如下代码注册新的 hook 规则：

``` java
@PostConstruct
public void saTokenPostConstruct() {
	// 注册新 hook 演示，拦截所有带有 pwd 参数的请求，拒绝响应 
	SaFirewallStrategy.instance.registerHook((req, res, extArg)->{
		if(req.getParam("pwd") != null) {
			throw new FirewallCheckException("请求中不可包含 pwd 参数");
		}
	});
}
```

除了注册新 hook 规则，你还可以移除默认 hook ，来删减你认为不必要存在的校验规则：

``` java
// 移除指定类型的 hook 验证
SaFirewallStrategy.instance.removeHook(SaFirewallCheckHookForHost.class);
```


### 3、利用自动注入特性注册 hook
如果你的项目属于 IOC 环境（例如 SpringBoot 项目），还可以这样注册 hook：
``` java
// 自定义防火墙校验 hook 
@Component
public class SaFirewallCheckHookForXxx implements SaFirewallCheckHook {
    @Override
    public void execute(SaRequest req, SaResponse res, Object extArg) {
        System.out.println("----------- 自定义防火墙校验 hook ");
    }
}
```


### 4、指定异常处理：

被防火墙拦截的请求不会做出格式化响应，因为通常这些请求为非正常业务请求，只需阻断即可，无需前端依照响应做出页面提示。

如果你的业务切实需要对防火墙拦截做出格式化响应，可以通过以下代码完成：

``` java
@PostConstruct
public void saTokenPostConstruct() {
	// 指定防火墙校验不通过时的处理方案
	SaFirewallStrategy.instance.checkFailHandle = (e, req, res, extArg) -> {
		System.out.println("防火墙校验不通过：" + e.getMessage());
		try {
			HttpServletResponse response = (HttpServletResponse)res.getSource();
			response.setContentType("application/json;charset=UTF-8");
			String resJson = SaResult.error(e.getMessage()).toString();
			response.getWriter().print(resJson);
			response.getWriter().flush();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	};
}
```

浏览器将得到以下 json 格式响应：

``` js
{
  "code": 500,
  "msg": "非法请求：/abc",
  "data": null
}
