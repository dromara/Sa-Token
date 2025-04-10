# 自定义 SaTokenContext 指南 

目前 Sa-Token 仅对 SpringBoot、SpringMVC、WebFlux、Solon 等部分 Web 框架制作了 Starter 集成包，
如果我们使用的 Web 框架不在上述列表之中，则需要自定义 SaTokenContext 相关接口完成整合工作。

我们需要关注的主要就是四个接口：

- SaTokenContext：上下文管理器。
- SaRequest：请求对象，携带着一次请求的所有参数数据。
- SaResponse：响应对象，携带着对客户端一次响应的所有数据。
- SaStorage：请求上下文对象，提供 [一次请求范围内] 的上下文数据读写。

---


### 上下文包装类 

在鉴权中，必不可少的步骤就是从 `HttpServletRequest` 中读取 Token，然而当我们调用 `StpUtil.isLogin()` 获取当前会话是否登录时，
我们并没有传递 `HttpServletRequest` 参数，框架是怎么读取出来 Token 的呢？

以 SpringBoot 项目为例，Sa-Token 框架会自动注册一个全局过滤器，在每次接收到请求时，将 `HttpServletRequest` 对象保存在 `ThreadLocal` 之中。

在后续的方法中，如果你调用了 `StpUtil.isLogin()` 等方法，框架便会从 `ThreadLocal` 中获取 `HttpServletRequest` 对象，从而进一步读取 token 等信息。

让我们来看一下具体的代码细节，全局上下文初始化过滤器：

``` java
/**
 * SaTokenContext 上下文初始化过滤器 (基于 Servlet)
 */
@Order(SaTokenConsts.SA_TOKEN_CONTEXT_FILTER_ORDER)
public class SaTokenContextFilterForServlet implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			SaTokenContextServletUtil.setContext((HttpServletRequest) request, (HttpServletResponse) response);
			chain.doFilter(request, response);
		} finally {
			SaTokenContextServletUtil.clearContext();
		}
	}
}
```

进一步追踪 `SaTokenContextServletUtil.setContext` 方法：

``` java
public static void setContext(HttpServletRequest request, HttpServletResponse response) {
	SaRequest req = new SaRequestForServlet(request);
	SaResponse res = new SaResponseForServlet(response);
	SaStorage stg = new SaStorageForServlet(request);
	SaManager.getSaTokenContext().setContext(req, res, stg);
}
```

此处有一个细节，为什么保存的不是原生 `HttpServletRequest` 与 `HttpServletResponse`，而是 `SaRequest`、`SaResponse`、`SaStorage` 三个包装对象？

因为并不是所有的 web 框架都具有 `HttpServletRequest` 对象，例如在 WebFlux 中，只有 `ServerHttpRequest`，
在一些其它Web框架中，可能连 `Request` 的概念都没有。

Sa-Token 为了一套代码对接所有的 Web 框架，就在原生请求对象的基础上又封装了一层 `SaTokenContext` 相关接口，用于屏蔽掉不同 Web 框架之间的差异，提供统一的调用API：

![sa-token-context](https://oss.dev33.cn/sa-token/doc/plugin/sa-token-context-2.svg)

因此，要对接不同的 Web 框架，就要针对不同的 Web 框架封装不同版本的 `SaRequest`、`SaResponse`、`SaStorage` 包装类对象。

如果你的 Web 框架是基于 Servlet 规范开发的，那么你可以直接引入 `sa-token-servlet`，这个包封装了针对 Servlet 规范的上下文包装类对象：

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


如果你的 web 框架不是基于 Servlet 规范开发的，也问题不大，手动实现一下即可，参考一下 Servlet 包是怎么做的：
[SaRequestForServlet.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-servlet/src/main/java/cn/dev33/satoken/servlet/model/SaRequestForServlet.java)、
[SaResponseForServlet.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-servlet/src/main/java/cn/dev33/satoken/servlet/model/SaResponseForServlet.java)、
[SaStorageForServlet.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-servlet/src/main/java/cn/dev33/satoken/servlet/model/SaStorageForServlet.java)。

封装好包装类对象之后，接下来要做的就是在这个 Web 框架中注册一个全局过滤器，将包装类对象保存到“全局上下文管理器”之中，以备调用：

``` java
SaRequest req = new SaRequestForXxx(request);
SaResponse res = new SaResponseForXxx(response);
SaStorage stg = new SaStorageForXxx(request);
SaManager.getSaTokenContext().setContext(req, res, stg);
```

这样我们即可在具体的 Controller 请求中，成功调用 `StpUtil.isLogin()` 的 API。

总结：整体的步骤并不复杂，就是先定义 `SaRequest`、`SaResponse`、`SaStorage` 的包装类，然后在全局过滤器保存在上下文管理器中。
可以参考具体实现 `sa-token-spring-boot-starter`（SpringBoot2 项目 starter 包）：
[SaTokenContextFilterForServlet.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-starter/sa-token-spring-boot-starter/src/main/java/cn/dev33/satoken/filter/SaTokenContextFilterForServlet.java)



### 旧版本方案
在旧版本中（< v1.42.0）我们推荐的方案是自定义整个 `SaTokenCentext` 接口，目前此方案在新版本已不推荐，此处仅做留档备份：[自定义 SaTokenContext 指南](/fun/sa-token-context--backup.md)

