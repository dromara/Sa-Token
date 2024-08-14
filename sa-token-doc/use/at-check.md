# 注解鉴权


### 注解鉴权

有同学表示：尽管使用代码鉴权非常方便，但是我仍希望把鉴权逻辑和业务逻辑分离开来，我可以使用注解鉴权吗？当然可以！<br>

注解鉴权 —— 优雅的将鉴权与业务代码分离！

- `@SaCheckLogin`: 登录校验 —— 只有登录之后才能进入该方法。
- `@SaCheckRole("admin")`: 角色校验 —— 必须具有指定角色标识才能进入该方法。
- `@SaCheckPermission("user:add")`: 权限校验 —— 必须具有指定权限才能进入该方法。
- `@SaCheckSafe`: 二级认证校验 —— 必须二级认证之后才能进入该方法。
- `@SaCheckHttpBasic`: HttpBasic校验 —— 只有通过 HttpBasic 认证后才能进入该方法。
- `@SaCheckHttpDigest`: HttpDigest校验 —— 只有通过 HttpDigest 认证后才能进入该方法。
- `@SaIgnore`：忽略校验 —— 表示被修饰的方法或类无需进行注解鉴权和路由拦截器鉴权。
- `@SaCheckDisable("comment")`：账号服务封禁校验 —— 校验当前账号指定服务是否被封禁。

Sa-Token 使用全局拦截器完成注解鉴权功能，为了不为项目带来不必要的性能负担，拦截器默认处于关闭状态<br>
因此，为了使用注解鉴权，**你必须手动将 Sa-Token 的全局拦截器注册到你项目中**


### 1、注册拦截器
以`SpringBoot2.0`为例，新建配置类`SaTokenConfigure.java`

``` java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	// 注册 Sa-Token 拦截器，打开注解式鉴权功能 
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册 Sa-Token 拦截器，打开注解式鉴权功能 
		registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");	
	}
}
```
保证此类被`springboot`启动类扫描到即可

<!-- !> 注意：如果在高版本 `SpringBoot (≥2.6.x)` 下注册拦截器失效，则需要额外添加 `@EnableWebMvc` 注解才可以使用。 -->


### 2、使用注解鉴权
然后我们就可以愉快的使用注解鉴权了：

``` java 
// 登录校验：只有登录之后才能进入该方法 
@SaCheckLogin						
@RequestMapping("info")
public String info() {
	return "查询用户信息";
}

// 角色校验：必须具有指定角色才能进入该方法 
@SaCheckRole("super-admin")		
@RequestMapping("add")
public String add() {
	return "用户增加";
}

// 权限校验：必须具有指定权限才能进入该方法 
@SaCheckPermission("user-add")		
@RequestMapping("add")
public String add() {
	return "用户增加";
}

// 二级认证校验：必须二级认证之后才能进入该方法 
@SaCheckSafe()		
@RequestMapping("add")
public String add() {
	return "用户增加";
}

// Http Basic 校验：只有通过 Http Basic 认证后才能进入该方法 
@SaCheckHttpBasic(account = "sa:123456")
@RequestMapping("add")
public String add() {
	return "用户增加";
}

// Http Digest 校验：只有通过 Http Digest 认证后才能进入该方法 
@SaCheckHttpDigest(value = "sa:123456")
@RequestMapping("add")
public String add() {
	return "用户增加";
}

// 校验当前账号是否被封禁 comment 服务，如果已被封禁会抛出异常，无法进入方法 
@SaCheckDisable("comment")				
@RequestMapping("send")
public String send() {
	return "查询用户信息";
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
- `SaMode.AND`，标注一组权限，会话必须全部具有才可通过校验。
- `SaMode.OR`，标注一组权限，会话只要具有其一即可通过校验。


### 4、角色权限双重 “or校验”
假设有以下业务场景：一个接口在具有权限 `user.add` 或角色 `admin` 时可以调通。怎么写？

``` java
// 角色权限双重 “or校验”：具备指定权限或者指定角色即可通过校验
@RequestMapping("userAdd")
@SaCheckPermission(value = "user.add", orRole = "admin")		
public SaResult userAdd() {
	return SaResult.data("用户信息");
}
```

orRole 字段代表权限校验未通过时的次要选择，两者只要其一校验成功即可进入请求方法，其有三种写法：
- 写法一：`orRole = "admin"`，代表需要拥有角色 admin 。
- 写法二：`orRole = {"admin", "manager", "staff"}`，代表具有三个角色其一即可。
- 写法三：`orRole = {"admin, manager, staff"}`，代表必须同时具有三个角色。


### 5、忽略认证

使用 `@SaIgnore` 可表示一个接口忽略认证：

``` java
@SaCheckLogin
@RestController
public class TestController {
	
