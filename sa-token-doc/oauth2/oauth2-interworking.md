# Sa-Token-OAuth2 与登录会话实现数据互通

--- 

### 前提

前提，我们：
- 把 OAuth2 模块生成的令牌称作资源令牌（access_token），
- 把 StpUtil 登录会话生成的令牌称作会话令牌（satoken）。

正常情况下，资源令牌 与 会话令牌 的数据是不互通的，具体表现就是：当我们拿着 access_token 去访问 satoken 令牌的接口，会被抛出异常：`无效Token：xxxxx`

那么，有什么办法可以做到这两个模块的数据互通呢？



### OAuth2-Server 端数据互通

很简单，你只需要在 `SaOAuth2TemplateImpl` 实现类中继续重写 Access-Token 的生成策略：

``` java
@Component
public class SaOAuth2TemplateImpl extends SaOAuth2Template {
	
	// ... 其它代码 

	// 重写 Access-Token 生成策略：复用登录会话的Token 
	@Override
	public String randomAccessToken(String clientId, Object loginId, String scope) {
		String tokenValue = StpUtil.createLoginSession(loginId);
		return tokenValue;
	}
	
}
```

重启项目，然后在 OAuth2 模块授权登录，现在生成的 `access_token` ，可以用来访问 `satoken` 的会话接口了。


### OAuth2-Client 数据互通
除了Server端，Client端也可以打通 `access_token` 与 `satoken` 会话。做法是在 Client 端拿到 `access_token` 后进行登录时，使用 SaLoginModel 预定登录生成的 Token 值 

``` java
// 1. 获取到access_token
String access_token = ...

// 2. 登录时预定生成的token 
StpUtil.login(uid, SaLoginConfig.setToken(access_token));

// 3. 其它代码...
```


> [!WARNING| label:注意点] 
> 数据互通，让前端与后端的交互更加方便，一个 token 即可访问所有接口，但也一定程度上失去了OAuth2的 “不同 Client 不同权限” 的设计意义，
> 同时也默认每个 Client 都拥有了账号的会话权限（access_token 与 satoken 为同一个）。
> 
> 应该根据自己的架构合理分析是否应该整合数据互通。







