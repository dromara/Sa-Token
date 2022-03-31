# Token有效期详解

<!-- 本篇介绍Token有效期的详细用法 -->

Sa-Token 提供两种Token自动过期策略，分别是`timeout`与`activity-timeout`，其详细用法如下：


### timeout
1. `timeout`代表Token的长久有效期，单位/秒，例如将其配置为 2592000 (30天)，代表在30天后，Token必定过期，无法继续使用
2. `timeout`~~无法续签，想要继续使用必须重新登录~~。v1.29.0+ 版本新增续期方法：`StpUtil.renewTimeout(100)`。
3. `timeout`的值配置为-1后，代表永久有效，不会过期


### activity-timeout
1. `activity-timeout`代表临时有效期，单位/秒，例如将其配置为 1800 (30分钟)，代表用户如果30分钟无操作，则此Token会立即过期
2. 如果在30分钟内用户有操作，则会再次续签30分钟，用户如果一直操作则会一直续签，直到连续30分钟无操作，Token才会过期
3. `activity-timeout`的值配置为-1后，代表永久有效，不会过期，此时也无需频繁续签


### 关于activity-timeout的续签
如果`activity-timeout`配置了大于零的值，Sa-Token 会在登录时开始计时，在每次直接或间接调用`getLoginId()`时进行一次过期检查与续签操作。
此时会有两种情况：
1. 一种是会话无操作时间太长，Token已经过期，此时框架会抛出`NotLoginException`异常(场景值=-3)，
2. 另一种则是会话在`activity-timeout`有效期内通过检查，此时Token可以成功续签 


### 我可以手动续签吗？
**可以！**
如果框架的自动续签算法无法满足您的业务需求，你可以进行手动续签，Sa-Token 提供两个API供你操作：
1. `StpUtil.checkActivityTimeout()`: 检查当前Token 是否已经[临时过期]，如果已经过期则抛出异常
2. `StpUtil.updateLastActivityToNow()`: 续签当前Token：(将 [最后操作时间] 更新为当前时间戳) 

注意：在手动续签时，即使Token已经 [临时过期] 也可续签成功，如果此场景下需要提示续签失败，可采用先检查再续签的形式保证Token有效性 

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

### StpUtil 类中哪些公开方法支持临时有效期自动续签? 
> 间接调用过 StpUtil.updateLastActivityToNow() 方法

| 支持自动续签的方法 |
|---|
| StpUtil.checkLogin() |
| StpUtil.getLoginId() |
| StpUtil.getLoginIdAsInt() |
| StpUtil.getLoginIdAsString() |
| StpUtil.getLoginIdAsLong() |
|---|
| StpUtil.getSession() |
| StpUtil.getSession() |
| StpUtil.getTokenSession() |
|---|
| StpUtil.getRoleList() |
| StpUtil.hasRole() |
| StpUtil.hasRoleAnd() |
| StpUtil.hasRoleOr() |
| StpUtil.checkRole() |
| StpUtil.checkRoleAnd() |
| StpUtil.checkRoleOr() |
|---|
| StpUtil.getPermissionList() |
| StpUtil.hasPermission() |
| StpUtil.hasPermissionAnd() |
| StpUtil.hasPermissionOr() |
| StpUtil.checkPermission() |
| StpUtil.checkPermissionAnd() |
| StpUtil.checkPermissionOr() |
|---|
| StpUtil.openSafe() |
| StpUtil.isSafe() |
| StpUtil.checkSafe() |
| StpUtil.getSafeTime() |
| StpUtil.closeSafe() |

> 以下注解都间接调用过 getLoginId() 方法

| 支持自动续签的注解 |
|---|
| @SaCheckLogin      |
| @SaCheckRole       |
| @SaCheckPermission |
| @SaCheckSafe       |
