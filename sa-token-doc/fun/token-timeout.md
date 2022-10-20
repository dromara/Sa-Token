# Token有效期详解

<!-- 本篇介绍Token有效期的详细用法 -->

Sa-Token 提供两种Token自动过期策略，分别是`timeout`与`activity-timeout`，配置方法如下：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
    # Token 有效期，单位：秒，默认30天, -1代表永不过期 
    timeout: 2592000
    # Token 临时有效期 (指定时间内无操作就视为 Token 过期) 单位: 秒，-1代表不设限 
    activity-timeout: -1
```
<!------------- tab:properties 风格  ------------->
``` properties
# Token 有效期，单位：秒，默认30天, -1代表永不过期 
sa-token.timeout=2592000
# Token 临时有效期 (指定时间内无操作就视为 Token 过期) 单位: 秒，-1代表不设限 
sa-token.activity-timeout=-1
```
<!---------------------------- tabs:end ---------------------------->


两者的区别，可以通过下面的例子体现：

> 1. 假设你到银行要存钱，首先就要办理一张卡 （要访问系统接口先登录）。
> 2. 银行为你颁发一张储蓄卡（系统为你颁发一个Token），以后每次存取钱都要带上这张卡（后续每次访问系统都要提交 Token）。
> 3. 银行为这张卡设定两个过期时间：
> 	- 第一个是 `timeout`，代表这张卡的长久有效期，就是指这张卡最长能用多久，假设 `timeout=3年`，那么3年后此卡将被银行删除，想要继续来银行办理业务必须重新办卡（Token 过期后想要访问系统必须重新登录）。
> 	- 第二个就是 `activity-timeout`，代表这张卡的临时有效期，就是指这张卡必须每隔多久来银行一次，假设 `activity-timeout=1月` ，你如果超过1月不来办一次业务，银行就将你的卡冻结，列为长期不动户（Token 长期不访问系统，被冻结，但不会被删除）。
> 4. 两个过期策略可以单独配置，也可以同时配置，只要有其中一个有效期超出了范围，这张卡就会变得不可用（两个有效期只要有一个过期了，Token就无法成功访问系统了）。

下面是对两个过期策略的详细解释：

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

如果你需要给其它 Token 续签：

``` java
// 为指定 Token 续签 
StpUtil.stpLogic.updateLastActivityToNow(tokenValue);
```


### timeout与activity-timeout可以同时使用吗？
**可以同时使用！** 
两者的认证逻辑彼此独立，互不干扰，可以同时使用。

### StpUtil 类中哪些公开方法支持临时有效期自动续签? 
> 直接或间接获取了当前用户id的方法 （间接调用过 StpLogic.getLoginId() 方法）

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
