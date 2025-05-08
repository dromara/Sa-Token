# 消息推送机制

消息推送机制简单来讲就是：sso-client 端按照特点格式构建一个 http 请求，调用 sso-server 端的 `/sso/pushS` 接口，sso-server 接收到消息后做出处理回应 sso-client 端。

消息推送是相互的，sso-server 端也可以构建 http 请求，调用 sso-client 端的 `/sso/pushC` 接口。

消息推送机制是应用与认证中心相互沟通的桥梁，ticket 校验、单点注销等行为都是依赖消息推送机制来实现的。

当然你也可以通过自定义消息处理器的方式，来扩展消息推送能力，这将非常有助于你完成一些应用与认证中心的自定义数据交互。

假设我们现在有如下需求：在 sso-client 获取 sso-server 端指定账号 id 的昵称、头像等信息，即：用户资料的拉取。


### 1、认证中心自定义消息处理器

首先，我们需要在 sso-server 实现一个消息处理器：

``` java
@RestController
public class SsoServerController {

	// 配置SSO相关参数 
	@Autowired
	private void configSso(SaSsoServerTemplate ssoServerTemplate) {

		// 添加消息处理器：userinfo (获取用户资料) （用于为 client 端开放拉取数据的接口）
		ssoServerTemplate.messageHolder.addHandle("userinfo", (ssoTemplate, message) -> {
			System.out.println("收到消息：" + message);

			// 自定义返回结果（模拟）
			return SaResult.ok()
					.set("id", message.get("loginId"))
					.set("name", "LinXiaoYu")
					.set("sex", "女")
					.set("age", 18);
		});

	}

}
```


### 2、应用端调用消息推送接口获取数据

首先保证在配置文件里要配置上消息推送的具体地址

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# sa-token配置 
sa-token:
    # sso-client 相关配置
    sso-client:
        # 应用标识
        client: sso-client3
        # sso-server 端主机地址
        server-url: http://sa-sso-server.com:9000
        # sso-server 端推送消息地址
		# 配置 server-url 后，框架可自动计算对应的 push-url 地址，也可以单独配置 push-url 地址，两者选其一即可
        # push-url: http://sa-sso-server.com:9000/sso/pushS
        # API 接口调用秘钥
        secret-key: SSO-C3-kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```
<!------------- tab:properties 风格  ------------->
``` properties
# sso-client 相关配置
# 应用标识
sa-token.sso-client.client=sso-client3
# sso-server 端主机地址
sa-token.sso-client.server-url=http://sa-sso-server.com:9000
# sso-server 端推送消息地址
# 配置 server-url 后，框架可自动计算对应的 push-url 地址，也可以单独配置 push-url 地址，两者选其一即可
sa-token.sso-client.push-url=http://sa-sso-server.com:9000/sso/pushS
# API 接口调用秘钥
sa-token.sso-client.secret-key=SSO-C3-kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```
<!---------------------------- tabs:end ---------------------------->


然后在需要拉取资料的地方：

``` java
// 查询我的账号信息：sso-client 前端 -> sso-center 后端 -> sso-server 后端
@RequestMapping("/sso/myInfo")
public Object myInfo() {
	// 如果尚未登录
	if( ! StpUtil.isLogin()) {
		return "尚未登录，无法获取";
	}

	// 获取本地 loginId
	Object loginId = StpUtil.getLoginId();

	// 构建消息对象 
	SaSsoMessage message = new SaSsoMessage();
	message.setType("userinfo");
	message.set("loginId", loginId);
	
	// 推送至 sso-server，并接收响应数据 
	SaResult result = SaSsoClientUtil.pushMessageAsSaResult(message);

	// 返回给前端
	return result;
}
```

