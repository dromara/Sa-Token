# Sa-Token-SSO整合-常见问题总结

--- 


### 一、何时引导用户去登录？

以下解决方案三选一：

##### 1.1、前端按钮跳转 
前端页面准备一个**`[登录]`**按钮，当用户点击按钮时，跳转到登录接口 
``` js
<a href="javascript:location.href='/sso/login?back=' + encodeURIComponent(location.href);">登录</a>
```

##### 1.2、后端拦截重定向
在后端注册全局过滤器（或拦截器、或全局异常处理），拦截需要登录后才能访问的页面资源，将未登录的访问重定向至登录接口 
``` java
/**
 * Sa-Token 配置类 
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	/** 注册 [Sa-Token全局过滤器] */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
        		.addInclude("/**")
        		.addExclude("/sso/*", "/favicon.ico")
        		.setAuth(r -> {
        			if(StpUtil.isLogin() == false) {
        				String back = SaFoxUtil.joinParam(SaHolder.getRequest().getUrl(), SpringMVCUtil.getRequest().getQueryString());
        				SaHolder.getResponse().redirect("/sso/login?back=" + SaFoxUtil.encodeUrl(back));
        				SaRouter.back();
        			}
        		})
        		;
    }
}
```

##### 1.3、后端拦截 + 前端跳转 
首先，后端仍需要提供拦截，但是不直接引导用户重定向，而是返回未登录的提示信息 
```  java
/**
 * Sa-Token 配置类 
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	/** 注册 [Sa-Token全局过滤器] */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
        		.addInclude("/**")
        		.addExclude("/sso/*", "/favicon.ico")
        		.setAuth(r -> {
        			if(StpUtil.isLogin() == false) {
        				// 与前端约定好，code=401时代表会话未登录 
        				SaRouter.back(SaResult.ok().setCode(401));
        			}
        		})
        		;
    }
}
```

前端接受到返回结果 `code=401` 时，开始跳转至登录接口
``` js
if(res.code == 401) {
	location.href = '/sso/login?back=' + encodeURIComponent(location.href);
}
```

这种方案比较适合以 Ajax 访问的 RestAPI 接口重定向 




### 二、定制化开发 

##### 2.1、如何自定义登录视图？
- 方式一：在demo示例中直接更改页面代码 
- 方式二：在配置中配置登录视图地址 

``` java
cfg.sso
// 配置：未登录时返回的View 
.setNotLoginView(() -> {
	return new ModelAndView("xxx.html");
})
```

##### 2.2、如何自定义登录API的接口？
根据需求点选择解决方案：

**2.2.1、如果只是想在 setDoLoginHandle 函数里获取除 name、pwd 以外的参数？**
``` java
// 在任意代码处获取前端提交的参数 
String xxx = SaHolder.getRequest().getParam("xxx");
```

**2.2.2、想完全自定义一个接口来接受前端登录请求？**
``` java
// 直接定义一个拦截路由为 `/sso/doLogin` 的接口即可 
@RequestMapping("/sso/doLogin")
public SaResult ss(String name, String pwd) {
	System.out.println("------ 请求进入了自定义的API接口 ---------- ");
	if("sa".equals(name) && "123456".equals(pwd)) {
		StpUtil.login(10001);
		return SaResult.ok("登录成功！");
	}
	return SaResult.error("登录失败！");
}
```

**2.2.3、不想使用`/sso/doLogin`这个接口，想自定义一个API地址？**

答：直接在前端更改点击按钮时 Ajax 的请求地址即可 


### 三、常见疑问

##### 问：在模式一与模式二中，Client端 必须通过 Alone-Redis 插件来访问Redis吗？

答：不必须，只是推荐，权限缓存与业务缓存分离后会减少SSO-Redis的访问压力，且可以避免多个Client端的缓存读写冲突


##### 问：将旧有系统改造为单点登录时，应该注意哪些？
建议不要把其中一个系统改造为SSO服务端，而是新起一个项目作为Server端，所有旧有项目全部作为Client端与此对接 



