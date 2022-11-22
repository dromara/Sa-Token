# 异常细分状态码

--- 

### 获取异常细分状态码

Sa-Token 中的基础异常类是 `SaTokenException`，在此基础上，又针对一些特定场景划分出诸如 `NotLoginException`、`NotPermissionException` 等。

但是框架中异常抛出点远远多于异常种类的划分，比如在 SSO 插件中，[ redirect 重定向地址无效 ] 和 [ ticket 参数值无效 ] 都会导致 SSO 授权的失败，
但是它们抛出的异常都是 `SaSsoException`，如果你需要对这两种异常情形做出不同的处理，仅仅判断异常的 ClassType 显然不够。

为了解决上述需求，Sa-Token 对每个异常抛出点都会指定一个特定的 code 值，就像这样：

``` java
if(SaFoxUtil.isUrl(url) == false) {
	throw new SaSsoException("无效redirect：" + url).setCode(SaSsoErrorCode.CODE_30001);	
}
```

就像是打上一个特定的标记，不同异常情形标记的 code 码值也会不同，这就为你精细化异常处理提供了前提。

要在捕获异常时获取这个 code 码也非常简单：

``` java
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(SaTokenException.class)
	public SaResult handlerSaTokenException(SaTokenException e) {
		
		// 根据不同异常细分状态码返回不同的提示 
		if(e.getCode() == 30001) {
			return SaResult.error("redirect 重定向 url 是一个无效地址");
		}
		if(e.getCode() == 30002) {
			return SaResult.error("redirect 重定向 url 不在 allowUrl 允许的范围内");
		}
		if(e.getCode() == 30004) {
			return SaResult.error("提供的 ticket 是无效的");
		}
		// 更多 code 码判断 ... 
		
		// 默认的提示 
		return SaResult.error("服务器繁忙，请稍后重试...");
	}
}
```

SaToken 中的所有异常都是继承于 `SaTokenException` 的，也就是说，所有异常你都可以通过 `e.getCode()` 的方式获取对应的异常细分状态码。





### 异常细分状态码-参照表

!> 部分插件因异常抛出点较少，暂未做状态码细分处理

#### sa-token-code 核心包

| code码值	| 含义									|
| :--------	| :--------								|
| -1		| 代表这个异常在抛出时未指定异常细分状态码	|
| 10001		| 未能获取有效的上下文处理器				|
| 10002		| 未能获取有效的上下文					|
| 10003		| JSON 转换器未实现						|
| 10011		| 未能从全局 StpLogic 集合中找到对应 type 的 StpLogic					|
| 10021		| 指定的配置文件加载失败					|
| 10022		| 配置文件属性无法正常读取				|
| 10031		| 重置的侦听器集合不可以为空				|
| 10032		| 注册的侦听器不可以为空					|
| 10301		| 提供的 Same-Token 是无效的				|
| 10311		| 表示未能通过 Http Basic 认证校验		|
| 10321		| 提供的 HttpMethod 是无效的				|
| 11001		| 未能读取到有效Token						|
| 11002		| 登录时的账号id值为空					|
| 11003		| 更改 Token 指向的 账号Id 时，账号Id值为空						|
| 11011		| 未能读取到有效Token						|
| 11012		| Token无效 								|
| 11013		| Token已过期							|
| 11014		| Token已被顶下线						|
| 11015		| Token已被踢下线						|
| 11016		| Token已临时过期						|
| 11031		| 在未集成 sa-token-jwt 插件时调用 getExtra() 抛出异常					|
| 11041		| 缺少指定的角色							|
| 11051		| 缺少指定的权限							|
| 11061		| 当前账号未通过服务封禁校验				|
| 11062		| 提供要解禁的账号无效					|
| 11063		| 提供要解禁的服务无效					|
| 11064		| 提供要解禁的等级无效					|
| 11071		| 二级认证校验未通过						|
| 12001		| 请求中缺少指定的参数					|
| 12002		| 构建 Cookie 时缺少 name 参数			|
| 12003		| 构建 Cookie 时缺少 value 参数		|
| 12101		| Base64 编码异常					|
| 12102		| Base64 解码异常					|
| 12103		| URL 编码异常						|
| 12104		| URL 解码异常						|
| 12111		| md5 加密异常						|
| 12112		| sha1 加密异常						|
| 12113		| sha256 加密异常					|
| 12114		| AES 加密异常						|
| 12115		| AES 解密异常						|
| 12116		| RSA 公钥加密异常					|
| 12117		| RSA 私钥加密异常					|
| 12118		| RSA 公钥解密异常					|
| 12119		| RSA 私钥解密异常					|