	// ... 其它方法 
	
	// 此接口加上了 @SaIgnore 可以游客访问 
	@SaIgnore
	@RequestMapping("getList")
	public SaResult getList() {
		// ... 
		return SaResult.ok(); 
	}
}
```

如上代码表示：`TestController` 中的所有方法都需要登录后才可以访问，但是 `getList` 接口可以匿名游客访问。

- @SaIgnore 修饰方法时代表这个方法可以被游客访问，修饰类时代表这个类中的所有接口都可以游客访问。
- @SaIgnore 具有最高优先级，当 @SaIgnore 和其它鉴权注解一起出现时，其它鉴权注解都将被忽略。
- @SaIgnore 同样可以忽略掉 Sa-Token 拦截器中的路由鉴权，在下面的 [路由拦截鉴权] 章节中我们会讲到。



### 6、批量注解鉴权

使用 `@SaCheckOr` 表示批量注解鉴权：

``` java
// 在 `@SaCheckOr` 中可以指定多个注解，只要当前会话满足其中一个注解即可通过验证，进入方法。
@SaCheckOr(
		login = @SaCheckLogin,
		role = @SaCheckRole("admin"),
		permission = @SaCheckPermission("user.add"),
		safe = @SaCheckSafe("update-password"),
		httpBasic = @SaCheckHttpBasic(account = "sa:123456"),
		disable = @SaCheckDisable("submit-orders")
)
@RequestMapping("test")
public SaResult test() {
	// ... 
	return SaResult.ok(); 
}
```

每一项属性都可以写成数组形式，例如：

``` java
// 当前客户端只要有 [ login 账号登录] 或者 [user 账号登录] 其一，就可以通过验证进入方法。
// 		注意：`type = "login"` 和 `type = "user"` 是多账号模式章节的扩展属性，此处你可以先略过这个知识点。
@SaCheckOr(
	login = { @SaCheckLogin(type = "login"), @SaCheckLogin(type = "user") }
)
@RequestMapping("test")
public SaResult test() {
	// ... 
	return SaResult.ok(); 
}
```

疑问：既然有了 `@SaCheckOr`，为什么没有与之对应的 `@SaCheckAnd` 呢？

因为当你写多个注解时，其天然就是 `and` 校验关系，例如：
 
``` java
// 当你在一个方法上写多个注解鉴权时，其默认就是要满足所有注解规则后，才可以进入方法，只要有一个不满足，就会抛出异常
@SaCheckLogin
@SaCheckRole("admin")
@SaCheckPermission("user.add")
@RequestMapping("test")
public SaResult test() {
	// ... 
	return SaResult.ok(); 
}
```



### 7、扩展阅读

- 在业务逻辑层使用鉴权注解：[AOP注解鉴权](/plugin/aop-at)

- 制作自定义鉴权注解注入到框架：[自定义注解](/fun/custom-annotations)


<!-- 疑问：我能否将注解写在其它架构层呢，比如业务逻辑层？

使用拦截器模式，只能在`Controller层`进行注解鉴权，如需在任意层级使用注解鉴权，请参考：[AOP注解鉴权](/plugin/aop-at) -->


---

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/use/AtCheckController.java"
	target="_blank">
	本章代码示例：Sa-Token 注解鉴权 —— [ AtCheckController.java ]
</a>
<a class="dt-btn" href="https://www.wenjuan.ltd/s/ARJvIbA/" target="_blank">本章小练习：Sa-Token 基础 - 注解鉴权，章节测试</a>







