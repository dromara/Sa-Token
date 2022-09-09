# 路由拦截鉴权
--- 

假设我们有如下需求：
> 项目中所有接口均需要登录认证，只有 '登录接口' 本身对外开放

我们怎么实现呢？给每个接口加上鉴权注解？手写全局拦截器？似乎都不是非常方便。

在这个需求中我们真正需要的是一种基于路由拦截的鉴权模式, 那么在Sa-Token怎么实现路由拦截鉴权呢？



### 1、注册 Sa-Token 路由拦截器
以`SpringBoot2.0`为例, 新建配置类`SaTokenConfigure.java`
``` java 
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	// 注册拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
		registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
				.addPathPatterns("/**")
				.excludePathPatterns("/user/doLogin"); 
	}
}
```
以上代码，我们注册了一个基于 `StpUtil.checkLogin()` 的登录校验拦截器，并且排除了`/user/doLogin`接口用来开放登录（除了`/user/doLogin`以外的所有接口都需要登录才能访问）。

!> `SaInterceptor` 是新版本提供的拦截器，点此 [查看旧版本代码迁移示例](https://blog.csdn.net/shengzhang_/article/details/126458949)。

### 2、校验函数详解  
自定义认证规则：`new SaInterceptor(handle -> StpUtil.checkLogin())` 是最简单的写法，代表只进行登录校验功能。

我们可以往构造函数塞一个完整的 lambda 表达式，来定义详细的校验规则，例如：

``` java 
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册 Sa-Token 拦截器，定义详细认证规则 
		registry.addInterceptor(new SaInterceptor(handler -> {
			// 根据路由划分模块，不同模块不同鉴权 
			SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
			SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
			SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
			SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
			SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
			SaRouter.match("/comment/**", r -> StpUtil.checkPermission("comment"));
		})).addPathPatterns("/**");
	}
}
```

SaRouter.match() 匹配函数有两个参数：
- 参数一：要匹配的path路由。
- 参数二：要执行的校验函数。

在校验函数内不只可以使用 `StpUtil.checkPermission("xxx")` 进行权限校验，你还可以写任意代码，例如：

``` java 
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	// 注册 Sa-Token 的拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册路由拦截器，自定义认证规则 
		registry.addInterceptor(new SaInterceptor(handler -> {
			
			// 登录认证 -- 拦截所有路由，并排除/user/doLogin 用于开放登录 
			SaRouter.match("/**", "/user/doLogin", r -> StpUtil.checkLogin());

			// 角色认证 -- 拦截以 admin 开头的路由，必须具备 admin 角色或者 super-admin 角色才可以通过认证 
			SaRouter.match("/admin/**", r -> StpUtil.checkRoleOr("admin", "super-admin"));

			// 权限认证 -- 不同模块认证不同权限 
			SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
			SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
			SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
			SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
			SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
			SaRouter.match("/comment/**", r -> StpUtil.checkPermission("comment"));
			
			// 甚至你可以随意的写一个打印语句
			SaRouter.match("/**", r -> System.out.println("----啦啦啦----"));

            // 连缀写法
            SaRouter.match("/**").check(r -> System.out.println("----啦啦啦----"));
			
		})).addPathPatterns("/**");
	}
}
```


### 3、匹配特征详解

除了上述示例的 path 路由匹配，还可以根据很多其它特征进行匹配，以下是所有可匹配的特征：

``` java
// 基础写法样例：匹配一个path，执行一个校验函数 
SaRouter.match("/user/**").check(r -> StpUtil.checkLogin());

// 根据 path 路由匹配   ——— 支持写多个path，支持写 restful 风格路由 
// 功能说明: 使用 /user , /goods 或者 /art/get 开头的任意路由都将进入 check 方法
SaRouter.match("/user/**", "/goods/**", "/art/get/{id}").check( /* 要执行的校验函数 */ );

// 根据 path 路由排除匹配 
// 功能说明: 使用 .html , .css 或者 .js 结尾的任意路由都将跳过, 不会进入 check 方法
SaRouter.match("/**").notMatch("*.html", "*.css", "*.js").check( /* 要执行的校验函数 */ );

// 根据请求类型匹配 
SaRouter.match(SaHttpMethod.GET).check( /* 要执行的校验函数 */ );

// 根据一个 boolean 条件进行匹配 
SaRouter.match( StpUtil.isLogin() ).check( /* 要执行的校验函数 */ );

// 根据一个返回 boolean 结果的lambda表达式匹配 
SaRouter.match( r -> StpUtil.isLogin() ).check( /* 要执行的校验函数 */ );

// 多个条件一起使用 
// 功能说明: 必须是 Get 请求 并且 请求路径以 `/user/` 开头 
SaRouter.match(SaHttpMethod.GET).match("/user/**").check( /* 要执行的校验函数 */ );

// 可以无限连缀下去 
// 功能说明: 同时满足 Get 方式请求, 且路由以 /admin 开头, 路由中间带有 /send/ 字符串, 路由结尾不能是 .js 和 .css
SaRouter
	.match(SaHttpMethod.GET)
	.match("/admin/**")
	.match("/**/send/**") 
	.notMatch("/**/*.js")
	.notMatch("/**/*.css")
	// ....
	.check( /* 只有上述所有条件都匹配成功，才会执行最后的check校验函数 */ );
```



### 4、提前退出匹配链 
使用 `SaRouter.stop()` 可以提前退出匹配链，例：

``` java
registry.addInterceptor(new SaInterceptor(handler -> {
	SaRouter.match("/**").check(r -> System.out.println("进入1"));
	SaRouter.match("/**").check(r -> System.out.println("进入2")).stop();
	SaRouter.match("/**").check(r -> System.out.println("进入3"));
	SaRouter.match("/**").check(r -> System.out.println("进入4"));
	SaRouter.match("/**").check(r -> System.out.println("进入5"));
})).addPathPatterns("/**");
```
如上示例，代码运行至第2条匹配链时，会在stop函数处提前退出整个匹配函数，从而忽略掉剩余的所有match匹配 

除了`stop()`函数，`SaRouter`还提供了 `back()` 函数，用于：停止匹配，结束执行，直接向前端返回结果
``` java
// 执行back函数后将停止匹配，也不会进入Controller，而是直接将 back参数 作为返回值输出到前端
SaRouter.match("/user/back").back("要返回到前端的内容");
```

stop() 与 back() 函数的区别在于：
- `SaRouter.stop()` 会停止匹配，进入Controller。
- `SaRouter.back()` 会停止匹配，直接返回结果到前端。


### 5、使用free打开一个独立的作用域

``` java
// 进入 free 独立作用域 
SaRouter.match("/**").free(r -> {
	SaRouter.match("/a/**").check(/* --- */);
	SaRouter.match("/a/**").check(/* --- */).stop();
	SaRouter.match("/a/**").check(/* --- */);
});
// 执行 stop() 函数跳出 free 后继续执行下面的 match 匹配 
SaRouter.match("/**").check(/* --- */);
```

free() 的作用是：打开一个独立的作用域，使内部的 stop() 不再一次性跳出整个 Auth 函数，而是仅仅跳出当前 free 作用域。


### 6、使用注解忽略掉路由拦截校验

我们可以使用 `@SaIgnore` 注解，忽略掉路由拦截认证：

1、先配置好了拦截规则：
``` java
@Override
public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(new SaInterceptor(handler -> {
		// 根据路由划分模块，不同模块不同鉴权 
		SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
		SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
		SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
		// ... 
	})).addPathPatterns("/**");
}
```

2、然后在 `Controller` 里又添加了忽略校验的注解
``` java
@SaIgnore
@RequestMapping("/user/getList")
public SaResult getList() {
	System.out.println("------------ 访问进来方法"); 
	return SaResult.ok(); 
}
```

请求将会跳过拦截器的校验，直接进入 Controller 的方法中。

**注意点：此注解的忽略效果只针对 SaInterceptor拦截器 和 AOP注解鉴权 生效，对自定义拦截器与过滤器不生效。**

