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

这样做有两个好处: 
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

成品样例参考：[码云 StpUserUtil.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/satoken/StpUserUtil.java)

4、接下来就可以像调用`StpUtil.java`一样调用 `StpUserUtil.java`了，这两套账号认证的逻辑是完全隔离的。例如：

``` java
// 凡是在 StpUtil 上有的方法，都可以在 StpUserUtil 上调用 
StpUserUtil.login(10001);    // 在当前会话以10001账号进行登录 
StpUserUtil.checkLogin();    // 校验当前账号是否以 User 身份进行登录 
StpUserUtil.getSession();    // 获取当前 User 账号的 Access-Session 对象 
StpUserUtil.checkPermission('xx');    // 校验当前登录的 user 账号是否具有 xx 权限 
// ...
```


### 5、Kit模式 
如果你觉得 “复制代码” 的方式繁琐不够优雅，这里还有另一种方案：建立一个 `StpKit.java` 门面类，声明所有的 `StpLogic` 引用：
``` java
/**
 * StpLogic 门面类，管理项目中所有的 StpLogic 账号体系
 */
public class StpKit {

    /**
     * 默认原生会话对象
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Admin 会话对象，管理 Admin 表所有账号的登录、权限认证
     */
    public static final StpLogic ADMIN = new StpLogic("admin");

    /**
     * User 会话对象，管理 User 表所有账号的登录、权限认证
     */
    public static final StpLogic USER = new StpLogic("user");

    /**
     * XX 会话对象，（项目中有多少套账号表，就声明几个 StpLogic 会话对象）
     */
    public static final StpLogic XXX = new StpLogic("xx");

}
```

在需要登录、权限认证的地方：
``` java
// 在当前会话进行 Admin 账号登录
StpKit.ADMIN.login(10001);

// 在当前会话进行 User 账号登录
StpKit.USER.login(10001);

// 检测当前会话是否以 Admin 账号登录，并具有 article:add 权限
StpKit.ADMIN.checkPermission("article:add");

// 检测当前会话是否以 User 账号登录，并通过了二级认证
StpKit.USER.checkSafe();

// 获取当前 User 会话的 Session 对象，并进行写值操作 
StpKit.USER.getSession().set("name", "zhang");
```


### 6、在多账户模式下使用注解鉴权
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


### 7、使用注解合并简化代码
交流群里有同学反应，虽然可以根据 `@SaCheckLogin(type = "user")` 指定账号类型，但几十上百个注解都加上这个的话，还是有些繁琐，代码也不够优雅，有么有更简单的解决方案？

我们期待一种`[注解继承/合并]`的能力，即：自定义一个注解，标注上`@SaCheckLogin(type = "user")`，
然后在方法上标注这个自定义注解，效果等同于标注`@SaCheckLogin(type = "user")`。

很遗憾，JDK默认的注解处理器并没有提供这种`[注解继承/合并]`的能力，不过好在我们可以利用 Spring 的注解处理器，达到同样的目的。

1. 重写Sa-Token默认的注解处理器：

``` java
@Configuration
public class SaTokenConfigure {
    @PostConstruct
    public void rewriteSaStrategy() {
    	// 重写Sa-Token的注解处理器，增加注解合并功能 
		SaStrategy.instance.getAnnotation = (element, annotationClass) -> {
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

注：其它注解 `@SaCheckRole("xxx")`、`@SaCheckPermission("xxx")`同理， 完整示例参考 Gitee 代码：
[注解合并](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/satoken/merge_annotation)。

> [!TIP| label:自定义注解方案] 
> 除了注解合并方案，这里还有一份自定义注解方案，参考：[自定义注解](/fun/custom-annotations)



### 8、同端多登陆 
假设我们不仅需要在后台同时集成两套账号，我们还需要在一个客户端同时登陆两套账号（业务场景举例：一个APP中可以同时登陆商家账号和用户账号）。

如果我们不做任何特殊处理的话，在客户端会发生`token覆盖`，新登录的 token 会覆盖掉旧登录的 token 从而导致旧登录失效。

具体表现大致为：在一个浏览器登录商家账号后，再登录用户账号，然后商家账号的登录态就会自动失效。

那么如何解决这个问题？很简单，我们只要更改一下 `StpUserUtil` 的 `TokenName` 即可，参考示例如下：

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

再次调用 `StpUserUtil.login(10001)` 进行登录授权时，token的名称将不再是 `satoken`，而是我们重写后的 `satoken-user`，这样就不会再客户端发生 token 的相互覆盖了。


### 9、不同体系不同 SaTokenConfig 配置
如果自定义的 StpUserUtil 需要使用不同 SaTokenConfig 对象, 也很简单，参考示例如下：

``` java
@Configuration
public class SaTokenConfigure {
	
	@PostConstruct
	public void setSaTokenConfig() {
		// 设定 StpUtil 使用的 SaTokenConfig 配置参数对象
		SaTokenConfig config1 = new SaTokenConfig();
		config1.setTokenName("satoken1");
		config1.setTimeout(1000);
		config1.setTokenStyle("random-64");
		// 更多设置 ... 
		StpUtil.stpLogic.setConfig(config1);

		// 设定 StpUserUtil 使用的 SaTokenConfig 配置参数对象
		SaTokenConfig config2 = new SaTokenConfig();
		config2.setTokenName("satoken2");
		config2.setTimeout(2000);
		config2.setTokenStyle("tik");
		// 更多设置 ... 
		StpUserUtil.stpLogic.setConfig(config2);
	}

}

```


### 10、多账号体系混合鉴权
QQ群中经常有小伙伴提问：在多账号体系下，怎么在 SaInterceptor 拦截器中给一个接口登录鉴权？

其实这个问题，主要是靠你的业务需求来决定，以后台 Admin 账号和前台 User 账号为例：

``` java
// 注册 Sa-Token 拦截器
@Override
public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(new SaInterceptor(handle -> {
		
		// 如果这个接口，要求客户端登录了后台 Admin 账号才能访问：
		SaRouter.match("/art/getInfo").check(r -> StpUtil.checkLogin());

		// 如果这个接口，要求客户端登录了前台 User 账号才能访问：
		SaRouter.match("/art/getInfo").check(r -> StpUserUtil.checkLogin());
		
		// 如果这个接口，要求客户端同时登录 Admin 和 User 账号，才能访问：
		SaRouter.match("/art/getInfo").check(r -> {
			StpUtil.checkLogin();
			StpUserUtil.checkLogin();
		});

		// 如果这个接口，要求客户端登录 Admin 和 User 账号任意一个，就能访问：
		SaRouter.match("/art/getInfo").check(r -> {
			if(StpUtil.isLogin() == false && StpUserUtil.isLogin() == false) {
				throw new SaTokenException("请登录后再访问接口");
			}
		});
		
	})).addPathPatterns("/**");
}
```





---

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/satoken/StpUserUtil.java"
	target="_blank">
	本章代码示例：Sa-Token 多账号体系认证 —— [ StpUserUtil.java ]
</a>