# OAuth2 定制化登录页面

---



### 1、如何自定义 OAuth-Server 端的登录视图？

重写 `cfg.notLoginView` 策略：

``` java
@Autowired
public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
	// 配置：未登录时返回的View 
	SaOAuth2Strategy.instance.notLoginView = ()->{
		return new ModelAndView("xxx.html");
	};
}
```

在以上返回的视图中 ajax 方式调用 `/oauth2/doLogin` 接口，该接口接受以下参数：

| 参数			| 是否必填	| 说明									|
| :--------		| :--------	| :--------								|
| name			| 否		| 账号									|
| pwd			| 否		| 密码									|

接口返回值根据你重写的 `cfg.doLoginHandle` 策略进行自由决定。



### 2、如何自定义登录API的接口地址？
根据需求点选择解决方案：

#### 2.1、如果只是想在 doLoginHandle 函数里获取除 name、pwd 以外的参数？
``` java
// 在任意代码处获取前端提交的参数 
String xxx = SaHolder.getRequest().getParam("xxx");
```

#### 2.2、想完全自定义一个接口来接受前端登录请求？
``` java
// 直接定义一个拦截路由为 `/oauth2/doLogin` 的接口即可 
@RequestMapping("/oauth2/doLogin")
public SaResult ss(String name, String pwd) {
	System.out.println("------ 请求进入了自定义的API接口 ---------- ");
	if("sa".equals(name) && "123456".equals(pwd)) {
		StpUtil.login(10001);
		return SaResult.ok("登录成功！");
	}
	return SaResult.error("登录失败！");
}
```

#### 2.3、不想使用`/oauth2/doLogin`这个接口，想自定义一个API地址？

答：直接在前端更改点击按钮时 Ajax 的请求地址即可 



### 3、如何自定义 OAuth-Server 端的确认授权视图？

重写 `cfg.confirmView` 策略：

``` java
@Autowired
public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
	// 配置：授权确认视图 
	SaOAuth2Strategy.instance.confirmView = (clientId, scopes)->{
		Map<String, Object> map = new HashMap<>();
		map.put("clientId", clientId);
		map.put("scope", scopes);
		return new ModelAndView("confirm.html", map);
	};
}
```

在以上返回的视图中 ajax 方式调用 `/oauth2/doConfirm` 接口，即可完成授权，该接口接受以下参数：

| 参数			| 是否必填	| 说明									|
| :--------		| :--------	| :--------								|
| client_id		| 是		| 应用 id								|
| scope			| 是		| 具体授予的权限，多个用逗号(或空格)隔开	|

接口返回值样例：
``` js
{
    "code": 200,
    "msg": "ok",
    "data": null,
}
```


