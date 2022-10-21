# StpUtil - 鉴权工具类

StpUtil 是 Sa-Token 整体功能的核心，大多数功能均由此工具类提供。

--- 

### 1、常规操作 
``` java
StpUtil.getStpLogic();   // 获取底层 StpLogic 对象。
StpUtil.setStpLogic(newStpLogic);   // 安全的重置底层 StpLogic 引用。
StpUtil.getLoginType();   // 获取账号类型 （例如：login、user、admin、teacher、student等等）。
StpUtil.getTokenName();   // 获取 Token 的名称 
StpUtil.getTokenValue();   // 获取本次请求前端提交的 Token。
StpUtil.getTokenValueNotCut();   // 获取本次请求前端提交的 Token (不裁剪前缀) 。
StpUtil.setTokenValue(tokenValue);   // 在当前会话中写入 Token 值。
StpUtil.setTokenValue(tokenValue, timeout);   // 在当前会话中写入 Token 值，并指定 Cookie 有效期。
StpUtil.getTokenInfo();   // 获取当前 Token 的详细参数。
```


### 2、登录相关  
``` java
StpUtil.login(10001);   // 会话登录
StpUtil.login(10001, "APP");   // 会话登录，并指定设备类型
StpUtil.login(10001, true);   // 会话登录，并指定是否 [记住我]
StpUtil.login(10001, loginModel);   // 会话登录，并指定所有登录参数Model
StpUtil.createLoginSession(10001);   // 创建指定账号id的登录会话，此方法不会将 Token 注入到上下文 
StpUtil.createLoginSession(10001, loginModel);   // 创建指定账号id的登录会话，此方法不会将 Token 注入到上下文 
```

SaLoginModel 配置示例：
``` java
// SaLoginModel 配置登录相关参数  
StpUtil.login(10001, new SaLoginModel()
            .setDevice("PC")                // 此次登录的客户端设备类型, 用于[同端互斥登录]时指定此次登录的设备类型
            .setIsLastingCookie(true)        // 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
            .setTimeout(60 * 60 * 24 * 7)    // 指定此次登录token的有效期, 单位:秒 （如未指定，自动取全局配置的 timeout 值）
            .setToken("xxxx-xxxx-xxxx-xxxx") // 预定此次登录生成的Token 
	        .setExtra("name", "zhangsan")    // Token挂载的扩展参数 （此方法只有在集成jwt插件时才会生效）
            .setIsWriteHeader(false)         // 是否在登录后将 Token 写入到响应头
            );
```


### 3、注销相关
``` java
StpUtil.logout();   // 会话注销 
StpUtil.logout(10001);   // 会话注销，根据账号id
StpUtil.logout(10001, "PC");   // 会话注销，根据账号id 和 设备类型
StpUtil.logoutByTokenValue(token);   // 指定 Token 强制注销
StpUtil.kickout(10001);   // 踢人下线，根据账号id
StpUtil.kickout(10001, "PC");   // 踢人下线，根据账号id 和 设备类型
StpUtil.kickoutByTokenValue(token);   // 踢人下线，根据token
```


### 4、会话查询
``` java
StpUtil.isLogin();   // 当前会话是否已经登录 
StpUtil.checkLogin();   // 检验当前会话是否已经登录，如未登录，则抛出异常
StpUtil.getLoginId();   // 获取当前会话账号id, 如果未登录，则抛出异常 
StpUtil.getLoginId(defaultValue);   // 获取当前会话账号id, 如果未登录，则返回默认值 
StpUtil.getLoginIdDefaultNull();   // 获取当前会话账号id, 如果未登录，则返回null 
StpUtil.getLoginIdAsString();   // 获取当前会话账号id, 并转换为String类型
StpUtil.getLoginIdAsInt();   // 获取当前会话账号id, 并转换为int类型
StpUtil.getLoginIdAsLong();   // 获取当前会话账号id, 并转换为long类型 
StpUtil.getLoginIdByToken(token);   // 获取指定Token对应的账号id，如果未登录，则返回 null 
StpUtil.getExtra(key);   // 获取当前 Token 的扩展信息（此函数只在jwt模式下生效）
StpUtil.getExtra(token, key);   // 获取指定 Token 的扩展信息（此函数只在jwt模式下生效）
```


