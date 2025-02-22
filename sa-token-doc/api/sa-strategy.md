# SaStrategy-全局策略

SaStrategy-全局策略，核心逻辑的代理封装。

--- 

### 所有策略

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
 * 判断：集合中是否包含指定元素（模糊匹配） 
 * <p> 参数 [集合, 元素] 
 */
public BiFunction<List<String>, String, Boolean> hasElement = (list, element) -> {
	return false;
};

/**
 * 对一个 [Method] 对象进行注解校验 （注解鉴权内部实现） 
 * <p> 参数 [Method句柄] 
 */
public Consumer<Method> checkMethodAnnotation = (method) -> {
	// ... 
};

/**
 * 对一个 [元素] 对象进行注解校验 （注解鉴权内部实现） 
 * <p> 参数 [element元素] 
 */
public Consumer<AnnotatedElement> checkElementAnnotation = (target) -> {
	// ... 
};

/**
 * 从元素上获取注解（注解鉴权内部实现） 
 * <p> 参数 [element元素，要获取的注解类型] 
 */
public BiFunction<AnnotatedElement, Class<? extends Annotation> , Annotation> getAnnotation = (element, annotationClass)->{
	// 默认使用jdk的注解处理器 
	return element.getAnnotation(annotationClass);
};

/**
 * 拼接两个url 
 * <p> 例如：url1=http://domain.cn，url2=/sso/auth，则返回：http://domain.cn/sso/auth
 * <p> 参数 [第一个url, 第二个url] 
 */
public BiFunction<String, String, String> spliceTwoUrl = (url1, url2) -> {
	return xxx;
};

/**
 * 是否自动续期，每次续期前都会执行，可以加入动态判断逻辑
 * <p> 参数 当前 stpLogic 实例对象
 * <p> 返回 true 自动续期 false 不自动续期
 */
public Function<StpLogic, Boolean> autoRenew = (stpLogic) -> {
	return stpLogic.getConfigOrGlobal().getAutoRenew();
};
```






