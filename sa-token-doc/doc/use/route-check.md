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














