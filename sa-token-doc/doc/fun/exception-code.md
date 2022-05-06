# 异常细分状态码

--- 

### 获取异常细分状态码

Sa-Token 中的基础异常类是 `SaTokenException`，在此基础上，又针对一些特定场景划分出诸如 `NotLoginException`、`NotPermissionException` 等。

但是框架中异常抛出点远远多于异常种类的划分，比如在 SSO 插件中，[ redirect 重定向地址无效 ] 和 [ ticket 参数值无效 ] 都会导致 SSO 授权的失败，
但是它们抛出的异常都是 `SaSsoException`，如果你需要对这两种异常情形做出不同的处理，仅仅判断异常的 ClassType 显然不够。

为了解决上述需求，Sa-Token 对每个异常抛出点都会指定一个特定的 code 值，就像这样：

``` java
if(SaFoxUtil.isUrl(url) == false) {
	throw new SaSsoException("无效redirect：" + url).setCode(SaSsoExceptionCode.CODE_20001);	
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
		if(e.getCode() == 20001) {
			return SaResult.error("redirect 重定向 url 是一个无效地址");
		}
		if(e.getCode() == 20002) {
			return SaResult.error("redirect 重定向 url 不在 allowUrl 允许的范围内");
		}
		if(e.getCode() == 20004) {
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

!> 目前仅对 sso 插件和 jwt 插件做了异常状态码细分，后续版本升级会支持更多模块

**sa-token-code 核心包：**

| code码值	| 含义									|
| :--------	| :--------								|
| -1		| 代表这个异常在抛出时未指定异常细分状态码	|


**sa-token-sso 单点登录相关：**

| code码值	| 含义												|
| :--------	| :--------											|
| 20001		| `redirect` 重定向 url 是一个无效地址					|
| 20002		| `redirect` 重定向 url 不在 allowUrl 允许的范围内		|
| 20003		| 接口调用方提供的 `secretkey` 秘钥无效					|
| 20004		| 提供的 `ticket` 是无效的								|
| 20005		| 在模式三下，sso-client 调用 sso-server 端 校验ticket接口 时，得到的响应是校验失败	|
| 20006		| 在模式三下，sso-client 调用 sso-server 端 单点注销接口 时，得到的响应是注销失败	|
| 20007		| http 请求调用 提供的 `timestamp` 与当前时间的差距超出允许的范围	|
| 20008		| http 请求调用 提供的 `sign` 无效						|
| 20009		| 本地系统没有配置 `secretkey` 字段						|



**sa-token-jwt 插件相关：**

| code码值	| 含义												|
| :--------	| :--------											|
| 40101		| 对 jwt 字符串解析失败					|
| 40102		| 此 jwt 的签名无效						|
| 40103		| 此 jwt 的 `loginType` 字段不符合预期		|
| 40104		| 此 jwt 已超时							|



















