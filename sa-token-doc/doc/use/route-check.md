# 路由拦截式鉴权
--- 

假设我们有如下需求：
> 项目中所有接口均需要登录验证，只有'登录接口'本身对外开放

我们怎么实现呢？给每个接口加上鉴权注解？手写全局拦截器？似乎都不是非常方便。<br/>
在这个需求中我们真正需要的是一种基于路由拦截的鉴权模式, 那么在sa-token怎么实现路由拦截鉴权呢？



## 1、注册路由拦截器
以`springboot2.0`为例, 新建配置类`MySaTokenConfig.java`
``` java 
@Configuration
public class MySaTokenConfig implements WebMvcConfigurer {
	// 注册sa-token的登录拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册登录拦截器，并排除登录接口地址 
		registry.addInterceptor(new SaRouteInterceptor()).addPathPatterns("/**").excludePathPatterns("/user/doLogin"); 
	}
}
```
以上代码，我们注册了一个登录验证拦截器，并且排除了`/user/doLogin`接口用来开放登录 <br>
那么我们如何进行权限认证拦截呢，且往下看


## 2、所有拦截器示例
``` java 
@Configuration
public class MySaTokenConfig implements WebMvcConfigurer {
	// 注册sa-token的所有拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		// 注册一个登录验证拦截器 
		registry.addInterceptor(SaRouteInterceptor.createLoginVal()).addPathPatterns("/**").excludePathPatterns("/user/doLogin"); 
		
		// 注册一个角色认证拦截器 
		registry.addInterceptor(SaRouteInterceptor.createRoleVal("super-admin")).addPathPatterns("/**"); 

		// 注册一个权限认证拦截器 
		registry.addInterceptor(SaRouteInterceptor.createPermissionVal("user:add", "user:deelete")).addPathPatterns("/UserController/**"); 

		// 注册一个自定义认证拦截器 (可以写任意认证代码)
		registry.addInterceptor(new SaRouteInterceptor((request, response, handler)->{
			System.out.println("---------- 进入自定义认证 --------------- ");
			// 你可以在这里写任意认证代码, 例如: StpUtil.checkLogin(); 
		})).addPathPatterns("/**");
		
	}
}
```
（你不必像上面的示例一样注册所有拦截器，只要按需注册即可 ）



## 3、让我们利用自定义拦截器做点快活的事情
你可以根据路由划分模块，不同模块不同鉴权 

``` java 
@Configuration
public class MySaTokenConfig implements WebMvcConfigurer {
	// 注册sa-token的所有拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SaRouteInterceptor((request, response, handler)->{
			// 根据路由划分模块，不同模块不同鉴权 
			SaRouterUtil.match("/user/**", () -> StpUtil.checkPermission("user"));
			SaRouterUtil.match("/admin/**", () -> StpUtil.checkPermission("admin"));
			SaRouterUtil.match("/goods/**", () -> StpUtil.checkPermission("goods"));
			SaRouterUtil.match("/orders/**", () -> StpUtil.checkPermission("orders"));
			SaRouterUtil.match("/notice/**", () -> StpUtil.checkPermission("notice"));
			SaRouterUtil.match("/comment/**", () -> StpUtil.checkPermission("comment"));
		})).addPathPatterns("/**");
	}
}
```


## 4、完整的示例 
最终的代码，可能会类似于下面的样子：

``` java 
@Configuration
public class MySaTokenConfig implements WebMvcConfigurer {
	// 注册sa-token的拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 自定义验证拦截器 
		registry.addInterceptor(new SaRouteInterceptor((request, response, handler) -> {
			
			// 登录验证 -- 拦截所有路由，并排除/user/doLogin 用于开放登录 
			SaRouterUtil.match("/**", "/user/doLogin", () -> StpUtil.checkLogin());
			
			// 角色认证 -- 拦截以 admin 开头的路由，必须具备[admin]角色或者[super-admin]角色才可以通过认证 
			SaRouterUtil.match("/admin/**", () -> StpUtil.checkRoleOr("admin", "super-admin"));
			
			// 权限认证 -- 不同模块, 校验不同权限 
			SaRouterUtil.match("/user/**", () -> StpUtil.checkPermission("user"));
			SaRouterUtil.match("/admin/**", () -> StpUtil.checkPermission("admin"));
			SaRouterUtil.match("/goods/**", () -> StpUtil.checkPermission("goods"));
			SaRouterUtil.match("/orders/**", () -> StpUtil.checkPermission("orders"));
			SaRouterUtil.match("/notice/**", () -> StpUtil.checkPermission("notice"));
			SaRouterUtil.match("/comment/**", () -> StpUtil.checkPermission("comment"));
			
			// 匹配 restful 风格路由 
			SaRouterUtil.match("/article/get/{id}", () -> StpUtil.checkPermission("article"));
			
		})).addPathPatterns("/**");
	}
}
```








