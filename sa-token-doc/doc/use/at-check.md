# 注解式鉴权
--- 

有同学表示：尽管使用代码鉴权非常方便，但是我仍希望可以把鉴权逻辑和业务逻辑分离开来，我可以使用注解鉴权吗？<br>
当然可以！身为自诩java最强权限认证框架，怎么能少的了注解鉴权这一标配功能呢？

- `@SaCheckLogin`: 标注在方法或类上，当前会话必须处于登录状态才可通过校验
- `@SaCheckRole("admin")`: 标注在方法或类上，当前会话必须具有指定角色标识才能通过校验
- `@SaCheckPermission("user:add")`: 标注在方法或类上，当前会话必须具有指定权限才能通过校验

sa-token内置两种模式完成注解鉴权，分别是`AOP模式`和`拦截器模式`, 为了避免不必要的性能浪费，这两种模式默认都处于关闭状态 <br>
因此如若使用注解鉴权，你必须选择其一进行注册


## 1、使用AOP模式

首先在`pom.xml`里添加依赖：

``` xml 
<!-- sa-token整合SpringAOP实现注解鉴权 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-aop</artifactId>
	<version>1.16.0.RELEASE</version>
</dependency>
```

然后我们就可以愉快的使用注解鉴权：

**登录验证**

``` java 
// 注解式鉴权：当前会话必须登录才能通过 
@SaCheckLogin						
@RequestMapping("info")
public String info() {
	return "查询用户信息";
}
```

**角色验证**

``` java 
// 注解式鉴权：当前会话必须具有指定角色标识才能通过 
@SaCheckRole("super-admin")		
@RequestMapping("add")
public String add() {
	return "用户增加";
}
```

**权限验证**

``` java 
// 注解式鉴权：当前会话必须具有指定权限才能通过 
@SaCheckPermission("user-add")		
@RequestMapping("add")
public String add() {
	return "用户增加";
}
```

注：以上两个注解都可以加在类上，代表为这个类所有方法进行鉴权


#### 设定校验模式
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



## 2、使用拦截器模式
使用AOP方式需要引入新的pom依赖，与此相比，拦截器模式显的更加轻量级  <br>
你只需要将sa-token的注解校验拦截器注册到你的项目中即可打开注解鉴权功能 <br>
以`SpringBoot2.0`为例, 新建配置类`SaTokenConfigure.java` 

``` java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	// 注册sa-token的注解拦截器，打开注解式鉴权功能 
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");	
	}
}
```
保证此类被`springboot`启动类扫描到即可


#### 注意事项：
- 使用AOP模式，可以将注解写在任意层级，使用拦截器模式，只能把注解写在`Controller层`上 <br>
- 拦截器模式和AOP模式不可同时集成，否则会在`Controller层`发生一个注解校验两次的bug









