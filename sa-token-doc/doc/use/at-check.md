# 注解式鉴权
--- 

- 尽管我们可以方便的一句代码完成权限验证，但是有时候我们仍希望可以将鉴权代码与我们的业务代码分离开来
- 怎么做？`sa-token`内置三个注解，帮助你使用注解完成鉴权操作


## 1、注册拦截器
- 为了不为项目带来不必要的性能负担，`sa-token`默认没有强制为项目注册全局拦截器
- 因此，为了使用注解式鉴权功能，你必须手动将`sa-token`的全局拦截器注册到你项目中
- 以`springboot2.0`为例, 新建配置类`MySaTokenConfig.java` 

``` java
	@Configuration
	public class MySaTokenConfig implements WebMvcConfigurer {
		// 注册sa-token的拦截器，打开注解式鉴权功能 
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new SaCheckInterceptor()).addPathPatterns("/**");		// 全局拦截器
		}
	}
```
- 保证此类被`springboot`启动类扫描到

## 2、使用注解

#### 登录验证

``` java 
	// 注解式鉴权：当前会话必须登录才能通过 
	@SaCheckLogin						
	@RequestMapping("info")
	public String info() {
		return "查询用户信息";
	}
```

#### 角色验证

``` java 
	// 注解式鉴权：当前会话必须具有指定角色标识才能通过 
	@SaCheckRole("super-admin")		
	@RequestMapping("add")
	public String add() {
		return "用户增加";
	}
```

#### 权限验证

``` java 
	// 注解式鉴权：当前会话必须具有指定权限才能通过 
	@SaCheckPermission("user-add")		
	@RequestMapping("add")
	public String add() {
		return "用户增加";
	}
```

<br>
以上两个注解都可以加在类上，代表为这个类所有方法进行鉴权


## 3、设定校验模式
`@SaCheckRole`与`@SaCheckPermission`注解可设置校验模式，例如：
``` java
	// 注解式鉴权：只要具有其中一个权限即可通过校验 
	@RequestMapping("atJurOr")
	@SaCheckPermission(value = {"user-add", "user-all", "user-delete"}, mode = SaMode.OR)		
	public AjaxJson atJurOr() {
		return AjaxJson.getSuccessData("用户信息");
	}
```


- mode有两种取值：
	- `SaMode.AND`, 标注一组权限，会话必须全部具有才可通过校验
	- `SaMode.OR`, 标注一组权限，会话只要具有其一即可通过校验



<!-- ## 3、扩展 
- 其实在注册拦截器时，我们也可以根据路由前缀设置不同 `StpLogic`, 从而达到不同模块不同鉴权方式的目的
- 以下为参考示例：
``` java
	@Configuration
	public class MySaTokenConfig implements WebMvcConfigurer {
		// 注册sa-token的拦截器，打开注解式鉴权功能 
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new SaCheckInterceptor(StpUtil.stpLogic)).addPathPatterns("/admin/**");	
			registry.addInterceptor(new SaCheckInterceptor(StpUserUtil.stpLogic)).addPathPatterns("/user/**");	
		}
	}
``` -->



