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


### 注解操作相关策略 

``` java
/**
 * 对一个 [Method] 对象进行注解校验 （注解鉴权内部实现）
 * <p>  参数：Method句柄  </p>
 * <p>  返回：无  </p>
 */
public SaCheckMethodAnnotationFunction checkMethodAnnotation = (method) -> {
	// ... 
};

/**
 * 对一个 [Element] 对象进行注解校验 （注解鉴权内部实现）
 * <p>  参数：element元素  </p>
 * <p>  返回：无  </p>
 */
@SuppressWarnings("unchecked")
public SaCheckElementAnnotationFunction checkElementAnnotation = (element) -> {
	// ... 
};

/**
 * 从元素上获取注解
 * <p>  参数：element元素，要获取的注解类型  </p>
 * <p>  返回：注解对象  </p>
 */
public SaGetAnnotationFunction getAnnotation = (element, annotationClass)->{
	return element.getAnnotation(annotationClass);
};

/**
 * 判断一个 Method 或其所属 Class 是否包含指定注解
 * <p>  参数：Method、注解  </p>
 * <p>  返回：是否包含  </p>
 */
public SaIsAnnotationPresentFunction isAnnotationPresent = (method, annotationClass) -> {
	// ...
	return false;
};

/**
 * SaCheckELRootMap 扩展函数
 * <p>  参数：SaCheckELRootMap 对象 </p>
 */
public SaCheckELRootMapExtendFunction checkELRootMapExtendFunction = rootMap -> {
	// 默认不做任何处理
};
```



### 防火墙相关策略 

``` java
/**
 * 防火墙校验函数
 * <p> 参数：请求对象、响应对象、预留扩展参数 </p>
 */
public SaFirewallCheckFunction check = (req, res, extArg) -> {
	// ... 
};

/**
 * 自定义当请求 path 校验不通过时地处理方案 
 * <p> 参数：防火墙校验异常、请求对象、响应对象、预留扩展参数 </p>
 */
SaFirewallStrategy.instance.checkFailHandle = (e, req, res, extArg) -> {
	// 自定义处理逻辑 ...
};
```

参考：[防火墙](/fun/firewall)
