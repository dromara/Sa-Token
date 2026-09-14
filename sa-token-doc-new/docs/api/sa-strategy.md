---
title: "Sa-Token SaStrategy-全局策略"
keywords: "Sa-Token,sa-token,satoken,Sa-Token文档,SaStrategy-全局策略,API手册"
description: "SaStrategy 全局策略 API：从外部数据源动态读取 Sa-Token 配置，代理封装核心逻辑。"
---

# SaStrategy-全局策略

SaStrategy-全局策略，核心逻辑的代理封装。

--- 

### 核心策略

``` java
/**
 * 创建 Token 的策略 
 * <p> 参数 [账号id, 账号类型] 
 */
public BiFunction<Object, String, String> createToken = (loginId, loginType) -> {
	// 默认，还是uuid 
	return "xxxxx-xxxxx-xxxxx-xxxxx";
};

/**
 * 创建 Session 的策略 
 * <p> 参数 [SessionId] 
 */
public Function<String, SaSession> createSession = (sessionId) -> {
	return new SaSession(sessionId);
};

/**
 * 反序列化 SaSession 时默认指定的类型
 */
public Class<? extends SaSession> sessionClassType = SaSession.class;

/**
 * 判断：集合中是否包含指定元素（模糊匹配） 
 * <p> 参数 [集合, 元素] 
 */
public BiFunction<List<String>, String, Boolean> hasElement = (list, element) -> {
	return false;
};


/**
 * 生成唯一式 token 的算法
 * <p> 参数：元素名称, 最大尝试次数, 创建 token 函数, 检查 token 函数 </p>
 */
public SaGenerateUniqueTokenFunction generateUniqueToken = (elementName, maxTryTimes, createTokenFunction, checkTokenFunction) -> {
	// ...
	return "xxxxxx";
};

/**
 * 是否自动续期，每次续期前都会执行，可以加入动态判断逻辑
 * <p> 参数 当前 stpLogic 实例对象
 * <p> 返回 true 自动续期 false 不自动续期
 */
public Function<StpLogic, Boolean> autoRenew = (stpLogic) -> {
	return stpLogic.getConfigOrGlobal().getAutoRenew();
};

/**
 * 创建 StpLogic 的算法
 * <p>  参数：账号体系标识  </p>
 * <p>  返回：创建好的 StpLogic 对象  </p>
 */
public SaCreateStpLogicFunction createStpLogic = (loginType) -> {
	return new StpLogic(loginType);
};

/**
 * 路由匹配策略
 * <p>  参数：pattern, path  </p>
 * <p>  返回：是否匹配  </p>
 */
public SaRouteMatchFunction routeMatcher = (pattern, path) -> {
	return true;
};

/**
 * CORS 策略处理函数
 * <p>  参数：请求包装对象, 响应包装对象, 数据读写对象  </p>
 */
public SaCorsHandleFunction corsHandle = (req, res, sto) -> {

};

/**
 * 获取 SaTokenConfig 的策略
 * <p>  默认 null，表示使用框架内置逻辑（先读 SaManager.config，为空时自动读取 sa-token.properties）  </p>
 * <p>  赋值后，每次调用 SaManager.getConfig() 时都会执行此策略并直接返回其结果  </p>
 * <p>  注意：策略内请勿再调用 SaManager.getConfig()，否则会陷入无限递归  </p>
 */
public SaGetSaTokenConfigFunction getSaTokenConfig = null;
```


### 重写策略（set 连缀风格）

``` java
SaStrategy.instance.setCreateToken(createToken);   // 重写创建 Token 的策略
SaStrategy.instance.setCreateSession(createSession);   // 重写创建 Session 的策略
SaStrategy.instance.setHasElement(hasElement);   // 重写集合模糊匹配策略
SaStrategy.instance.setGenerateUniqueToken(generateUniqueToken);   // 重写生成唯一 token 的策略
SaStrategy.instance.setCreateStpLogic(createStpLogic);   // 重写创建 StpLogic 的策略
SaStrategy.instance.setAutoRenew(autoRenew);   // 重写是否自动续期策略
SaStrategy.instance.setGetSaTokenConfig(getSaTokenConfig);   // 重写获取 SaTokenConfig 的策略
```

#### getSaTokenConfig 使用示例

适用于需要从数据库等外部数据源动态读取配置的场景：

``` java
SaStrategy.instance.setGetSaTokenConfig(() -> {
	// 从数据库读取配置，自行做好缓存
	SaTokenConfig config = new SaTokenConfig();
	config.setTokenName("satoken");
	config.setTimeout(30 * 24 * 60 * 60);
	return config;
});
```

注意：
- 策略内**不要**调用 `SaManager.getConfig()`，否则会无限递归。
- 启用后，每次 `SaManager.getConfig()` 都会走此策略；请做好缓存处理。
