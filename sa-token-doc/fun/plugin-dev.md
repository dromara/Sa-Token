# Sa-Token 插件开发指南 

--- 

插件，从字面意思理解就是可拔插的组件，作用是在不改变 Sa-Token 现有架构的情况下，替换或扩展一部分底层代码逻辑。
					
为 Sa-Token 开发插件非常简单，以下是几种可用的方法：

- 自定义全局策略。
- 更改全局组件实现。
- 实现自定义SaTokenContext。
- 其它自由扩展。

下面依次介绍这几种方式。

### 1、自定义全局策略

Sa-Token 将框架的一些关键逻辑抽象出一个统一的概念 —— 策略，并统一定义在 `SaStrategy` 中，源码参考：[SaStrategy](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/strategy/SaStrategy.java) 。

SaStrategy 的每一个函数都可以单独重写，以 “自定义Token生成策略” 这一需求为例：

``` java
// 重写 Token 生成策略 
SaStrategy.instance.createToken = (loginId, loginType) -> {
	return SaFoxUtil.getRandomString(60);    // 随机60位长度字符串
};
```

就像变量的重新赋值一样，我们只需重新指定一个新的策略函数，即可自定义 Token 生成的逻辑。


### 2、更改全局组件实现

Sa-Token 大部分全局组件都定义在 SaManager 之上（[SaManager](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/SaManager.java)），
我们只需要更改组件的实现类即可。以 临时令牌认证 模块举例

##### 1、先自定义一个实现类 

``` java
/**
 * 临时认证模块 自定义实现 
 */
public class MySaTemp implements SaTempInterface {

	@Override
	public String createToken(Object value, long timeout) {
		System.out.println("------- 自定义一些逻辑 createToken ");
		return SaTempInterface.super.createToken(value, timeout);
	}
	
	@Override
	public Object parseToken(String token) {
		System.out.println("------- 自定义一些逻辑 parseToken ");
		return SaTempInterface.super.parseToken(token);
	}
	
}
```

##### 2、将自定义实现类绑定在 SaManager 上
``` java
// 注入 
SaManager.setSaTemp(new MySaTemp());
```

以上是手动注入方式，如果你是 Spring 的 IOC 环境，则直接在 MySaTemp 实现类加上 @Component 注解即可。

##### 3、开始测试：

``` java 
// 根据 value 创建一个 token 
String token = SaTempUtil.createToken("10014", 120);
System.out.println("生成的Token为：" + token);

Object value = SaTempUtil.parseToken(token);
System.out.println("将Token解析后的值为：" + value);
```

观察控制台输出，检验自定义实现类是否注入成功：

![dev-plugin-print](https://oss.dev33.cn/sa-token/doc/dev-plugin-print.png)


### 3、实现自定义SaTokenContext
SaTokenContext 是对接不同框架的上下文接口，注入流程和第二步类似，篇幅限制，可参考：[自定义 SaTokenContext 指南](/fun/sa-token-context)


### 4、其它自由扩展
这种方式就无需注入什么全局组件替换内部实现了，你可以在 Sa-Token 的基础之上封装任何代码，进行功能扩展。


### 5、练练手
熟悉了插件开发流程，下面的 [ 待开发插件列表 ] 或许可以给你提供一个练手的方向。

##### SaTokenContext 实现：

| 插件						| 功能						| 状态			|
| :--------					| :--------					| :--------		|
| sa-token-solon-starter	| Sa-Token 与 Solon 的整合	| <font color="green" >已完成</font>		|
| sa-token-jfinal-starter	| Sa-Token 与 JFinal 的整合	| <font color="green" >已完成</font>		|
| sa-token-hasor-starter	| Sa-Token 与 Hasor 的整合	| 待开发			|

##### 标签方言：

| 插件							| 功能							| 状态			|
| :--------						| :--------						| :--------		|
| sa-token-dialect-thymeleaf	| Sa-Token 与 thymeleaf 的整合	| <font color="green" >已完成</font>			|
| sa-token-dialect-freemarker	| Sa-Token 与 freemarker 的整合	| 待开发			|
| sa-token-dialect-jsp			| Sa-Token 与 jsp 的整合			| 待开发			|
| sa-token-dialect-velocity		| Sa-Token 与 velocity 的整合	| 待开发			|
| sa-token-dialect-beetl		| Sa-Token 与 beetl 的整合		| 待开发			|

##### 持久层扩展：

| 插件							| 功能							| 状态			|
| :--------						| :--------						| :--------		|
| sa-token-redis			| Sa-Token 与 Redis 的整合		| <font color="green" >已完成</font>			|
| sa-token-memcached		| Sa-Token 与 memcached 的整合	| 待开发			|

##### 其它：
任何你认为有价值的功能代码，都可以扩展为插件。


### 6、发布代码
插件开发完毕之后，你可以将其pr到官方仓库，或：

上传到 gitee/github 作为独立项目维护，并发布到 Maven 中央仓库，参考这篇：[https://juejin.cn/post/6844904104834105358](https://juejin.cn/post/6844904104834105358)





