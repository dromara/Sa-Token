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
		
        		// 指定 拦截路由 与 放行路由
        		.addInclude("/**").addExclude("/favicon.ico")
				
        		// 认证函数: 每次请求执行 
        		.setAuth(r -> {
					System.out.println("---------- 进入sa-token全局认证 -----------");
					
					// 登录验证 -- 拦截所有路由，并排除/user/doLogin 用于开放登录 
					SaRouterUtil.match("/**", "/user/doLogin", () -> StpUtil.checkLogin());
					
					// 更多拦截处理方式，请参考“路由拦截式鉴权”章节 
        		})
				
        		// 异常处理函数：每次认证函数发生异常时执行此函数 
        		.setError(e -> {
					System.out.println("---------- 进入sa-token异常处理 -----------");
        			return AjaxJson.getError(e.getMessage());
        		})
				
        		// 前置函数：在每次认证函数之前执行
        		.setBeforeAuth(r -> {
        			// ---------- 设置一些安全响应头 ----------
        			SaHolder.getResponse()
        			// 服务器名称 
        			.setServer("sa-server")
        			// 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以 
        			.setHeader("X-Frame-Options", "SAMEORIGIN")
        			// 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
        			.setHeader("X-Frame-Options", "1; mode=block")
        			// 禁用浏览器内容嗅探 
        			.setHeader("X-Content-Type-Options", "nosniff")
        			;
        		})
        		;
	}
	
}
```

### 注意事项
- 在`[认证函数]`里，你可以写和拦截器里一致的代码，进行路由匹配鉴权，参考：[路由拦截式鉴权](/use/route-check)
- 由于过滤器中抛出的异常不进入全局异常处理，所以你必须提供`[异常处理函数]`来处理`[认证函数]`里抛出的异常
- 在`[异常处理函数]`里的返回值，将作为字符串输出到前端，如果需要定制化返回数据，请注意其中的格式转换


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
			// 其它代码... 
		;
	}
	
}
```
		