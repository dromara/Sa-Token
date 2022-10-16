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
    SaBasicUtil.check("sa:123456");
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
// 对当前会话进行 Basic 校验，账号密码为 yml 配置的值（例如：sa-token.basic=sa:123456）
SaBasicUtil.check();

// 对当前会话进行 Basic 校验，账号密码为：`sa / 123456`
SaBasicUtil.check("sa:123456");

// 以注解方式启用 Basic 校验
@SaCheckBasic(account = "sa:123456")
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
				SaRouter.match("/test/**", () -> SaBasicUtil.check("sa:123456"));
			});
}
```

### 3、URL 认证 
除了访问后再输入账号密码外，我们还可以在 URL 中直接拼接账号密码通过 Basic 认证，例如：
``` url
http://sa:123456@127.0.0.1:8081/test/test3
```


--- 

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/up/DisableController.java"
	target="_blank">
	本章代码示例：Sa-Token 账号禁用  —— [ com.pj.cases.up.DisableController.java ]
</a>





















