# 账号封禁

之前的章节中，我们学习了 踢人下线 和 强制注销 功能，用于清退违规账号。

在部分场景下，我们还需要将其 **账号封禁**，以防止其再次登录。

--- 

### 1、账号封禁

对指定账号进行封禁：

``` java
// 封禁指定账号 
StpUtil.disable(10001, 86400); 
```

参数含义：
- 参数1：要封禁的账号id。
- 参数2：封禁时间，单位：秒，此为 86400秒 = 1天（此值为 -1 时，代表永久封禁）。


注意点：对于正在登录的账号，将其封禁并不会使它立即掉线，如果我们需要它即刻下线，可采用先踢再封禁的策略，例如：<br>
``` java
// 先踢下线
StpUtil.kickout(10001); 
// 再封禁账号
StpUtil.disable(10001, 86400); 
```

待到下次登录时，我们先校验一下这个账号是否已被封禁：
``` java
// 校验指定账号是否已被封禁，如果被封禁则抛出异常 `DisableServiceException`
StpUtil.checkDisable(10001); 

// 通过校验后，再进行登录：
StpUtil.login(10001); 
```

!> 旧版本在 `StpUtil.login()` 时会自动校验账号是否被封禁，新版本将 校验封禁 和 登录 两个动作分离成两个方法，不再自动校验，请注意其中的逻辑更改。

此模块所有方法：
``` java
// 封禁指定账号 
StpUtil.disable(10001, 86400); 

// 获取指定账号是否已被封禁 (true=已被封禁, false=未被封禁) 
StpUtil.isDisable(10001); 

// 校验指定账号是否已被封禁，如果被封禁则抛出异常 `DisableServiceException`
StpUtil.checkDisable(10001); 

// 获取指定账号剩余封禁时间，单位：秒，如果该账号未被封禁，则返回-2 
StpUtil.getDisableTime(10001); 

// 解除封禁
StpUtil.untieDisable(10001); 
```


### 2、分类封禁

有的时候，我们并不需要将整个账号禁掉，而是只禁止其访问部分服务。

假设我们在开发一个电商系统，对于违规账号的处罚，我们设定三种分类封禁：

- 1、封禁评价能力：账号A 因为多次虚假好评，被限制订单评价功能。
- 2、封禁下单能力：账号B 因为多次薅羊毛，被限制下单功能。
- 3、封禁开店能力：账号C 因为店铺销售假货，被限制开店功能。

相比于封禁账号的一刀切处罚，这里的关键点在于：每一项能力封禁的同时，都不会对其它能力造成影响。

也就是说我们需要一种只对部分服务进行限制的能力，对应到代码层面，就是只禁止部分接口的调用。

``` java
// 封禁指定用户评论能力，期限为 1天
StpUtil.disable(10001, "comment", 86400);

// 在评论接口，校验一下，会抛出异常：`DisableServiceException`，使用 e.getService() 可获取业务标识 `comment` 
StpUtil.checkDisable(10001, "comment");

// 在下单时，我们校验一下 下单能力，并不会抛出异常，因为我们没有限制其下单功能
StpUtil.checkDisable(10001, "place-order");

// 现在我们再将其下单能力封禁一下，期限为 7天 
StpUtil.disable(10001, "place-order", 86400 * 7);

// 然后在下单接口，我们添加上校验代码，此时用户便会因为下单能力被封禁而无法下单（代码抛出异常）
StpUtil.checkDisable(10001, "place-order");

// 但是此时，用户如果调用开店功能的话，还是可以通过，因为我们没有限制其开店能力 （除非我们再调用了封禁开店的代码）
StpUtil.checkDisable(10001, "open-shop");
```

通过以上示例，你应该大致可以理解 `业务封禁 -> 业务校验` 的处理步骤。

有关分类封禁的所有方法：
``` java
// 封禁：指定账号的指定服务 
StpUtil.disable(10001, "<业务标识>", 86400); 

// 判断：指定账号的指定服务 是否已被封禁 (true=已被封禁, false=未被封禁) 
StpUtil.isDisable(10001, "<业务标识>"); 

// 校验：指定账号的指定服务 是否已被封禁，如果被封禁则抛出异常 `DisableServiceException`
StpUtil.checkDisable(10001, "<业务标识>"); 

// 获取：指定账号的指定服务 剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
StpUtil.getDisableTime(10001, "<业务标识>"); 

// 解封：指定账号的指定服务
StpUtil.untieDisable(10001, "<业务标识>"); 
```


### 3、阶梯封禁

对于多次违规的用户，我们常常采取阶梯处罚的策略，这种 “阶梯” 一般有两种形式：

