# 注解式鉴权
--- 

- 尽管我们可以方便的一句代码完成权限验证，但是有时候我们仍希望可以将鉴权代码与我们的业务代码分离开来
- 怎么做？
- sa-token内置两个注解，帮助你使用注解完成鉴权操作


## 1、注册拦截器
- 为了不为项目带来不必要的性能负担，`sa-token`默认没有强制为项目注册全局拦截器
- 因此，为了使用注解式鉴权功能，你必须手动将`sa-token`的全局拦截器注册到你项目中
- 以`springboot2.0`为例, 新建配置类`MySaTokenConfig.java` 

``` java
	@Configuration
	public class MySaTokenConfig extends WebMvcConfigurationSupport {
		// 注册sa-token的拦截器，打开注解式鉴权功能 
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new SaCheckInterceptor()).addPathPatterns("/**");
		}
	}
```
- 保证此类被springboot启动类扫描到

## 2、使用注解

#### 登录验证

``` java 
	@SaCheckLogin						// 注解式鉴权：当前会话必须登录才能通过 
	@RequestMapping("info")
	public String info() {
		return "查询用户信息";
	}
```

#### 权限验证

``` java 
	@SaCheckPermission("user-add")		// 注解式鉴权：当前会话必须具有指定权限才能通过 
	@RequestMapping("add")
	public String add() {
		return "用户增加";
	}
```

#### 注意事项
以上两个注解都可以加在类上，代表为这个类所有方法进行鉴权