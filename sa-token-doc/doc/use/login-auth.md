# 登录认证
--- 


### 核心思想

所谓登录认证，说白了就是限制某些接口只有登录后才能访问（如：查询我的账号资料） <br>
那么判断一个会话是否登录的依据是什么？当然是登录成功后框架给你做个标记！然后在需要鉴权的接口里检查此标记，有标记者视为已登录，无标记者视为未登录！


### 登录与注销
根据以上思路，我们很容易想到以下api：

``` java
// 标记当前会话登录的账号id 
// 建议的参数类型：long | int | String， 不可以传入复杂类型，如：User、Admin等等
StpUtil.setLoginId(Object loginId);	

// 当前会话注销登录
StpUtil.logout();

// 获取当前会话是否已经登录，返回true=已登录，false=未登录
StpUtil.isLogin();

// 检验当前会话是否已经登录, 如果未登录，则抛出异常：`NotLoginException`
StpUtil.checkLogin()
```

扩展：`NotLoginException` 对象可通过 `getLoginKey()` 方法获取具体是哪个 `StpLogic` 抛出的异常 <br>
扩展：`NotLoginException` 对象可通过 `getType()` 方法获取具体的场景值，详细参考章节：[未登录场景值](/fun/not-login-scene)


### 会话查询
``` java
// 获取当前会话登录id, 如果未登录，则抛出异常：`NotLoginException`
StpUtil.getLoginId();

// 类似查询API还有：
StpUtil.getLoginIdAsString();    // 获取当前会话登录id, 并转化为`String`类型
StpUtil.getLoginIdAsInt();       // 获取当前会话登录id, 并转化为`int`类型
StpUtil.getLoginIdAsLong();      // 获取当前会话登录id, 并转化为`long`类型

// ---------- 指定未登录情形下返回的默认值 ----------

// 获取当前会话登录id, 如果未登录，则返回null 
StpUtil.getLoginIdDefaultNull();

// 获取当前会话登录id, 如果未登录，则返回默认值 （`defaultValue`可以为任意类型）
StpUtil.getLoginId(T defaultValue);
```


### 其它API
``` java
// 获取指定token对应的登录id，如果未登录，则返回 null
StpUtil.getLoginIdByToken(String tokenValue);

// 获取当前`StpLogic`的token名称
StpUtil.getTokenName();

// 获取当前会话的token值
StpUtil.getTokenValue();

// 获取当前会话的token信息参数
StpUtil.getTokenInfo();
```

?> 有关TokenInfo参数详解，请参考：[参考：TokenInfo参数详解](/fun/token-info)	