- 处罚时间阶梯：首次违规封禁 1 天，第二次封禁 7 天，第三次封禁 30 天，依次顺延……
- 处罚力度阶梯：首次违规消息提醒、第二次禁言禁评论、第三次禁止账号登录，等等……

基于处罚时间的阶梯，我们只需在封禁时 `StpUtil.disable(10001, 86400)` 传入不同的封禁时间即可，下面我们着重探讨一下基于处罚力度的阶梯形式。

假设我们在开发一个论坛系统，对于违规账号的处罚，我们设定三种力度：

- 1、轻度违规：封禁其发帖、评论能力，但允许其点赞、关注等操作。
- 2、中度违规：封禁其发帖、评论、点赞、关注等一切与别人互动的能力，但允许其浏览帖子、浏览评论。
- 3、重度违规：封禁其登录功能，限制一切能力。

解决这种需求的关键在于，我们需要把不同处罚力度，量化成不同的处罚等级，比如上述的 `轻度`、`中度`、`重度` 3 个力度，
我们将其量化为`一级封禁`、`二级封禁`、`三级封禁` 3个等级，数字越大代表封禁力度越高。

然后我们就可以使用阶梯封禁的API，进行鉴权了：

``` java
// 阶梯封禁，参数：封禁账号、封禁级别、封禁时间 
StpUtil.disableLevel(10001, 3, 10000);

// 获取：指定账号封禁的级别 （如果此账号未被封禁则返回 -2）
StpUtil.getDisableLevel(10001);

// 判断：指定账号是否已被封禁到指定级别，返回 true 或 false
StpUtil.isDisableLevel(10001, 3);

// 校验：指定账号是否已被封禁到指定级别，如果已达到此级别（例如已被3级封禁，这里校验是否达到2级），则抛出异常 `DisableServiceException`
StpUtil.checkDisableLevel(10001, 2);
```

注意点：`DisableServiceException` 异常代表当前账号未通过封禁校验，可以：
- 通过 `e.getLevel()` 获取这个账号实际被封禁的等级。
- 通过 `e.getLimitLevel()` 获取这个账号在校验时要求低于的等级。当 `Level >= LimitLevel` 时，框架就会抛出异常。

如果业务足够复杂，我们还可能将 分类封禁 和 阶梯封禁 组合使用：

``` java
// 分类阶梯封禁，参数：封禁账号、封禁服务、封禁级别、封禁时间 
StpUtil.disableLevel(10001, "comment", 3, 10000);

// 获取：指定账号的指定服务 封禁的级别 （如果此账号未被封禁则返回 -2）
StpUtil.getDisableLevel(10001, "comment");

// 判断：指定账号的指定服务 是否已被封禁到指定级别，返回 true 或 false
StpUtil.isDisableLevel(10001, "comment", 3);

// 校验：指定账号的指定服务 是否已被封禁到指定级别（例如 comment服务 已被3级封禁，这里校验是否达到2级），如果已达到此级别，则抛出异常 
StpUtil.checkDisableLevel(10001, "comment", 2);
```



### 4、使用注解完成封禁校验
首先我们需要注册 Sa-Token 全局拦截器（可参考 [注解鉴权](/use/at-check) 章节），然后我们就可以使用以下注解校验账号是否封禁

``` java
// 校验当前账号是否被封禁，如果已被封禁会抛出异常，无法进入方法 
@SaCheckDisable
@PostMapping("send")
public SaResult send() {
	// ... 
	return SaResult.ok(); 
}

// 校验当前账号是否被封禁 comment 服务，如果已被封禁会抛出异常，无法进入方法 
@SaCheckDisable("comment")
@PostMapping("send")
public SaResult send() {
	// ... 
	return SaResult.ok(); 
}

// 校验当前账号是否被封禁 comment、place-order、open-shop 等服务，指定多个值，只要有一个已被封禁，就无法进入方法 
@SaCheckDisable({"comment", "place-order", "open-shop"})
@PostMapping("send")
public SaResult send() {
	// ... 
	return SaResult.ok(); 
}

// 阶梯封禁，校验当前账号封禁等级是否达到5级，如果达到则抛出异常 
@SaCheckDisable(level = 5)
@PostMapping("send")
public SaResult send() {
	// ... 
	return SaResult.ok(); 
}

// 分类封禁 + 阶梯封禁 校验：校验当前账号的 comment 服务，封禁等级是否达到5级，如果达到则抛出异常 
@SaCheckDisable(value = "comment", level = 5)
@PostMapping("send")
public SaResult send() {
	// ... 
	return SaResult.ok(); 
}
```


