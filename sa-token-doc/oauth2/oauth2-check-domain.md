# OAuth2 整合-配置域名校验

--- 

### 1、code 劫持攻击
在前面章节的 OAuth-Server 搭建示例中：

``` java
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	// 根据 clientId 获取 Client 信息
	@Override
	public SaClientModel getClientModel(String clientId) {
		if("1001".equals(clientId)) {
			return new SaClientModel()
					// ...
					.addAllowRedirectUris("*")    // 所有允许授权的 url
					// ...
		}
		return null;
	}
	// 其它代码 ... 
}
```

配置项 `AllowRedirectUris` 意为配置此 `Client` 端所有允许的授权地址，不在此配置项中的 URL 将无法下发 `code` 授权码。

为了方便测试，上述代码将其配置为`*`，但是，<font color="#FF0000" >在生产环境中，此配置项绝对不能配置为 * </font>，否则会有被 `code` 劫持的风险。

假设攻击者根据模仿我们的授权地址，巧妙的构造一个URL：

> [http://sa-oauth-server.com:8000/oauth2/authorize?response_type=code&client_id=1001&redirect_uri=https://www.baidu.com](http://sa-oauth-server.com:8000/oauth2/authorize?response_type=code&client_id=1001&redirect_uri=https://www.baidu.com)

当不知情的小红被诱导访问了这个 URL 时，它将被重定向至百度首页。

![oauth2-ticket-jc](https://oss.dev33.cn/sa-token/doc/oauth2-new/oauth2-ticket-jc.png 's-w-sh')

可以看到，代表着用户身份的 code 授权码也显现到了URL之中，借此漏洞，攻击者完全可以构建一个 URL 将小红的 code 授权码自动提交到攻击者自己的服务器，伪造小红身份登录网站。


### 2、防范方法

造成此漏洞的直接原因就是我们对此 client 配置了过于宽泛的 `AllowRedirectUris` 允许授权地址，防范的方法也很简单，就是缩小 `AllowRedirectUris` 授权范围。

我们将其配置为一个具体的URL：

``` java
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	// 根据 clientId 获取 Client 信息
	@Override
	public SaClientModel getClientModel(String clientId) {
		if("1001".equals(clientId)) {
			return new SaClientModel()
					// ...
					.addAllowRedirectUris("http://sa-oauth-client.com:8002/")    // 所有允许授权的 url
					// ...
		}
		return null;
	}
	// 其它代码 ... 
}
```

再次访问上述链接：

![oauth2-feifa-rf](https://oss.dev33.cn/sa-token/doc/oauth2-new/oauth2-feifa-rf.png 's-w-sh')

URL 没有通过校验，拒绝授权！


### 3、配置安全性参考表

| 配置方式		| 举例											| 安全性								|  建议									|
| :--------		| :--------										| :--------							| :--------								|
| 配置为*		| `*`											| <font color="#F00" >低</font>		| **<font color="#F00" >禁止在生产环境下使用</font>**	|
| 配置到域名		| `http://sa-oauth-client.com:8002/*`			| <font color="#F70" >中</font>		| <font color="#F70" >不建议在生产环境下使用</font>	|
| 配置到详细地址	| `http://sa-oauth-client.com:8002/xxx/xxx`		| <font color="#080" >高</font>		| <font color="#080" >可以在生产环境下使用</font>	|


### 4、其它规则

1、AllowRedirectUris 配置的地址不允许出现 `@` 字符。

- 反例：`http://user@sa-token.cc`
- 反例：`http://sa-oauth-client.com@sa-token.cc`

*详见源码：[SaOAuth2Template.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-plugin/sa-token-oauth2/src/main/java/cn/dev33/satoken/oauth2/template/SaOAuth2Template.java) 
`checkRedirectUri` 方法。*

2、AllowRedirectUris 配置的地址 `*` 通配符只允许出现在字符串末尾，不允许出现在字符串中间位置。

- 反例：`http*://sa-oauth-client.com/`
- 反例：`http://*.sa-oauth-client.com/`

*详见源码： [SaOAuth2Template.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-plugin/sa-token-oauth2/src/main/java/cn/dev33/satoken/oauth2/template/SaOAuth2Template.java) 
`checkRedirectUriListNormalStaticMethod` 方法。*

参考：[github/issue/529](https://github.com/dromara/Sa-Token/issues/529)
感谢这位 `@m4ra7h0n` 用户反馈的漏洞。

