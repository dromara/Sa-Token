# Http Basic 认证 

Http Basic 是 http 协议中最基础的认证方式，其有两个特点：
- 简单、易集成。
- 功能支持度低。

在 Sa-Token 中使用 Http Basic 认证非常简单，只需调用几个简单的方法 

--- 

### 1、启用 Http Basic 认证 

首先我们在一个接口中，调用 Http Basic 校验：
``` java
@RequestMapping("test3")
public SaResult test3() {
    SaHttpBasicUtil.check("sa:123456");
	// ... 其它代码
	return SaResult.ok();
}
```

全局异常处理：
``` java
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
}
```

然后我们访问这个接口时，浏览器会强制弹出一个表单：

![sa-basic.png](https://oss.dev33.cn/sa-token/doc/sa-basic.png 's-w-sh')


当我们输入账号密码后 `（sa / 123456）`，才可以继续访问数据：

![sa-basic-ok.png](https://oss.dev33.cn/sa-token/doc/sa-basic-ok.png 's-w-sh')


### 2、其它启用方式 
``` java
// 对当前会话进行 Http Basic 校验，账号密码为 yml 配置的值（例如：sa-token.http-basic=sa:123456）
SaHttpBasicUtil.check();

// 对当前会话进行 Http Basic 校验，账号密码为：`sa / 123456`
SaHttpBasicUtil.check("sa:123456");

// 以注解方式启用 Http Basic 校验
@SaCheckHttpBasic(account = "sa:123456")
@RequestMapping("test3")
public SaResult test3() {
	return SaResult.ok();
}

// 在全局拦截器 或 过滤器中启用 Basic 认证 
@Bean
public SaServletFilter getSaServletFilter() {
	return new SaServletFilter()
			.addInclude("/**").addExclude("/favicon.ico")
			.setAuth(obj -> {
				SaRouter.match("/test/**", () -> SaHttpBasicUtil.check("sa:123456"));
			});
}
```

### 3、URL 认证 
除了访问后再输入账号密码外，我们还可以在 URL 中直接拼接账号密码通过 Basic 认证，例如：
``` url
http://sa:123456@127.0.0.1:8081/test/test3
```


### 4、Http Digest 认证 

Http Digest 认证是 Http Basic 认证的升级版，Http Digest 在提交请求时不会使用明文方式传输认证信息，而是使用一定的规则加密后提交。
不过对于开发者来讲，开启 Http Digest 认证校验的流程与 Http Basic 认证基本是一致的。

``` java
// 测试 Http Digest 认证   浏览器访问： http://localhost:8081/test/testDigest
@RequestMapping("testDigest")
public SaResult testDigest() {
	SaHttpDigestUtil.check("sa", "123456");
	return SaResult.ok();
}

// 使用注解方式开启 Http Digest 认证
@SaCheckHttpDigest("sa:123456")
@RequestMapping("testDigest2")
public SaResult testDigest() {
	return SaResult.ok();
}


// 对当前会话进行 Http Digest 校验，账号密码为 yml 配置的值（例如：sa-token.http-digest=sa:123456）
SaHttpDigestUtil.check();
```

与上面的 Http Basic 认证一致，在访问这个路由时，浏览器会强制弹出一个表单，客户端输入正确的账号密码后即可通过校验。

同样的，Http Digest 也支持在浏览器访问接口时直接使用 @ 符号拼接账号密码信息，使客户端直接通过校验。

``` url
http://sa:123456@127.0.0.1:8081/test/testDigest
```



--- 

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/up/HttpBasicController.java"
	target="_blank">
	本章代码示例：Sa-Token Http Basic 认证 —— [ HttpBasicController.java ]
</a>





















