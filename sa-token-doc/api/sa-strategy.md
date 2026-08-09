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
```


### 重写策略（set 连缀风格）

``` java
SaStrategy.instance.setCreateToken(createToken);   // 重写创建 Token 的策略
SaStrategy.instance.setCreateSession(createSession);   // 重写创建 Session 的策略
SaStrategy.instance.setHasElement(hasElement);   // 重写集合模糊匹配策略
SaStrategy.instance.setGenerateUniqueToken(generateUniqueToken);   // 重写生成唯一 token 的策略
SaStrategy.instance.setCreateStpLogic(createStpLogic);   // 重写创建 StpLogic 的策略
SaStrategy.instance.setAutoRenew(autoRenew);   // 重写是否自动续期策略
```
