# 全局侦听器

--- 

### 1、工作原理

Sa-Token 提供一种侦听器机制，通过注册侦听器，你可以订阅框架的一些关键性事件，例如：用户登录、退出、被踢下线等。 

事件触发流程大致如下：

![sa-token-listener](https://oss.dev33.cn/sa-token/doc/sa-token-listener.svg  's-w')

框架默认内置了侦听器 `SaTokenListenerForLog` 实现：[代码参考](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/listener/SaTokenListenerForLog.java)
，功能是控制台 log 打印输出，你可以通过配置`sa-token.is-log=true`开启。

要注册自定义的侦听器也非常简单：
1. 新建类实现 `SaTokenListener` 接口。
2. 将实现类注册到 `SaTokenEventCenter` 事件发布中心。


### 2、自定义侦听器实现

##### 2.1、新建实现类：

新建`MySaTokenListener.java`，实现`SaTokenListener`接口，并添加上注解`@Component`，保证此类被`SpringBoot`扫描到：

``` java
/**
 * 自定义侦听器的实现 
 */
@Component
public class MySaTokenListener implements SaTokenListener {

	/** 每次登录时触发 */
	@Override
	public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
		System.out.println("---------- 自定义侦听器实现 doLogin");
	}

	/** 每次注销时触发 */
	@Override
	public void doLogout(String loginType, Object loginId, String tokenValue) {
		System.out.println("---------- 自定义侦听器实现 doLogout");
	}

	/** 每次被踢下线时触发 */
	@Override
	public void doKickout(String loginType, Object loginId, String tokenValue) {
		System.out.println("---------- 自定义侦听器实现 doKickout");
	}

	/** 每次被顶下线时触发 */
	@Override
	public void doReplaced(String loginType, Object loginId, String tokenValue) {
		System.out.println("---------- 自定义侦听器实现 doReplaced");
	}

	/** 每次被封禁时触发 */
	@Override
	public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
		System.out.println("---------- 自定义侦听器实现 doDisable");
	}

	/** 每次被解封时触发 */
	@Override
	public void doUntieDisable(String loginType, Object loginId, String service) {
		System.out.println("---------- 自定义侦听器实现 doUntieDisable");
	}

	/** 每次二级认证时触发 */
	@Override
	public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
		System.out.println("---------- 自定义侦听器实现 doOpenSafe");
	}

	/** 每次退出二级认证时触发 */
	@Override
	public void doCloseSafe(String loginType, String tokenValue, String service) {
		System.out.println("---------- 自定义侦听器实现 doCloseSafe");
	}

	/** 每次创建Session时触发 */
	@Override
	public void doCreateSession(String id) {
		System.out.println("---------- 自定义侦听器实现 doCreateSession");
	}

	/** 每次注销Session时触发 */
	@Override
	public void doLogoutSession(String id) {
		System.out.println("---------- 自定义侦听器实现 doLogoutSession");
	}
	
	/** 每次Token续期时触发 */
    @Override
	public void doRenewTimeout(String tokenValue, Object loginId, long timeout) {
		System.out.println("---------- 自定义侦听器实现 doRenewTimeout");
	}
}
```

##### 2.2、将侦听器注册到事件中心：

以上代码由于添加了 `@Component` 注解，会被 SpringBoot 扫描并自动注册到事件中心，此时我们无需手动注册。

如果我们没有添加 `@Component` 注解或者项目属于非 IOC 自动注入环境，则需要我们手动将这个侦听器注册到事件中心：

``` java
// 将侦听器注册到事件发布中心
SaTokenEventCenter.registerListener(new MySaTokenListener());
```

事件中心的其它一些常用方法：

``` java
// 获取已注册的所有侦听器 
SaTokenEventCenter.getListenerList(); 

// 重置侦听器集合 
SaTokenEventCenter.setListenerList(listenerList); 

// 注册一个侦听器 
SaTokenEventCenter.registerListener(listener); 

// 注册一组侦听器 
SaTokenEventCenter.registerListenerList(listenerList); 

// 移除一个侦听器 
SaTokenEventCenter.removeListener(listener); 

// 移除指定类型的所有侦听器 
SaTokenEventCenter.removeListener(cls); 

// 清空所有已注册的侦听器 
SaTokenEventCenter.clearListener(); 

// 判断是否已经注册了指定侦听器  
SaTokenEventCenter.hasListener(listener); 

// 判断是否已经注册了指定类型的侦听器   
SaTokenEventCenter.hasListener(cls); 
```

##### 2.3、启动测试：
在 `TestController` 中添加登录测试代码：
``` java
// 测试登录接口 
@RequestMapping("login")
public SaResult login() {
	System.out.println("登录前");
	StpUtil.login(10001);		
	System.out.println("登录后");
	return SaResult.ok();
}
```

启动项目，访问登录接口，观察控制台输出：

![sa-token-listener-println](https://oss.dev33.cn/sa-token/doc/sa-token-listener-println.png 's-w-sh')


### 3、其它注意点

##### 3.1、你可以通过继承 `SaTokenListenerForSimple` 快速实现一个侦听器：

``` java
@Component
public class MySaTokenListener extends SaTokenListenerForSimple {
	/*
	 * SaTokenListenerForSimple 对所有事件提供了空实现，通过继承此类，你只需重写一部分方法即可实现一个可用的侦听器。
	 */
	/** 每次登录时触发 */
	@Override
	public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
		System.out.println("---------- 自定义侦听器实现 doLogin");
	}
}
```

##### 3.2、使用匿名内部类的方式注册：
``` java
// 登录时触发 
SaTokenEventCenter.registerListener(new SaTokenListenerForSimple() {
	@Override
	public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
		System.out.println("---------------- doLogin");
	}
});
```

##### 3.3、使用 try-catch 包裹不安全的代码：
如果你认为你的事件处理代码是不安全的（代码可能在运行时抛出异常），则需要使用 `try-catch` 包裹代码，以防因为抛出异常导致 Sa-Token 的整个登录流程被强制中断。

``` java
// 登录时触发 
SaTokenEventCenter.registerListener(new SaTokenListenerForSimple() {
	@Override
	public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
		try {
			// 不安全代码需要写在 try-catch 里 
			// ......  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
});
```

##### 3.4、疑问：一个项目可以注册多个侦听器吗？
可以，多个侦听器间彼此独立，互不影响，按照注册顺序依次接受到事件通知。


---

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/satoken/MySaTokenListener.java"
	target="_blank">
	本章代码示例：Sa-Token 自定义侦听器  —— [ MySaTokenListener.java ]
</a>

