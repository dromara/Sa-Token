# 多账号验证
--- 

### 需求场景
有的时候，我们会在一个项目中设计两套账号体系，比如一个电商系统的 `user表` 和 `admin表`<br>
在这种场景下，如果两套账号我们都使用 `StpUtil` 类的API进行登录鉴权，那么势必会发生逻辑冲突

在sa-token中，这个问题的模型叫做：多账号体系验证 <br>
要解决这个问题，我们必须有一个合理的机制将这两套账号的授权给区分开，让它们互不干扰才行


### 解决方案

以上几篇介绍的api调用，都是经过 `StpUtil` 类的各种静态方法进行授权验证，
而如果我们深入它的源码，[点此阅览](https://gitee.com/sz6/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/stp/StpUtil.java) <br/>
就会发现，此类并没有任何代码逻辑，唯一做的事就是对成员变量`stpLogic`的各个API包装一下进行转发

这样做有两个优点: 
- `StpLogic`类的所有函数都可以被重写，按需扩展
- 在构造方法时随意传入一个不同的 `loginKey`，就可以再造一套账号登录体系 


### 操作示例

比如说，对于原生`StpUtil`类，我们只做`admin账号`权限验证，而对于`user账号`，我们则：
1. 新建一个新的权限验证类，比如： `StpUserUtil.java`
2. 将`StpUtil.java`类的全部代码复制粘贴到 `StpUserUtil.java`里
3. 更改一下其 `LoginKey`， 比如：
``` java
	// 底层的 StpLogic 对象  
	public static StpLogic stpLogic = new StpLogic("user");	// 将 LoginKey 改为 user 
```
4. 接下来就可以像调用`StpUtil.java`一样调用 `StpUserUtil.java`了，这两套账号认证的逻辑是完全隔离的


### 进阶
假设我们不仅需要在后台同时集成两套账号，我们还需要在一个客户端同时登陆两套账号（业务场景举例：一个APP中可以同时登陆商家账号和用户账号）

如果我们不做任何特殊处理的话，在客户端会发生`token覆盖`，新登录的token会覆盖掉旧登录的token从而导致旧登录失效

那么如何解决这个问题？<br>
很简单，我们只要更改一下 `StpUserUtil` 的 `TokenName` 即可，参考示例如下：

``` java
// 底层的 StpLogic 对象  
public static StpLogic stpLogic = new StpLogic("login") {
	// 重写 `getTokenName` 函数，返回一个与 `StpUtil` 不同的token名称, 防止冲突 
	@Override
	public String getTokenName() {
		return super.getKeyTokenName() + "-user";
	}
}; 
```

再次调用 `StpUserUtil.setLoginId(10001)` 进行登录授权时，token的名称将不再是 `satoken`，而是我们重写后的 `satoken-user`