### 5、Session 相关
``` java
// User-Session 相关 
StpUtil.getSession();   // 获取当前会话的Session，如果Session尚未创建，则新建并返回 
StpUtil.getSession(true);   // 获取当前会话的Session, 如果Session尚未创建，isCreate=是否新建并返回
StpUtil.getSessionByLoginId(10001);   // 获取指定账号id的Session，如果Session尚未创建，则新建并返回
StpUtil.getSessionByLoginId(10001, true);   // 获取指定账号id的Session, 如果Session尚未创建，isCreate=是否新建并返回

// Token-Session 相关 
StpUtil.getTokenSession();   // 获取当前会话的Session，如果Session尚未创建，则新建并返回 
StpUtil.getTokenSessionByToken(token);   // 获取指定Token-Session，如果Session尚未创建，则新建并返回
StpUtil.getAnonTokenSession();   // 获取当前匿名 Token-Session （可在未登录情况下使用的Token-Session）

// 其它
StpUtil.getSessionBySessionId("xxxx-xxxx-xxxx");   // 获取指定key的Session, 如果Session尚未创建，则返回 null
```


### 6、Token有效期相关
``` java
// 临时有效期
StpUtil.getTokenActivityTimeout();   // 获取当前 token [临时过期] 剩余有效时间 (单位: 秒)
StpUtil.checkActivityTimeout();   // 检查当前token 是否已经[临时过期]，如果已经过期则抛出异常  
StpUtil.updateLastActivityToNow();   // 续签当前token：(将 [最后操作时间] 更新为当前时间戳)   

// 长久有效期
StpUtil.getTokenTimeout();   // 获取当前登录者的 token 剩余有效时间 (单位: 秒)
StpUtil.getSessionTimeout();   // 获取当前登录者的 User-Session 剩余有效时间 (单位: 秒)
StpUtil.getTokenSessionTimeout();   // 获取当前 Token-Session 剩余有效时间 (单位: 秒) 
StpUtil.renewTimeout(timeout);   // 对当前 Token 的 timeout 值进行续期 
StpUtil.renewTimeout(token, timeout);   // 对指定 Token 的 timeout 值进行续期 
```


### 7、角色认证
``` java
StpUtil.getRoleList();   // 获取：当前账号的角色集合
StpUtil.getRoleList(10001);   // 获取：指定账号的角色集合 
StpUtil.hasRole(role);   // 判断：当前账号是否拥有指定角色, 返回true或false 
StpUtil.hasRole(loginId, role);   // 判断：指定账号是否含有指定角色标识, 返回true或false 
StpUtil.hasRoleAnd(...roleArray);   // 判断：当前账号是否含有指定角色标识 [指定多个，必须全部验证通过] 
StpUtil.hasRoleOr(...roleArray);   // 判断：当前账号是否含有指定角色标识 [指定多个，只要其一验证通过即可] 
StpUtil.checkRole(role);   // 校验：当前账号是否含有指定角色标识, 如果验证未通过，则抛出异常: NotRoleException 
StpUtil.checkRoleAnd(...roleArray);   // 校验：当前账号是否含有指定角色标识 [指定多个，必须全部验证通过] 
StpUtil.checkRoleOr(...roleArray);   // 校验：当前账号是否含有指定角色标识 [指定多个，只要其一验证通过即可] 
```


### 8、权限认证
``` java
StpUtil.getPermissionList();   // 获取：当前账号的权限集合
StpUtil.getPermissionList(10001);   // 获取：指定账号的权限集合 
StpUtil.hasPermission(permission);   // 判断：当前账号是否拥有指定权限, 返回true或false 
StpUtil.hasPermission(loginId, permission);   // 判断：指定账号是否含有指定权限标识, 返回true或false 
StpUtil.hasPermissionAnd(...permissionArray);   // 判断：当前账号是否含有指定权限标识 [指定多个，必须全部验证通过] 
StpUtil.hasPermissionOr(...permissionArray);   // 判断：当前账号是否含有指定权限标识 [指定多个，只要其一验证通过即可] 
StpUtil.checkPermission(permission);   // 校验：当前账号是否含有指定权限标识, 如果验证未通过，则抛出异常: NotRoleException 
StpUtil.checkPermissionAnd(...permissionArray);   // 校验：当前账号是否含有指定权限标识 [指定多个，必须全部验证通过] 
StpUtil.checkPermissionOr(...permissionArray);   // 校验：当前账号是否含有指定权限标识 [指定多个，只要其一验证通过即可] 
```


