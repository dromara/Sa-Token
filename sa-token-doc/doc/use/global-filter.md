# 全局过滤器
--- 

### 组件简述

之前的章节中，我们学习了“根据拦截器实现路由拦截鉴权”，其实在大多数web框架中，使用过滤器可以实现同样的功能，本章我们就利用sa-token全局过滤器来实现路由拦截器鉴权。

首先我们先梳理清楚一个问题，既然拦截器已经可以实现路由鉴权，为什么还要用过滤器再实现一遍呢？简而言之：
1. 相比于拦截器，过滤器更加底层，执行时机更靠前，有利于防渗透扫描
2. 过滤器可以拦截静态资源，方便我们做一些权限控制
3. 部分Web框架根本就没有提供拦截器功能，但几乎所有的Web框架都会提供过滤器机制

但是过滤器也有一些缺点，比如：
1. 由于太过底层，导致无法率先拿到`HandlerMethod`对象，无法据此添加一些额外功能
2. 由于拦截的太全面了，导致我们需要对很多特殊路由(如`/favicon.ico`)做一些额外处理
3. 在Spring中，过滤器中抛出的异常无法进入全局`@ExceptionHandler`，我们必须额外编写代码进行异常处理

Sa-Token同时提供过滤器和拦截器机制，不是为了让谁替代谁，而是为了让大家根据自己的实际业务合理选择，拥有更多的发挥空间。


### 注册过滤器
同拦截器一样，为了避免不必要的性能浪费，sa-token全局过滤器默认处于关闭状态，若要使用过滤器组件，首先你需要注册它到项目中：
``` java
/**
 * [Sa-Token 权限认证] 配置类 
 * @author kong
 */
@Configuration
public class SaTokenConfigure {
	
	/**
	 * 注册 [sa-token全局过滤器] 
	 */
	@Bean
	public SaServletFilter getSaReactorFilter() {
		return new SaServletFilter()
				.addInclude("/**")
				.addExclude("/favicon.ico");
	}
	
	/**
	 * 注册 [sa-token全局过滤器-认证策略] 
	 */
	@Bean
	public SaFilterStrategy getSaFilterStrategy() {
		return r -> {
			System.out.println("---------- 进入sa-token全局过滤器 -----------");
			
			// 登录验证 -- 拦截所有路由，并排除/user/doLogin 用于开放登录 
            SaRouterUtil.match("/**", "/user/doLogin", () -> StpUtil.checkLogin());
			
			// 权限认证 -- 不同模块, 校验不同权限 
			SaRouterUtil.match("/user/**", () -> StpUtil.checkPermission("user"));
			SaRouterUtil.match("/admin/**", () -> StpUtil.checkPermission("admin"));
			SaRouterUtil.match("/goods/**", () -> StpUtil.checkPermission("goods"));
			SaRouterUtil.match("/orders/**", () -> StpUtil.checkPermission("orders"));
			SaRouterUtil.match("/notice/**", () -> StpUtil.checkPermission("notice"));
			SaRouterUtil.match("/comment/**", () -> StpUtil.checkPermission("comment"));
			
			// 匹配 restful 风格路由 
			SaRouterUtil.match("/article/get/{id}", () -> StpUtil.checkPermission("article"));
		};
	}
	
	/**
	 * 注册 [sa-token全局过滤器-异常处理策略] 
	 */
	@Bean
	public SaFilterErrorStrategy getSaFilterErrorStrategy() {
		return e -> AjaxJson.getError(e.getMessage());
	}
	
}
```

### 注意事项
- 在`[认证策略]`里，你可以和拦截器里一致的代码，进行路由匹配鉴权
- 由于过滤器中抛出的异常不进入全局异常处理，所以你必须提供`[异常处理策略]`来处理`[认证策略]`里抛出的异常
- 在`[异常处理策略]`里的返回值，将作为字符串输出到前端，如果需要定制化返回数据，请注意其中的格式转换


### 在WebFlux中使用过滤器
`Spring WebFlux`中不提供拦截器机制，因此若你的项目需要路由鉴权功能，过滤器是你唯一的选择，在`Spring WebFlux`注册过滤器的流程与上述流程几乎完全一致，
除了您需要将过滤器名称由`SaServletFilter`更换为`SaReactorFilter`以外，其它所有步骤均可参考以上示例
``` java
/**
 * [Sa-Token 权限认证] 配置类 
 * @author kong
 */
@Configuration
public class SaTokenConfigure {
		
	/**
	 * 注册 [sa-token全局过滤器] 
	 */
	@Bean
	public SaReactorFilter getSaReactorFilter() {
		return new SaReactorFilter()
				.addInclude("/**")
				.addExclude("/favicon.ico");
	}
	
	// 其它代码 ... 
	
}
```
		