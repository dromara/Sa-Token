# Sa-Token-SSO整合-常见问题总结

--- 


### 一、何时引导用户去登录？

以下方案三选一：

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




### 二、如何自定义登录视图？

- 方式一：在demo示例中直接更改页面代码 
- 方式二：在配置中配置登录视图地址 

``` java
cfg.sso
// 配置：未登录时返回的View 
.setNotLoginView(() -> {
	return new ModelAndView("xxx.html");
})
```


### 三、如何自定义登录API的接口？
根据需求点选择解决方案：

##### 3.1、如果只是想在 setDoLoginHandle 函数里获取除 name、pwd 以外的参数？
``` java
// 在任意代码处获取前端提交的参数 
String xxx = SaHolder.getRequest().getParam("xxx");
```

##### 3.2、想完全自定义一个接口来接受前端登录请求？
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

##### 3.3、不想使用`/sso/doLogin`这个接口，想自定义一个API地址？

答：直接在前端更改点击按钮时 Ajax 的请求地址即可 


### 四、前后端分离架构下的整合方案

如果我们已有的系统是前后端分离模式，我们显然不能为了接入SSO而改造系统的基础架构，官方仓库的示例采用的是前后端一体方案，要将其改造为前后台分离架构模式非常简单

以`sa-token-demo-sso2-client`为例：

##### 4.1、新建`H5Controller`开放接口
``` java
/**
 * 前后台分离架构下集成SSO所需的代码 
 */
@RestController
public class H5Controller {

	// 当前是否登录 
	@RequestMapping("/isLogin")
	public Object isLogin() {
		return SaResult.data(StpUtil.isLogin());
	}
	
	// 返回SSO认证中心登录地址 
	@RequestMapping("/getSsoAuthUrl")
	public SaResult getSsoAuthUrl(String clientLoginUrl) {
		String serverAuthUrl = SaSsoUtil.buildServerAuthUrl(clientLoginUrl, "");
		return SaResult.data(serverAuthUrl);
	}
	
	// 根据ticket进行登录 
	@RequestMapping("/doLoginByTicket")
	public SaResult doLoginByTicket(String ticket) {
		Object loginId = SaSsoUtil.checkTicket(ticket);
		if(loginId != null) {
			StpUtil.login(loginId);
			return SaResult.data(StpUtil.getTokenValue());
		}
		return SaResult.error("无效ticket：" + ticket); 
	}

	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}
```

##### 4.2、增加跨域过滤器`CorsFilter.java`
源码详见：[CorsFilter.java](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso2-client/src/main/java/com/pj/h5/CorsFilter.java)，
将其复制到项目中即可 

##### 4.3、新建前端项目 
任意文件夹新建前端项目：`sa-token-demo-sso2-client-h5`，在根目录添加测试文件：`index.html`
``` js
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Sa-Token-SSO-Client端-测试页（前后端分离版）</title>
	</head>
	<body>
		<h2>Sa-Token SSO-Client 应用端（前后端分离版）</h2>
		<p>当前是否登录：<b class="is-login"></b></p>
		<p>
			<a href="javascript:location.href='sso-login.html?back=' + encodeURIComponent(location.href);">登录</a>
			<a href="javascript:location.href=baseUrl + '/sso/logout?satoken=' + localStorage.satoken + '&back=' + encodeURIComponent(location.href);">注销</a>
		</p>
		<script src="https://unpkg.zhimg.com/jquery@3.4.1/dist/jquery.min.js"></script>
		<script type="text/javascript">
		
			// 后端接口地址 
			var baseUrl = "http://sa-sso-client1.com:9001";
				
			// 查询当前会话是否登录 
			$.ajax({
				url: baseUrl + '/isLogin',
				type: "post", 
				dataType: 'json',
				headers: {
					"X-Requested-With": "XMLHttpRequest",
					"satoken": localStorage.getItem("satoken")
				},
				success: function(res){
					$('.is-login').html(res.data + '');
				},
				error: function(xhr, type, errorThrown){
					return alert("异常：" + JSON.stringify(xhr));
				}
			});
			
		</script>
	</body>
</html>
```

##### 4.4、添加登录处理文件`sso-login.html`
源码详见：[sso-login.html](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso2-client-h5/sso-login.html)，
将其复制到项目中即可，与`index.html`一样放在根目录下 


##### 4.5、测试 
先启动Server服务端与Client服务端，再随便找个能预览html的工具打开前端项目（比如[HBuilderX](https://www.dcloud.io/hbuilderx.html)），测试流程与一体版一致 


### 五、常见疑问

##### 问：在模式一与模式二中，Client端 必须通过 Alone-Redis 插件来访问Redis吗？

答：不必须，只是推荐，权限缓存与业务缓存分离后会减少SSO-Redis的访问压力，且可以避免多个Client端的缓存读写冲突


##### 问：将旧有系统改造为单点登录时，应该注意哪些？
建议不要把其中一个系统改造为SSO服务端，而是新起一个项目作为Server端，所有旧有项目全部作为Client端与此对接 