#### sa-token-servlet

| code码值	| 含义												|
| :--------	| :--------											|
| 20001		| 转发失败											|
| 20002		| 重定向失败											|


#### sa-token-spring-boot-starter

| code码值	| 含义												|
| :--------	| :--------											|
| 20101		| 企图在非 Web 上下文获取 Request、Response 等对象		|
| 20103		| 对象转 JSON 字符串失败								|
| 20104		| JSON 字符串转 Map 失败								|
| 20105		| 默认的 Filter 异常处理函数							|


#### sa-token-reactor-spring-boot-starter

| code码值	| 含义												|
| :--------	| :--------											|
| 20203		| 对象转 JSON 字符串失败								|
| 20204		| JSON 字符串转 Map 失败								|
| 20205		| 默认的 Filter 异常处理函数							|


#### sa-token-solon-plugin

| code码值	| 含义												|
| :--------	| :--------											|
| 20301		| 默认的拦截器异常处理函数							|
| 20302		| 默认的 Filter 异常处理函数							|


#### sa-token-sso 单点登录相关：

| code码值	| 含义												|
| :--------	| :--------											|
| 30001		| `redirect` 重定向 url 是一个无效地址					|
| 30002		| `redirect` 重定向 url 不在 allowUrl 允许的范围内		|
| 30003		| 接口调用方提供的 `secretkey` 秘钥无效					|
| 30004		| 提供的 `ticket` 是无效的								|
| 30005		| 在模式三下，sso-client 调用 sso-server 端 校验ticket接口 时，得到的响应是校验失败	|
| 30006		| 在模式三下，sso-client 调用 sso-server 端 单点注销接口 时，得到的响应是注销失败	|
| 30007		| http 请求调用 提供的 `timestamp` 与当前时间的差距超出允许的范围	|
| 30008		| http 请求调用 提供的 `sign` 无效						|
| 30009		| 本地系统没有配置 `secretkey` 字段						|
| 30010		| 本地系统没有配置 http 请求处理器							|


#### sa-token-oauth2 相关：
| code码值	| 含义												|
| :--------	| :--------											|
| 30101		| client_id 不可为空					|
| 30102		| scope 不可为空					|
| 30103		| redirect_uri 不可为空					|
| 30104		| LoginId 不可为空					|
| 30105		| 无效client_id					|
| 30106		| 无效access_token					|
| 30107		| 无效 client_token					|
| 30108		| Access-Token 不具备指定的 Scope					|
| 30109		| Client-Token 不具备指定的 Scope					|
| 30110		| 无效 code 码					|
| 30111		| 无效 Refresh-Token					|
| 30112		| 请求的Scope暂未签约					|
| 30113		| 无效redirect_url					|
| 30114		| 非法redirect_url					|
| 30115		| 无效client_secret					|
| 30116		| 请求的Scope暂未签约					|
| 30117		| 无效code					|
| 30118		| 无效client_id					|
| 30119		| 无效client_secret					|
| 30120		| 无效redirect_uri					|
| 30121		| 无效refresh_token					|
| 30122		| 无效client_id					|
| 30123		| 无效client_secret					|
| 30124		| 无效client_id					|
| 30125		| 无效response_type					|
| 30131		| 暂未开放授权码模式					|
| 30132		| 暂未开放隐藏式模式					|
| 30133		| 暂未开放密码式模式					|
| 30134		| 暂未开放凭证式模式					|


#### sa-token-jwt 插件相关：

| code码值	| 含义									|
| :--------	| :--------								|
| 30201		| 对 jwt 字符串解析失败					|
| 30202		| 此 jwt 的签名无效						|
| 30203		| 此 jwt 的 `loginType` 字段不符合预期	|
| 30204		| 此 jwt 已超时							|
| 30205		| 没有配置jwt秘钥						|
| 30206		| 登录时提供的账号id为空					|


#### sa-token-temp-jwt 插件相关：
| code码值	| 含义									|
| :--------	| :--------								|
| 30301		| jwt 模式没有提供秘钥					|
| 30302		| jwt 模式不可以删除 Token				|
| 30303		| Token已超时							|

































