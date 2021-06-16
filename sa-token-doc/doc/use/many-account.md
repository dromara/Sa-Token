# 多账号验证
--- 

### 需求场景
有的时候，我们会在一个项目中设计两套账号体系，比如一个电商系统的 `user表` 和 `admin表`<br>
在这种场景下，如果两套账号我们都使用 `StpUtil` 类的API进行登录鉴权，那么势必会发生逻辑冲突

在sa-token中，这个问题的模型叫做：多账号体系验证 <br>
要解决这个问题，我们必须有一个合理的机制将这两套账号的授权给区分开，让它们互不干扰才行


### 解决方案

以上几篇介绍的api调用，都是经过 `StpUtil` 类的各种静态方法进行授权验证，
而如果我们深入它的源码，[点此阅览](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/stp/StpUtil.java) <br/>
就会发现，此类并没有任何代码逻辑，唯一做的事就是对成员变量`stpLogic`的各个API包装一下进行转发

这样做有两个优点: 
- `StpLogic`类的所有函数都可以被重写，按需扩展
- 在构造方法时随意传入一个不同的 `loginType`，就可以再造一套账号登录体系 


### 操作示例

比如说，对于原生`StpUtil`类，我们只做`admin账号`权限验证，而对于`user账号`，我们则：
1. 新建一个新的权限验证类，比如： `StpUserUtil.java`
2. 将`StpUtil.java`类的全部代码复制粘贴到 `StpUserUtil.java`里
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
4. 接下来就可以像调用`StpUtil.java`一样调用 `StpUserUtil.java`了，这两套账号认证的逻辑是完全隔离的

> 成品样例参考：[码云 StpUserUtil.java](https://gitee.com/click33/sa-plus/blob/master/sp-server/src/main/java/com/pj/current/satoken/StpUserUtil.java)


### 在多账号模式下使用注解鉴权
框架默认的注解鉴权 如`@SaCheckLogin` 只针对原生`StpUtil`进行鉴权 

例如，我们在一个方法上加上`@SaCheckLogin`注解，这个注解只会放行通过`StpUtil.login(id)`进行登录的会话，
而对于通过`StpUserUtil.login(id)`进行登录的都会话，则始终不会通过校验

那么如何告诉`@SaCheckLogin`要鉴别的是哪套账号的登录会话呢？很简单，你只需要指定一下注解的type属性即可：

``` java
// 通过type属性指定此注解校验的是我们自定义的`StpUserUtil`，而不是原生`StpUtil`
@SaCheckLogin(type = StpUserUtil.TYPE)
@RequestMapping("info")
public String info() {
    return "查询用户信息";
}
```

注：`@SaCheckRole("xxx")`、`@SaCheckPermission("xxx")`同理，亦可根据type属性指定其校验的账号体系，此属性默认为`""`，代表使用原生`StpUtil`账号体系





### 进阶
假设我们不仅需要在后台同时集成两套账号，我们还需要在一个客户端同时登陆两套账号（业务场景举例：一个APP中可以同时登陆商家账号和用户账号）

如果我们不做任何特殊处理的话，在客户端会发生`token覆盖`，新登录的token会覆盖掉旧登录的token从而导致旧登录失效

那么如何解决这个问题？<br>
很简单，我们只要更改一下 `StpUserUtil` 的 `TokenName` 即可，参考示例如下：

``` java
// 底层的 StpLogic 对象  
public static StpLogic stpLogic = new StpLogic("user") {
	// 重写 StpLogic 类下的 `splicingKeyTokenName` 函数，返回一个与 `StpUtil` 不同的token名称, 防止冲突 
	@Override
	public String splicingKeyTokenName() {
		return super.splicingKeyTokenName() + "-user";
	}
}; 
```

再次调用 `StpUserUtil.login(10001)` 进行登录授权时，token的名称将不再是 `satoken`，而是我们重写后的 `satoken-user`



> 不同体系账号在登录时设置不同的token有效期等信息, 详见[登录时指定token有效期](/use/remember-me?id=登录时指定token有效期)