# 注解式鉴权
--- 

有同学表示：尽管使用代码鉴权非常方便，但是我仍希望把鉴权逻辑和业务逻辑分离开来，我可以使用注解鉴权吗？当然可以！<br>

注解鉴权 —— 优雅的将鉴权与业务代码分离！

- `@SaCheckLogin`: 标注在方法或类上，当前会话必须处于登录状态才可通过校验
- `@SaCheckRole("admin")`: 标注在方法或类上，当前会话必须具有指定角色标识才能通过校验
- `@SaCheckPermission("user:add")`: 标注在方法或类上，当前会话必须具有指定权限才能通过校验

Sa-Token使用全局拦截器完成注解鉴权功能，为了不为项目带来不必要的性能负担，拦截器默认处于关闭状态<br>
因此，为了使用注解鉴权，你必须手动将sa-token的全局拦截器注册到你项目中

<!-- Sa-Token内置两种模式完成注解鉴权，分别是`拦截器模式`和`AOP模式`, 为了避免不必要的性能浪费，这两种模式默认都处于关闭状态 <br>
因此如若使用注解鉴权，你必须选择其一进行注册 -->


### 1、注册拦截器
以`SpringBoot2.0`为例, 新建配置类`SaTokenConfigure.java` 

``` java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	// 注册sa-token的注解拦截器，打开注解式鉴权功能 
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
                // 注册注解拦截器，并排除不需要注解鉴权的接口地址 (与登录拦截器无关)
		registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");	
	}
}
```
保证此类被`springboot`启动类扫描到即可


### 2、使用注解鉴权
然后我们就可以愉快的使用注解鉴权：

``` java 
// 登录认证：当前会话必须登录才能通过 
@SaCheckLogin						
@RequestMapping("info")
public String info() {
	return "查询用户信息";
}

// 角色认证：当前会话必须具有指定角色标识才能通过 
@SaCheckRole("super-admin")		
@RequestMapping("add")
public String add() {
	return "用户增加";
}

// 权限认证：当前会话必须具有指定权限才能通过 
@SaCheckPermission("user-add")		
@RequestMapping("add")
public String add() {
	return "用户增加";
}
```

注：以上注解都可以加在类上，代表为这个类所有方法进行鉴权


### 3、设定校验模式
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



### 4、在业务逻辑层使用注解鉴权
疑问：我能否将注解写在其它架构层呢，比如业务逻辑层？

使用拦截器模式，只能在`Controller层`进行注解鉴权，如需在任意层级使用注解鉴权，请参考：[AOP注解鉴权](/plugin/aop-at)








