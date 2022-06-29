# 注解鉴权
--- 

有同学表示：尽管使用代码鉴权非常方便，但是我仍希望把鉴权逻辑和业务逻辑分离开来，我可以使用注解鉴权吗？当然可以！<br>

注解鉴权 —— 优雅的将鉴权与业务代码分离！

- `@SaCheckLogin`: 登录认证 —— 只有登录之后才能进入该方法。 
- `@SaCheckRole("admin")`: 角色认证 —— 必须具有指定角色标识才能进入该方法。 
- `@SaCheckPermission("user:add")`: 权限认证 —— 必须具有指定权限才能进入该方法。 
- `@SaCheckSafe`: 二级认证校验 —— 必须二级认证之后才能进入该方法。 
- `@SaCheckBasic`: HttpBasic认证 —— 只有通过 Basic 认证后才能进入该方法。 

Sa-Token 使用全局拦截器完成注解鉴权功能，为了不为项目带来不必要的性能负担，拦截器默认处于关闭状态<br>
因此，为了使用注解鉴权，**你必须手动将 Sa-Token 的全局拦截器注册到你项目中**

<!-- Sa-Token内置两种模式完成注解鉴权，分别是`拦截器模式`和`AOP模式`, 为了避免不必要的性能浪费，这两种模式默认都处于关闭状态 <br>
因此如若使用注解鉴权，你必须选择其一进行注册 -->


### 1、注册拦截器
以`SpringBoot2.0`为例，新建配置类`SaTokenConfigure.java` 

``` java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	// 注册Sa-Token的注解拦截器，打开注解式鉴权功能 
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册注解拦截器，并排除不需要注解鉴权的接口地址 (与登录拦截器无关)
		registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");	
	}
}
```
保证此类被`springboot`启动类扫描到即可

!> 注意：如果在高版本 `SpringBoot (≥2.6.x)` 下注册拦截器生效，则需要额外添加 `@EnableWebMvc` 注解才可以使用。


### 2、使用注解鉴权
然后我们就可以愉快的使用注解鉴权了：

``` java 
// 登录认证：只有登录之后才能进入该方法 
@SaCheckLogin						
@RequestMapping("info")
public String info() {
	return "查询用户信息";
}

// 角色认证：必须具有指定角色才能进入该方法 
@SaCheckRole("super-admin")		
@RequestMapping("add")
public String add() {
	return "用户增加";
}

// 权限认证：必须具有指定权限才能进入该方法 
@SaCheckPermission("user-add")		
@RequestMapping("add")
public String add() {
	return "用户增加";
}

// 二级认证：必须二级认证之后才能进入该方法 
@SaCheckSafe()		
@RequestMapping("add")
public String add() {
	return "用户增加";
}

// Http Basic 认证：只有通过 Basic 认证后才能进入该方法 
@SaCheckBasic(account = "sa:123456")
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
public SaResult atJurOr() {
	return SaResult.data("用户信息");
}
```

mode有两种取值：
- `SaMode.AND`, 标注一组权限，会话必须全部具有才可通过校验。
- `SaMode.OR`, 标注一组权限，会话只要具有其一即可通过校验。


### 4、角色权限双重 “or校验” 
假设有以下业务场景：一个接口在具有权限 `user-add` 或角色 `admin` 时可以调通。怎么写？

``` java
// 注解式鉴权：只要具有其中一个权限即可通过校验 
@RequestMapping("userAdd")
@SaCheckPermission(value = "user-add", orRole = "admin")		
public SaResult userAdd() {
	return SaResult.data("用户信息");
}
```

orRole 字段代表权限认证未通过时的次要选择，两者只要其一认证成功即可通过校验，其有三种写法：
- 写法一：`orRole = "admin"`，代表需要拥有角色 admin 。
- 写法二：`orRole = {"admin", "manager", "staff"}`，代表具有三个角色其一即可。
- 写法三：`orRole = {"admin, manager, staff"}`，代表必须同时具有三个角色。


### 5、在业务逻辑层使用注解鉴权
疑问：我能否将注解写在其它架构层呢，比如业务逻辑层？

使用拦截器模式，只能在`Controller层`进行注解鉴权，如需在任意层级使用注解鉴权，请参考：[AOP注解鉴权](/plugin/aop-at)








