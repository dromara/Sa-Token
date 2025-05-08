# 单点注销

Sa-Token SSO 提供多种注销模式：

从注销范围上可以分为：

- 单端注销：会话只在当前应用注销，其它应用和认证中心不受影响。
- 全端注销：一处注销，全端下线。也即：单点注销。
- 单浏览器注销：该账号的只在当前浏览器登录的应用注销，其它浏览器/设备不受影响。

从注销方式上可以分为：
- ajax 无刷单点注销：调用指定的 RestAPI 接口完成注销。
- 跳页面注销：跳转到指定接口进行注销，注销完成后原路返回或跳转到指定页面。

--- 

### 1、单端注销

在后端添加接口：
``` java
// 当前应用独自注销 (不退出其它应用)
@RequestMapping("/sso/logoutByAlone")
public Object logoutByAlone() {
	StpUtil.logout();
	return SaSsoClientProcessor.instance._ssoLogoutBack(SaHolder.getRequest(), SaHolder.getResponse());
}
```

在前端或跳转或 ajax 异步调用此接口即可。

如果是跳转可指定 back 参数，代表注销成功后跳转的地址，例如：`http://sso-client.com/sso/logoutByAlone?back=https://sa-token.cc` 


### 2、全端注销

此处先简单看一下 Sa-Token SSO 的单点注销链路过程：

1. sso-client 的前端向 sso-client 的后端发起单点注销请求。(调用 `http://{sso-client}/sso/logout`)
2. sso-client 的后端向 sso-server 的后端发送单点注销请求。(调用 `http://{sso-server}/sso/pushS?msgType=signout`)
3. sso-server 端遍历 client 列表，逐个推送消息通知 sso-client 端下线。(`http://{sso-client}/sso/pushC?msgType=logoutCall`)
4. sso-server 端注销下线。
5. sso-server 后端响应 sso-client 后端：注销完成。
6. sso-client 后端响应 sso-client 前端：注销完成。
7. 整体完成。


<button class="show-img" img-src="https://oss.dev33.cn/sa-token/doc/g/g3--sso3-logout.gif">加载动态演示图</button>


这些逻辑 Sa-Token 内部已经封装完毕，你只需按照文档步骤集成即可。以模式三 demo 为例：

#### 2.1、更改注销方案

单点注销是 Sa-Token SSO 内部已封装的接口，无需手动再添加，只需要在前端调用即可。

``` java
// SSO-Client端：首页
@RequestMapping("/")
public String index() {
	String str = "<h2>Sa-Token SSO-Client 应用端 (模式三)</h2>" +
			"<p>当前会话是否登录：" + StpUtil.isLogin() + " (" + StpUtil.getLoginId("") + ")</p>" +
			"<p> " +
				"<a href='/sso/login?back=/'>登录</a> - " +
				"<a href='/sso/logoutByAlone?back=/'>单应用注销</a> - " +
				"<a href='/sso/logout?back=self'>全端注销</a> " +
			"</p>";
	return str;
}
```

重点在第 9 行。

#### 2.2、启动测试 
重启项目，依次登录三个 client：
- [http://sa-sso-client1.com:9003/](http://sa-sso-client1.com:9003/)
- [http://sa-sso-client2.com:9003/](http://sa-sso-client2.com:9003/)
- [http://sa-sso-client3.com:9003/](http://sa-sso-client3.com:9003/)

![sso-type3-client-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-client-index.png 's-w-sh')

在任意一个 client 里，点击 **`[注销]`** 按钮，即可单点注销成功（打开另外两个client，刷新一下页面，登录态丢失）。

<!-- ![sso-type3-slo.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-slo.png 's-w-sh') -->

![sso-type3-slo-index.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type3-slo-index.png 's-w-sh')

PS：这里我们为了方便演示，使用的是超链接跳页面的形式，正式项目中使用 Ajax 调用接口即可做到无刷单点登录退出。

例如，我们使用 [Apifox 接口测试工具](https://www.apifox.cn/) 可以做到同样的效果：

![sso-slo-apifox.png](https://oss.dev33.cn/sa-token/doc/sso/sso-slo-apifox.png 's-w-sh')



### 3、单浏览器注销

单浏览器注销的前提是在登录时按照 `deviceId` 设备ID 参数为登录进行分组，这样在发起注销时即可格局设备ID参数做到单浏览器注销功能。

#### 3.1、sso-server 端加上设备ID参数登录

首先在 sso-server 的登录方法内，加上 deviceId 参数，例如：

``` java
@RestController
public class SsoServerController {
	
	// 其它代码，非重点，省略展示...
	
	// 配置SSO相关参数 
	@Autowired
	private void configSso(SaSsoServerTemplate ssoServerTemplate) {
		// 配置：登录处理函数 
		ssoServerTemplate.strategy.doLoginHandle = (name, pwd) -> {
			// 此处仅做模拟登录，真实环境应该查询数据库进行登录
			if("sa".equals(name) && "123456".equals(pwd)) {
				String deviceId = SaHolder.getRequest().getParam("deviceId", SaFoxUtil.getRandomString(32));
				StpUtil.login(10001, new SaLoginParameter().setDeviceId(deviceId));
				return SaResult.ok("登录成功！").setData(StpUtil.getTokenValue());
			}
			return SaResult.error("登录失败！");
		};

	}
}
```

如上代码，在登录时获取前端提交的 deviceId 参数，如果前端没有提交则随机生成一个。


#### 3.2、sso-client 端发起注销时指定单设备注销参数

然后在 sso-client 发起单点注销时，加上 `singleDeviceIdLogout=true` 参数，代表按照设备 id 进行分组注销，非本设备id的会话不参与注销行为：

``` java
// SSO-Client端：首页
@RequestMapping("/")
public String index() {
	String str = "<h2>Sa-Token SSO-Client 应用端 (模式三)</h2>" +
			"<p>当前会话是否登录：" + StpUtil.isLogin() + " (" + StpUtil.getLoginId("") + ")</p>" +
			"<p> " +
				"<a href='/sso/login?back=/'>登录</a> - " +
				"<a href='/sso/logoutByAlone?back=/'>单应用注销</a> - " +
				"<a href='/sso/logout?back=self&singleDeviceIdLogout=true'>单浏览器注销</a> - " +
				"<a href='/sso/logout?back=self'>全端注销</a> " +
			"</p>";
	return str;
}
```

重点在第 9 行。


> [!WARNING| label:测试注意点] 
> 在进行测试时，同时将一个浏览器双击打开两次，是不算 “不同浏览器” 的，虽然你打开了两个浏览器窗口，但是这两个浏览器的会话数据是互通的。
> 
> 必须打开两个不同的浏览器来测试，或者按快捷键 `ctrl + shift + N` 打开隐私模式，才可以做到会话相互隔离。


