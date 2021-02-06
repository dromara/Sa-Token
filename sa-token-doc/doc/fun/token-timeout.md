# token有效期详解

<!-- 本篇介绍token有效期的详细用法 -->

`sa-token` 提供两种token自动过期策略，分别是`timeout`与`activity-timeout`，其详细用法如下：


### timeout
1. `timeout`代表token的长久有效期，单位/秒，例如将其配置为`2592000`(30天)，代表在30天后，token必定过期，无法继续使用
2. `timeout`无法续签，想要继续使用必须重新登录
3. `timeout`的值配置为-1后，代表永久有效，不会过期


### activity-timeout
1. `activity-timeout`代表临时有效期，单位/秒，例如将其配置为`1800`(30分钟)，代表用户如果30分钟无操作，则此token会立即过期
2. 如果在30分钟内用户有操作，则会再次续签30分钟，用户如果一直操作则会一直续签，直到连续30分钟无操作，token才会过期
3. `activity-timeout`的值配置为-1后，代表永久有效，不会过期，此时也无需频繁续签


### 关于activity-timeout的续签
如果`activity-timeout`配置了大于零的值，`sa-token`会在登录时开始计时，在每次直接或间接调用`getLoginId()`时进行一次过期检查与续签操作。
此时会有两种情况：
1. 一种是会话无操作时间太长，token已经过期，此时框架会抛出`NotLoginException`异常(场景值=-3)，
2. 另一种则是会话在`activity-timeout`有效期内通过检查，此时token可以成功续签 


### 我可以手动续签吗？
**可以！**
如果框架的自动续签算法无法满足您的业务需求，你可以进行手动续签，`sa-token`提供两个API供你操作：
1. `StpUtil.checkActivityTimeout()`: 检查当前token 是否已经[临时过期]，如果已经过期则抛出异常
2. `StpUtil.updateLastActivityToNow()`: 续签当前token：(将 [最后操作时间] 更新为当前时间戳) 

注意：在手动续签时，即时token已经 [临时过期] 也可续签成功，如果此场景下需要提示续签失败，可采用先检查再续签的形式保证token有效性 

例如以下代码：
``` java
	// 先检查是否已过期
	StpUtil.checkActivityTimeout();
	// 检查通过后继续续签
	StpUtil.updateLastActivityToNow();
```

同时，你还可以关闭框架的自动续签（在配置文件中配置 `autoRenew=false` ），此时续签操作完全由开发者控制，框架不再自动进行任何续签操作


### timeout与activity-timeout可以同时使用吗？
**可以同时使用！** 
两者的认证逻辑彼此独立，互不干扰，可以同时使用。

