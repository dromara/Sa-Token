# 多账号认证
--- 

### 1、需求场景
有的时候，我们会在一个项目中设计两套账号体系，比如一个电商系统的 `user表` 和 `admin表`，
在这种场景下，如果两套账号我们都使用 `StpUtil` 类的API进行登录鉴权，那么势必会发生逻辑冲突。

在Sa-Token中，这个问题的模型叫做：多账号体系认证。

要解决这个问题，我们必须有一个合理的机制将这两套账号的授权给区分开，让它们互不干扰才行。


### 2、演进思路
假如说我们的 user表 和 admin表 都有一个 id=10001 的账号，它们对应的登录代码：`StpUtil.login(10001)` 是一样的，
那么问题来了：在`StpUtil.getLoginId()`获取到的账号id如何区分它是User用户，还是Admin用户？

你可能会想到为他们加一个固定前缀，比如`StpUtil.login("User_" + 10001)`、`StpUtil.login("Admin_" + 10001)`，这样确实是可以解决问题的，
但是同样的：你需要在`StpUtil.getLoginId()`时再裁剪掉相应的前缀才能获取真正的账号id，这样一增一减就让我们的代码变得无比啰嗦。

那么，有没有从框架层面支持的，更优雅的解决方案呢？


### 3、解决方案

前面几篇介绍的api调用，都是经过 StpUtil 类的各种静态方法进行授权认证，
而如果我们深入它的源码，[点此阅览](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/stp/StpUtil.java) <br/>
就会发现，此类并没有任何代码逻辑，唯一做的事就是对成员变量`stpLogic`的各个API包装一下进行转发。

这样做有两个优点: 
- StpLogic 类的所有函数都可以被重写，按需扩展。
- 在构造方法时随意传入一个不同的 `loginType`，就可以再造一套账号登录体系。


### 4、操作示例

比如说，对于原生`StpUtil`类，我们只做`admin账号`权限认证，而对于`user账号`，我们则：
1. 新建一个新的权限认证类，比如： `StpUserUtil.java`。
2. 将`StpUtil.java`类的全部代码复制粘贴到 `StpUserUtil.java`里。
3. 更改一下其 `LoginType`， 比如：

``` java
public class StpUserUtil {
	
	/**
	 * 账号体系标识 
	 */
	public static final String TYPE = "user";	// 将 LoginType 从`login`改为`user` 

	// 其它代码 ... 

}
```
4. 接下来就可以像调用`StpUtil.java`一样调用 `StpUserUtil.java`了，这两套账号认证的逻辑是完全隔离的。

> 成品样例参考：[码云 StpUserUtil.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-springboot/src/main/java/com/pj/satoken/at/StpUserUtil.java)


### 5、在多账户模式下使用注解鉴权
框架默认的注解鉴权 如`@SaCheckLogin` 只针对原生`StpUtil`进行鉴权。

例如，我们在一个方法上加上`@SaCheckLogin`注解，这个注解只会放行通过`StpUtil.login(id)`进行登录的会话，
而对于通过`StpUserUtil.login(id)`进行登录的会话，则始终不会通过校验。

那么如何告诉`@SaCheckLogin`要鉴别的是哪套账号的登录会话呢？很简单，你只需要指定一下注解的type属性即可：

``` java
// 通过type属性指定此注解校验的是我们自定义的`StpUserUtil`，而不是原生`StpUtil`
@SaCheckLogin(type = StpUserUtil.TYPE)
@RequestMapping("info")
public String info() {
    return "查询用户信息";
}
```

注：`@SaCheckRole("xxx")`、`@SaCheckPermission("xxx")`同理，亦可根据type属性指定其校验的账号体系，此属性默认为`""`，代表使用原生`StpUtil`账号体系。

> 使用注解必须[添加注解拦截器](/use/at-check)

### 6、使用注解合并简化代码
交流群里有同学反应，虽然可以根据 `@SaCheckLogin(type = "user")` 指定账号类型，但几十上百个注解都加上这个的话，还是有些繁琐，代码也不够优雅，有么有更简单的解决方案？

我们期待一种`[注解继承/合并]`的能力，即：自定义一个注解，标注上`@SaCheckLogin(type = "user")`，
然后在方法上标注这个自定义注解，效果等同于标注`@SaCheckLogin(type = "user")`。

很遗憾，JDK默认的注解处理器并没有提供这种`[注解继承/合并]`的能力，不过好在我们可以利用 Spring 的注解处理器，达到同样的目的。

1. 重写Sa-Token默认的注解处理器：

``` java
@Configuration
public class SaTokenConfigure {
    @Autowired
    public void rewriteSaStrategy() {
    	// 重写Sa-Token的注解处理器，增加注解合并功能 
		SaStrategy.me.getAnnotation = (element, annotationClass) -> {
			return AnnotatedElementUtils.getMergedAnnotation(element, annotationClass); 
		};
    }
}
```

2. 自定义一个注解：

``` java
/**
 * 登录认证(User版)：只有登录之后才能进入该方法 
 * <p> 可标注在函数、类上（效果等同于标注在此类的所有方法上） 
 */
@SaCheckLogin(type = "user")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface SaUserCheckLogin {
	
}
```

3. 接下来就可以使用我们的自定义注解了：

``` java
// 使用 @SaUserCheckLogin 的效果等同于使用：@SaCheckLogin(type = "user")
@SaUserCheckLogin
@RequestMapping("info")
public String info() {
    return "查询用户信息";
}
```

注：其它注解 `@SaCheckRole("xxx")`、`@SaCheckPermission("xxx")`同理，
完整示例参考：[码云：自定义注解](https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-springboot/src/main/java/com/pj/satoken/at)。




### 7、同端多登陆 
假设我们不仅需要在后台同时集成两套账号，我们还需要在一个客户端同时登陆两套账号（业务场景举例：一个APP中可以同时登陆商家账号和用户账号）。

如果我们不做任何特殊处理的话，在客户端会发生`token覆盖`，新登录的token会覆盖掉旧登录的token从而导致旧登录失效。

那么如何解决这个问题？<br>
很简单，我们只要更改一下 `StpUserUtil` 的 `TokenName` 即可，参考示例如下：

``` java
public class StpUserUtil {
	
	// 使用匿名子类 重写`stpLogic对象`的一些方法 
	public static StpLogic stpLogic = new StpLogic("user") {
		// 重写 StpLogic 类下的 `splicingKeyTokenName` 函数，返回一个与 `StpUtil` 不同的token名称, 防止冲突 
		@Override
		public String splicingKeyTokenName() {
			return super.splicingKeyTokenName() + "-user";
		}
		// 同理你可以按需重写一些其它方法 ... 
	}; 
	
	// ... 
	
}
```

再次调用 `StpUserUtil.login(10001)` 进行登录授权时，token的名称将不再是 `satoken`，而是我们重写后的 `satoken-user`。


### 8、不同体系不同 SaTokenConfig 配置
如果自定义的 StpUserUtil 需要使用不同 SaTokenConfig 对象, 也很简单，参考示例如下：

``` java
public class StpUserUtil {
	
	// 使用匿名子类 重写`stpLogic对象`的一些方法 
	public static StpLogic stpLogic = new StpLogic("user") {
		
		// 首先自定义一个 Config 对象 
		SaTokenConfig config = new SaTokenConfig()
			.setTokenName("satoken")
			.setTimeout(2592000)
			// ... 其它set
			;
		
		// 然后重写 stpLogic 配置获取方法 
		@Override
		public SaTokenConfig getConfig() {
			return config;
		}
	};
	
	// ... 
	
}
```
