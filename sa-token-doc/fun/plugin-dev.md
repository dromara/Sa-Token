# Sa-Token 插件开发指南 

<!-- > 注：为 Sa-Token 提交插件请在 sa-token-three-plugin 仓库进行：[点击跳转](https://gitee.com/sa-tokens/sa-token-three-plugin) -->

插件，从字面意思理解就是可拔插的组件，作用是在不改变 Sa-Token 现有架构的情况下，替换或扩展一部分底层代码逻辑。

--- 


## 1、插件开发
			
为 Sa-Token 开发插件非常简单，以下是几种可行的方式：

- 1、自定义全局策略。
- 2、更改全局组件实现。
- 3、实现自定义SaTokenContext。
- 4、其它自由扩展。

下面依次介绍这几种方式。

### 方式1：自定义全局策略

Sa-Token 将框架的一些关键逻辑抽象出一个统一的概念 —— 策略，并统一定义在 `SaStrategy` 中，源码参考：[SaStrategy](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/strategy/SaStrategy.java) 。

SaStrategy 的每一个函数都可以单独重写，以 “自定义Token生成策略” 这一需求为例：

``` java
// 重写 Token 生成策略 
SaStrategy.instance.createToken = (loginId, loginType) -> {
	return SaFoxUtil.getRandomString(60);    // 随机60位长度字符串
};
```

就像变量的重新赋值一样，你只需重新指定一个新的策略函数，即可自定义 Token 生成的逻辑。


### 方式2：更改全局组件实现

你可以找到不符合你需求的组件，重新定义一个实现类，以 临时令牌认证 模块为例，你需要自定义 `SaTempInterface` 的实现类：

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

### 方式3：实现自定义SaTokenContext
SaTokenContext 是对接不同框架的上下文接口，篇幅限制，可参考：[自定义 SaTokenContext 指南](/fun/sa-token-context)


### 方式4：其它自由扩展
这种方式就无需注入什么全局组件替换内部实现了，你可以在 Sa-Token 的基础之上封装任何代码，进行功能扩展。



## 2、插件注册

在你完成插件开发之后，你还需要考虑一个问题，如何让插件代码注入到项目中。

首先这里需要分两种情况：
- 情况1：只打算自己的项目使用这个插件。
- 情况2：准备提交 pr 到 Sa-Token 仓库，让更多人使用。


### 情况1：只打算自己的项目使用这个插件

这种情况比较简单，如果是 SpringBoot 项目，你可以在自定义插件类上添加注解 `@Component`：

``` java
@Component
public class MySaTemp implements SaTempInterface {
	// ... 
}
```

这样在项目启动时， sa-token-spring-boot-starter 集成包将会扫描到这个自定义组件，注入到框架中。

如果是重写全局策略的代码，也可以通过 `@PostConstruct` 注解做到项目启动时自动执行：

``` java
@PostConstruct
public void rewriteSaStrategy() {
	// 重写 token 生成策略
	SaStrategy.instance.createToken = (loginId, loginType) -> {
		return SaFoxUtil.getRandomString(60);
	};
}
```

如果是非 SpringBoot 项目，项目环境无法做到自动注入，保底的方案是在 main 方法中，手动注册组件：

``` java
public static void main(String[] args) {
	// 示例：手动替换 Sa-Token 内部组件
	// Sa-Token 大部分全局组件都定义在 SaManager 之上，参考：https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/SaManager.java 
	SaManager.setSaTemp(new MySaTemp());
	
	// 示例：手动重写 Sa-Token 全局策略
	SaStrategy.instance.createToken = (loginId, loginType) -> {
		return SaFoxUtil.getRandomString(60);
	};
}
```


### 情况2：准备提交 pr 到 Sa-Token 仓库，让更多人使用。

这种情况稍微复杂一些，因为你基本上很难：通过在插件内部写一些代码，帮助“插件使用者”注册插件到项目中。

一种解决方案是：难办，那就别办了。

对，就是你只负责开发相对应的自定义组件，而将自定义组件的注册过程完全交给使用者，这并不是妥协的选择，反而会给插件使用者更大的自由度，
sa-token-jwt、sa-token-thymeleaf 等官方插件都是这样做的。

如果你觉得还是完成插件的自动注入比较好，也是有办法的，那就是利用 SPI 机制来注册组件。

（关于 java SPI 机制，网上教程众多，此处暂不详细介绍，不熟悉的同学可以直接向 deepseek 等 AI 工具提问，给你讲的明明白白的）

你需要考虑一点：这个插件是专门给 SpringBoot 项目使用的，还是面向 Solon、JFinal 等任意项目使用：

#### 如果是：SpringBoot 专用插件

如果这个插件只打算给 SpringBoot 项目使用，可以利用 SpringBoot 的 SPI 机制注册插件

SpringBoot2 格式：创建 `resources\META-INF\spring.factories` 文件：

``` txt
org.springframework.boot.autoconfigure.EnableAutoConfiguration=插件完全限定名
```

SpringBoot3 格式：创建 `resources\META-INF\spring\org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件：

``` txt
插件完全限定名
```

这样在别人引入此插件时，便会根据 SPI 文件指定的地址去加载插件类，做到插件引入即注册的效果。


#### 如果是：通用型插件

通用型插件则不能使用 SpringBoot 的 SPI 机制去注册组件，因为其它项目是无法识别 SpringBoot SPI 文件的，
好在 Sa-Token 提供了自己的 SPI 机制，所有环境均可使用：

1、新建 `SaTokenPluginForXxx` 类，此类需要 `implements SaTokenPlugin` 接口，并且推荐定义在 `cn.dev33.satoken.plugin` 下：

``` java
/**
 * SaToken 插件安装：插件作用描述
 */
public class SaTokenPluginForXxx implements SaTokenPlugin {
    @Override
    public void install() {
        // 书写需要在项目启动时执行的代码，例如：
        // SaManager.setXxx(new SaXxxForXxx());
    }
}
```

2、新建 `resources\META-INF\satoken\cn.dev33.satoken.plugin.SaTokenPlugin` 文件，填写上插件类的完全限定名地址
``` txt
cn.dev33.satoken.plugin.SaTokenPluginForXxx
```

这样便可以在项目启动时，被 Sa-Token 插件管理器加载到此插件，执行自定义 `SaTokenPluginForXxx` 实现类的 `install` 方法，完成插件安装。


## 3、练练手

学废了吗？给你出个题练练手：

开发一个 `sa-token-hutool-json` 插件，要求引入该插件后，自动替换掉 Sa-Token 的 json 序列化方案为 hutool-json 模块。

如果没有思路，可以参考一下 `sa-token-fastjson` 的插件源码实现哦。


<!-- ##### 3、开始测试：

``` java 
// 根据 value 创建一个 token 
String token = SaTempUtil.createToken("10014", 120);
System.out.println("生成的Token为：" + token);

Object value = SaTempUtil.parseToken(token);
System.out.println("将Token解析后的值为：" + value);
```

观察控制台输出，检验自定义实现类是否注入成功：

![dev-plugin-print](https://oss.dev33.cn/sa-token/doc/dev-plugin-print.png) -->


<!-- ### 5、练练手
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
| sa-token-thymeleaf	| Sa-Token 与 thymeleaf 的整合	| <font color="green" >已完成</font>			|
| sa-token-freemarker	| Sa-Token 与 freemarker 的整合	| 待开发			|
| sa-token-jsp			| Sa-Token 与 jsp 的整合			| 待开发			|
| sa-token-velocity		| Sa-Token 与 velocity 的整合	| 待开发			|
| sa-token-beetl		| Sa-Token 与 beetl 的整合		| 待开发			|

##### 持久层扩展：

| 插件							| 功能							| 状态			|
| :--------						| :--------						| :--------		|
| sa-token-redis			| Sa-Token 与 Redis 的整合		| <font color="green" >已完成</font>			|
| sa-token-memcached		| Sa-Token 与 memcached 的整合	| 待开发			|

##### 其它：
任何你认为有价值的功能代码，都可以扩展为插件。 -->


<!-- ### 6、发布代码
插件开发完毕之后，你可以将其 pr 到 [sa-token-three-plugin](https://gitee.com/sa-tokens/sa-token-three-plugin)，或：

上传到 gitee/github 作为独立项目维护，并发布到 Maven 中央仓库，参考这篇：[https://juejin.cn/post/6844904104834105358](https://juejin.cn/post/6844904104834105358) -->