### 9、id 反查 Token
``` java
StpUtil.getTokenValueByLoginId(10001);   // 获取指定账号id的tokenValue 
StpUtil.getTokenValueByLoginId(10001, "PC");   // 获取指定账号id指定设备类型端的tokenValue
StpUtil.getTokenValueListByLoginId(10001);   // 获取指定账号id的tokenValue集合 
StpUtil.getTokenValueListByLoginId(10001, "APP");   // 获取指定账号id指定设备类型端的tokenValue 集合 
StpUtil.getLoginDevice();   // 返回当前会话的登录设备类型
```


### 10、会话管理
``` java
StpUtil.searchTokenValue(keyword, start, size, sortType);   // 根据条件查询Token
StpUtil.searchSessionId(keyword, start, size, sortType);   // 根据条件查询SessionId 
StpUtil.searchTokenSessionId(keyword, start, size, sortType);   // 根据条件查询Token专属Session的Id 
```
详细可参考：[会话治理](/up/search-session)


### 11、账号封禁
``` java
StpUtil.disable(10001, 1200);   // 封禁：指定账号 指定时间(单位s)
StpUtil.isDisable(10001);   // 判断：指定账号是否已被封禁 (true=已被封禁, false=未被封禁) 
StpUtil.checkDisable(10001);   // 校验：指定账号是否已被封禁，如果被封禁则抛出异常 `DisableServiceException`
StpUtil.getDisableTime(10001);   // 获取：指定账号剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
StpUtil.untieDisable(loginId);   // 解封：指定账号
```


### 12、分类封禁 (version >= 1.31.0)
``` java
StpUtil.disable(10001, "<业务标识>", 86400);   // 封禁：指定账号的指定服务 指定时间(单位s)
StpUtil.isDisable(10001, "<业务标识>");   // 判断：指定账号的指定服务 是否已被封禁 (true=已被封禁, false=未被封禁) 
StpUtil.checkDisable(10001, "<业务标识>");   // 校验：指定账号的指定服务 是否已被封禁，如果被封禁则抛出异常 `DisableServiceException`
StpUtil.getDisableTime(10001, "<业务标识>");   // 获取：指定账号的指定服务 剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
StpUtil.untieDisable(loginId, "<业务标识>");   // 解封：指定账号的指定服务
```


### 13、阶梯封禁 (version >= 1.31.0)
``` java
StpUtil.disableLevel(10001, "comment", 3, 10000);   // 分类阶梯封禁，参数：封禁账号、封禁服务、封禁级别、封禁时间 
StpUtil.getDisableLevel(10001, "comment");   // 获取：指定账号的指定服务 封禁的级别 （如果此账号未被封禁则返回 -2）
StpUtil.isDisableLevel(10001, "comment", 3);   // 判断：指定账号的指定服务 是否已被封禁到指定级别，返回 true 或 false
StpUtil.checkDisableLevel(10001, "comment", 2);   // 校验：指定账号的指定服务 是否已被封禁到指定级别（例如 comment服务 已被3级封禁，这里校验是否达到2级），如果已达到此级别，则抛出异常 
```


### 14、身份切换
``` java
StpUtil.switchTo(10044);   // 临时切换身份为指定账号id 
StpUtil.endSwitch();   // 结束临时切换身份
StpUtil.isSwitch();   // 当前是否正处于[身份临时切换]中 
StpUtil.switchTo(10044, () -> {});   // 在一个代码段里方法内，临时切换身份为指定账号id
```


### 15、二级认证
``` java
StpUtil.openSafe(safeTime);   // 在当前会话 开启二级认证 
StpUtil.isSafe();   // 当前会话 是否处于二级认证时间内 
StpUtil.checkSafe();   // 检查当前会话是否已通过二级认证，如未通过则抛出异常 
StpUtil.getSafeTime();   // 获取当前会话的二级认证剩余有效时间 (单位: 秒, 返回-2代表尚未通过二级认证)
StpUtil.closeSafe();   // 在当前会话 结束二级认证 
```

