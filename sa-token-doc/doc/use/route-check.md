# 路由拦截式鉴权
--- 

假设我们有如下需求：
> 项目中所有接口均需要登录验证，只有'登录接口'本身对外开放

我们怎么实现呢？给每个接口加上鉴权注解？手写全局拦截器？似乎都不是非常方便。<br/>
在这个需求中我们真正需要的是一种基于路由拦截的鉴权模式, 那么在sa-token怎么实现路由拦截鉴权呢？



## 1、注册路由拦截器
以`SpringBoot2.0`为例, 新建配置类`MySaTokenConfig.java`
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
以上代码，我们注册了一个登录验证拦截器，并且排除了`/user/doLogin`接口用来开放登录（除了`/user/doLogin`以外的所有接口都需要登录才能访问） <br>
那么我们如何进行权限认证拦截呢，且往下看


## 2、自定义权限验证规则
你可以使用函数式编程自定义验证规则

``` java 
@Configuration
public class MySaTokenConfig implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册路由拦截器，自定义验证规则 
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

## 3、完整示例
所有用法示例：

``` java 
@Configuration
public class MySaTokenConfig implements WebMvcConfigurer {
	// 注册sa-token的拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册路由拦截器，自定义验证规则 
		registry.addInterceptor(new SaRouteInterceptor((request, response, handler) -> {
			
			// 登录验证 -- 拦截所有路由，并排除/user/doLogin 用于开放登录 
			SaRouterUtil.match("/**", "/user/doLogin", () -> StpUtil.checkLogin());
			
			// 登录验证 -- 排除多个路径
			SaRouterUtil.match(Arrays.asList("/**"), Arrays.asList("/user/doLogin", "/user/reg"), () -> StpUtil.checkLogin());
						
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
			
            // 检查请求方式 
			SaRouterUtil.match("/notice/**", () -> {
				if(request.getMethod().equals(HttpMethod.GET.toString())) {
					StpUtil.checkPermission("notice");
				}
			});
			
			// 在多账号模式下，可以使用任意StpUtil进行校验
			SaRouterUtil.match("/user/**", () -> StpUserUtil.checkLogin());
			
		})).addPathPatterns("/**");
	}
}
```










