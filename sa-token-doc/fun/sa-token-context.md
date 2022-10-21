# 自定义 SaTokenContext 指南 

目前 Sa-Token 仅对 SpringBoot、SpringMVC、WebFlux、Solon 等部分 Web 框架制作了 Starter 集成包，
如果我们使用的 Web 框架不在上述列表之中，则需要自定义 SaTokenContext 接口的实现完成整合工作。

---

### 1、SaTokenContext是什么，为什么要实现 SaTokenContext 接口？

在鉴权中，必不可少的步骤就是从 `HttpServletRequest` 中读取 Token，然而并不是所有框架都具有 HttpServletRequest 对象，例如在 WebFlux 中，只有 `ServerHttpRequest`，
在一些其它Web框架中，可能连 `Request` 的概念都没有。

那么，Sa-Token 如何只用一套代码就对接到所有 Web 框架呢？

解决这个问题的关键就在于 `SaTokenContext` 接口，此接口的作用是屏蔽掉不同 Web 框架之间的差异，提供统一的调用API：

![sa-token-context](https://oss.dev33.cn/sa-token/doc/sa-token-context.svg 's-w')


SaTokenContext只是一个接口，没有工作能力，这也就意味着 SaTokenContext 接口的实现是必须的。
那么疑问来了，我们之前在 SpringBoot 中引用 Sa-Token 时为什么可以直接使用呢？

其实原理很简单，`sa-token-spring-boot-starter`集成包中已经内置了`SaTokenContext`的实现：[SaTokenContextForSpring](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-spring-boot-starter/src/main/java/cn/dev33/satoken/spring/SaTokenContextForSpring.java)，
并且根据 Spring 的自动注入特性，在项目启动时注入到 Sa-Token 中，做到“开箱即用”。

那么如果我们使用不是 Spring 框架，是不是就必须得手动实现 `SaTokenContext` 接口？答案是肯定的，脱离Spring 环境后，我们就不能再使用`sa-token-spring-boot-starter`集成包了，
此时我们只能引入 `sa-token-core` 核心包，然后手动实现 `SaTokenContext` 接口。

不过不用怕，这个工作很简单，只要跟着下面的文档一步步来，你就可以将 Sa-Token 对接到任意Web框架中。


### 2、实现 Model 接口
我们先来观察一下 `SaTokenContext` 接口的签名：
``` java
/**
 * Sa-Token 上下文处理器
 */
public interface SaTokenContext {

	/**
	 * 获取当前请求的 [Request] 对象
	 */
	public SaRequest getRequest();

	/**
	 * 获取当前请求的 [Response] 对象
	 */
	public SaResponse getResponse();

	/**
	 * 获取当前请求的 [存储器] 对象 
	 */
	public SaStorage getStorage();

	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径 
	 */
	public boolean matchPath(String pattern, String path);

}
```

你可能对 `SaRequest` 比较疑惑，这个对象是干什么用的？正如每个 Web 框架都有 Request 概念的抽象，Sa-Token 也封装了 `Request`、`Response`、`Storage`三者的抽象：

- `Request`：请求对象，携带着一次请求的所有参数数据。参考：[SaRequest.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/context/model/SaRequest.java)。
- `Response`：响应对象，携带着对客户端一次响应的所有数据。参考：[SaResponse.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/context/model/SaResponse.java)。
- `Storage`：请求上下文对象，提供 [一次请求范围内] 的上下文数据读写。参考：[SaStorage.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/context/model/SaStorage.java)。


因此，在实现 `SaTokenContext` 之前，你必须先实现这三个 Model 接口。

先别着急动手，如果你的 Web 框架是基于 Servlet 规范开发的，那么 Sa-Token 已经为你封装好了三个 Model 接口的实现，你要做的就是引入 `sa-token-servlet`包即可：

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 权限认证（ServletAPI 集成包） -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-servlet</artifactId>
    <version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 权限认证（ServletAPI 集成包）
implementation 'cn.dev33:sa-token-servlet:${sa.top.version}'
```
<!---------------------------- tabs:end ------------------------------>


如果你的 Web 框架不是基于 Servlet 规范，那么你就需要手动实现这三个 Model 接口，我们可以参考 `sa-token-servlet` 是怎样实现的：
[SaRequestForServlet.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-servlet/src/main/java/cn/dev33/satoken/servlet/model/SaRequestForServlet.java)、
[SaResponseForServlet.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-servlet/src/main/java/cn/dev33/satoken/servlet/model/SaResponseForServlet.java)、
[SaStorageForServlet.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-servlet/src/main/java/cn/dev33/satoken/servlet/model/SaStorageForServlet.java)。


### 3、实现 SaTokenContext 接口

接下来我们奔入主题，提供 `SaTokenContext` 接口的实现，同样我们可以参考 Spring 集成包是怎样实现的：

``` java
/**
 * Sa-Token 上下文处理器 [ SpringMVC版本实现 ] 
 */
public class SaTokenContextForSpring implements SaTokenContext {

	/**
	 * 获取当前请求的Request对象
	 */
	@Override
	public SaRequest getRequest() {
		return new SaRequestForServlet(SpringMVCUtil.getRequest());
	}

	/**
	 * 获取当前请求的Response对象
	 */
	@Override
	public SaResponse getResponse() {
		return new SaResponseForServlet(SpringMVCUtil.getResponse());
	}

	/**
	 * 获取当前请求的 [存储器] 对象 
	 */
	@Override
	public SaStorage getStorage() {
		return new SaStorageForServlet(SpringMVCUtil.getRequest());
	}
	
	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径 
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		return SaPathMatcherHolder.getPathMatcher().match(pattern, path);
	}

}
```

详细参考：
[SaTokenContextForSpring.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-spring-boot-starter/src/main/java/cn/dev33/satoken/spring/SaTokenContextForSpring.java)


### 4、将自定义实现注入到 Sa-Token 框架中

有了 `SaTokenContext` 接口的实现，我们还需要将这个实现类注入到 Sa-Token 之中，伪代码参考如下：
``` java
/**
 * 程序启动类
 */
public class Application {

	public static void main(String[] args) {
		// 框架启动
		XxxApplication.run(xxx);
		
		// 将自定义的 SaTokenContext 实现类注入到框架中 
		SaTokenContext saTokenContext = new SaTokenContextForXxx();
		SaManager.setSaTokenContext(saTokenContext);
	}
	
}
```

如果你使用的框架带有自动注入特性，那就更简单了，参考 Spring 集成包的 Bean 注入流程：
[注册Bean](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-spring-boot-starter/src/main/java/cn/dev33/satoken/spring/SaBeanRegister.java)、
[注入Bean](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-spring-boot-starter/src/main/java/cn/dev33/satoken/spring/SaBeanInject.java)


### 5、启动项目

启动项目，尝试打印一下 `SaManager.getSaTokenContext()` 对象，如果输出的是你的自定义实现类，那就证明你已经自定义 `SaTokenContext` 成功了，
快来体验一下 Sa-Token 的各种功能吧。




