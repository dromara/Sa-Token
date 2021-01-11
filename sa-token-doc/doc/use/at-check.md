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
		// 注册sa-token的注解拦截器，打开注解式鉴权功能 
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");	
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


mode有两种取值：
- `SaMode.AND`, 标注一组权限，会话必须全部具有才可通过校验
- `SaMode.OR`, 标注一组权限，会话只要具有其一即可通过校验




## 4、AOP模式使用注解

使用拦截器方式，只能把注解加到`Controller层`上，那么如果我想把注解写到项目的任意位置，比如`Service层`，应该怎么办？ <br>
很简单，你只需要将拦截器模式更换为`SpringAOP模式`即可, 在`pom.xml`里添加

``` xml 
<!-- sa-token整合SpringAOP实现注解鉴权 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-aop</artifactId>
	<version>1.12.0</version>
</dependency>
```

然后你就可以在任意地方使用注解鉴权，例如:
``` java
@Service
public class UserService {
	@SaCheckLogin
	public List<String> getList() {
		System.out.println("getList");
		return new ArrayList<String>();
	}
}
```


**注意：拦截器模式和AOP模式不可同时集成，否则会在Controller层发生一个注解校验两次的bug**











