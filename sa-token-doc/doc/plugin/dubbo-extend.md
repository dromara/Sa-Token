# 和 Dubbo 集成 

本插件的作用是让 Sa-Token 和 Dubbo 做一个整合。 

--- 

### 先说说要解决的问题 

在 Dubbo 的整个调用链中，代码被分为 Consumer 端和 Provider 端，为方便理解我们可以称其为 `[调用端]` 和 `[被调用端]`。 

RPC 模式的调用，可以让我们像调用本地方法一样完成服务通信，然而这种便利下却隐藏着两个问题：

- 上下文环境的丢失。
- 上下文参数的丢失。 

这种问题作用在 Sa-Token 框架上就是，在 [ 被调用端 ] 调用 Sa-Token 相关API会抛出异常：**`无效上下文`**。

所以本插件的目的也就是解决上述两个问题：

- 在 [ 被调用端 ] 提供以 Dubbo 为基础的上下文环境 
- 在 RPC 调用时将 Token 传递至 [ 被调用端 ]，同时在调用结束时将 Token 回传至 [ 调用端 ]。


### 引入插件 

在项目已经引入 Dubbo 的基础上，继续添加依赖（Consumer 端和 Provider 端都需要引入）：

``` xml
<!-- Sa-Token 整合 Dubbo -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-context-dubbo</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```


然后我们就可以愉快的做到以下事情：

1. 在 [ 被调用端 ] 安全的调用 Sa-Token 相关 API。
2. 在 [ 调用端 ] 登录的会话，其登录状态可以自动传递到 [ 被调用端 ] 。
3. 在 [ 被调用端 ] 登录的会话，其登录状态也会自动回传到 [ 调用端 ] 。

但是我们仍具有以下限制：

1. [ 调用端 ] 与 [ 被调用端 ] 的 `SaStorage` 数据无法互通。
2. [ 被调用端 ] 执行的 `SaResponse.setHeader()`、`setStatus()` 等代码无效。

应该合理避开以上 API 的使用。


### RPC调用鉴权

在之前的 [Id-Token](/micro/id-token) 章节，我们演示了基于 Feign 的 RPC 调用鉴权，下面我们演示一下在 Dubbo 中如何集成 Id-Token 模块。

其实思路和 Feign 模式一致，在 [ 调用端 ] 追加 Id-Token 参数，在 [ 被调用端 ] 校验这个 Id-Token 参数：

- 校验通过：调用成功。
- 校验不通过：通过失败，抛出异常。

我们有两种方式完成整合。

##### 方式一、使用配置

直接在 `application.yml` 配置即可：

``` yml
sa-token: 
	# 打开 RPC 调用鉴权 
	check-id-token: true
```


##### 方式二、自建 Dubbo 过滤器校验

1、在 [ 调用端 ] 的 `\resources\META-INF\dubbo\` 目录新建 `org.apache.dubbo.rpc.Filter` 文件
``` html
dubboConsumerFilter=com.pj.DubboConsumerFilter
```

新建 `DubboConsumerFilter.java` 过滤器

``` java
package com.pj;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import cn.dev33.satoken.id.SaIdUtil;

/**
 * Sa-Token 整合 Dubbo Consumer端过滤器 
 */
@Activate(group = {CommonConstants.CONSUMER}, order = -10000)
public class DubboConsumerFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		// 追加 Id-Token 参数 
		RpcContext.getContext().setAttachment(SaIdUtil.ID_TOKEN, SaIdUtil.getToken()); 
		
		// 开始调用
		return invoker.invoke(invocation);
	}

}
```


2、在 [ 被调用端 ] 的 `\resources\META-INF\dubbo\` 目录新建 `org.apache.dubbo.rpc.Filter` 文件
``` html
dubboProviderFilter=com.pj.DubboProviderFilter
```

新建 `DubboProviderFilter.java` 过滤器

``` java
package com.pj;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import cn.dev33.satoken.id.SaIdUtil;

/**
 * Sa-Token 整合 Dubbo Provider端过滤器 
 */
@Activate(group = {CommonConstants.PROVIDER}, order = -10000)
public class DubboProviderFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		// 取出 Id-Token 进行校验 
		String idToken = invocation.getAttachment(SaIdUtil.ID_TOKEN);
		SaIdUtil.checkToken(idToken);
		
		// 开始调用
		return invoker.invoke(invocation);
	}

}
```

然后我们就可以进行安全的 RPC 调用了，不带有 Id-Token 参数的调用都会抛出异常，无法调用成功。
